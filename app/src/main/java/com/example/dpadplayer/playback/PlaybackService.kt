package com.example.dpadplayer.playback

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.preference.PreferenceManager
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.media.app.NotificationCompat.MediaStyle
import androidx.media.session.MediaButtonReceiver
import com.example.dpadplayer.MainActivity
import com.example.dpadplayer.MediaStoreScanner
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import android.os.Handler
import android.os.Looper

class PlaybackService : Service() {

    companion object {
        const val COMMAND_PLAY        = "com.example.dpadplayer.action.PLAY"
        const val COMMAND_PAUSE       = "com.example.dpadplayer.action.PAUSE"
        const val COMMAND_NEXT        = "com.example.dpadplayer.action.NEXT"
        const val COMMAND_PREV        = "com.example.dpadplayer.action.PREV"
        const val COMMAND_SEEK_FWD    = "com.example.dpadplayer.action.SEEK_FWD"
        const val COMMAND_SEEK_BWD    = "com.example.dpadplayer.action.SEEK_BWD"
        const val COMMAND_SEEK_TO     = "com.example.dpadplayer.action.SEEK_TO"
        const val COMMAND_PLAY_INDEX  = "com.example.dpadplayer.action.PLAY_INDEX"
        const val EXTRA_INDEX         = "index"
        const val EXTRA_POSITION      = "position"

        const val REPEAT_OFF = 0
        const val REPEAT_ALL = 1
        const val REPEAT_ONE = 2

        const val NOTIF_CHANNEL_ID = "dpad_player_channel"
        const val NOTIF_ID = 1

        private val MEDIA_SESSION_ACTIONS =
            PlaybackStateCompat.ACTION_PLAY or
            PlaybackStateCompat.ACTION_PAUSE or
            PlaybackStateCompat.ACTION_PLAY_PAUSE or
            PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
            PlaybackStateCompat.ACTION_STOP or
            PlaybackStateCompat.ACTION_SEEK_TO
    }

    inner class LocalBinder : Binder() {
        fun getService(): PlaybackService = this@PlaybackService
    }
    private val binder = LocalBinder()

    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var audioManager: AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null

    val tracks = mutableListOf<Track>()
    var currentIndex = 0
        private set

    // Shuffle / repeat state
    var shuffleOn  = false
    var repeatMode = REPEAT_OFF  // 0=off, 1=all, 2=one
    // Shuffled play order (indices into tracks)
    private val shuffleOrder = mutableListOf<Int>()
    private var shufflePos   = 0  // position within shuffleOrder

    // Callbacks for bound UI
    var onTrackChanged: ((Int) -> Unit)? = null
    var onPlaybackStateChanged: ((Boolean) -> Unit)? = null
    var onPositionChanged: ((Long) -> Unit)? = null
    var onShuffleChanged: ((Boolean) -> Unit)? = null
    var onRepeatChanged: ((Int) -> Unit)? = null

    // Seek-bar position polling
    private val mainHandler = Handler(Looper.getMainLooper())
    private val positionRunnable: Runnable = object : Runnable {
        override fun run() {
            onPositionChanged?.invoke(player.currentPosition)
            mainHandler.postDelayed(this, 500)
        }
    }

    // ─── Lifecycle ────────────────────────────────────────────────────────────

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        player = ExoPlayer.Builder(this).build()

        mediaSession = MediaSessionCompat(this, "dpad_player").apply {
            setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS or
                     MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS)
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay()              { requestAudioFocusAndPlay() }
                override fun onPause()             { pausePlayback() }
                override fun onSkipToNext()        { next() }
                override fun onSkipToPrevious()    { prev() }
                override fun onStop()              { stopSelf() }
                override fun onSeekTo(pos: Long)   { player.seekTo(pos); updatePlaybackState() }
            })
            isActive = true
        }

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updatePlaybackState()
                if (isPlaying) {
                    tryStartForeground()
                    startPositionPolling()
                } else {
                    ServiceCompat.stopForeground(this@PlaybackService, ServiceCompat.STOP_FOREGROUND_DETACH)
                    updateNotification()
                    stopPositionPolling()
                }
                onPlaybackStateChanged?.invoke(isPlaying)
            }
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) next()
            }
        })

        // Tracks are pushed in by MainActivity after binding; nothing to load here.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MediaButtonReceiver.handleIntent(mediaSession, intent)
        when (intent?.action) {
            COMMAND_PLAY       -> requestAudioFocusAndPlay()
            COMMAND_PAUSE      -> pausePlayback()
            COMMAND_NEXT       -> next()
            COMMAND_PREV       -> prev()
            COMMAND_SEEK_FWD   -> seekBy(configuredSeekStepMs())
            COMMAND_SEEK_BWD   -> seekBy(-configuredSeekStepMs())
            COMMAND_SEEK_TO    -> { intent.getLongExtra(EXTRA_POSITION, 0).let { player.seekTo(it); updatePlaybackState() } }
            COMMAND_PLAY_INDEX -> {
                val idx = intent.getIntExtra(EXTRA_INDEX, 0)
                prepareAndPlay(idx)
            }
            else -> { /* service started cold — ensure notification */ }
        }
        // Ensure we stay in foreground
        tryStartForeground()
        return START_STICKY
    }

    private fun configuredSeekStepMs(): Long {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        return prefs.getString("seek_step", "15000")?.toLongOrNull() ?: 15_000L
    }

    private fun seekBy(deltaMs: Long) {
        val duration = player.duration.takeIf { it >= 0 } ?: Long.MAX_VALUE
        val target = (player.currentPosition + deltaMs).coerceIn(0L, duration)
        player.seekTo(target)
        updatePlaybackState()
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onDestroy() {
        stopPositionPolling()
        abandonAudioFocus()
        mediaSession.isActive = false
        mediaSession.release()
        player.release()
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }

    // ─── Playback control ─────────────────────────────────────────────────────

    private fun requestAudioFocusAndPlay() {
        val gained = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val req = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build())
                .setWillPauseWhenDucked(true)
                .setOnAudioFocusChangeListener { focus ->
                    when (focus) {
                        AudioManager.AUDIOFOCUS_LOSS,
                        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> pausePlayback()
                        AudioManager.AUDIOFOCUS_GAIN -> if (!player.isPlaying) player.play()
                    }
                }.build().also { audioFocusRequest = it }
            audioManager.requestAudioFocus(req) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN) ==
                    AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }

        if (gained) {
            if (tracks.isEmpty()) return
            if (!player.isCurrentMediaItemSeekable && player.playbackState == Player.STATE_IDLE) {
                prepareAndPlay(currentIndex)
            } else {
                player.play()
            }
        }
    }

    private fun pausePlayback() {
        player.pause()
        abandonAudioFocus()
    }

    private fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(null)
        }
    }

    fun prepareAndPlay(index: Int) {
        if (index < 0 || index >= tracks.size) { isTransitioning = false; return }
        currentIndex = index
        player.clearMediaItems()
        player.setMediaItem(MediaItem.fromUri(tracks[index].uri))
        player.prepare()
        player.play()
        updateMetadata(index)
        updatePlaybackState()
        onTrackChanged?.invoke(index)
        isTransitioning = false
    }

    private var isTransitioning = false

    fun next() {
        if (isTransitioning) return
        isTransitioning = true
        if (tracks.isEmpty()) { isTransitioning = false; return }
        when {
            repeatMode == REPEAT_ONE -> { player.seekTo(0); player.play(); return }
            shuffleOn -> {
                shufflePos = (shufflePos + 1) % shuffleOrder.size
                prepareAndPlay(shuffleOrder[shufflePos])
            }
            repeatMode == REPEAT_ALL -> prepareAndPlay((currentIndex + 1) % tracks.size)
            currentIndex < tracks.size - 1 -> prepareAndPlay(currentIndex + 1)
            else -> { player.seekTo(0); player.pause() } // end of list, stop
        }
    }

    fun prev() {
        if (isTransitioning) return
        isTransitioning = true
        if (tracks.isEmpty()) { isTransitioning = false; return }
        if (player.currentPosition > 3_000) {
            player.seekTo(0); updatePlaybackState(); isTransitioning = false; return
        }
        when {
            shuffleOn -> {
                shufflePos = ((shufflePos - 1) + shuffleOrder.size) % shuffleOrder.size
                prepareAndPlay(shuffleOrder[shufflePos])
            }
            else -> prepareAndPlay(if (currentIndex - 1 < 0) tracks.size - 1 else currentIndex - 1)
        }
    }

    fun toggleShuffle() {
        shuffleOn = !shuffleOn
        if (shuffleOn) buildShuffleOrder()
        onShuffleChanged?.invoke(shuffleOn)
    }

    fun playNextTrack(track: Track) {
        if (tracks.isEmpty()) {
            tracks.add(track)
            prepareAndPlay(0)
            return
        }
        val insertIndex = currentIndex + 1
        tracks.add(insertIndex, track)
        if (shuffleOn) {
            shuffleOrder.add(shufflePos + 1, insertIndex)
            for (i in 0 until shuffleOrder.size) {
                if (i != (shufflePos + 1) && shuffleOrder[i] >= insertIndex) {
                    shuffleOrder[i]++
                }
            }
        }
    }

    fun enqueueTrack(track: Track) {
        if (tracks.isEmpty()) {
            tracks.add(track)
            prepareAndPlay(0)
            return
        }
        tracks.add(track)
        if (shuffleOn) {
            shuffleOrder.add(tracks.size - 1)
        }
    }

    fun cycleRepeat() {
        repeatMode = (repeatMode + 1) % 3
        onRepeatChanged?.invoke(repeatMode)
    }

    private fun buildShuffleOrder() {
        shuffleOrder.clear()
        shuffleOrder.addAll((tracks.indices).toMutableList().also { it.shuffle() })
        shufflePos = shuffleOrder.indexOf(currentIndex).coerceAtLeast(0)
    }

    fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
        updatePlaybackState()
    }

    val isPlaying get() = player.isPlaying
    val currentPosition get() = player.currentPosition
    val duration get() = if (tracks.isNotEmpty()) tracks[currentIndex].duration else 0L

    // ─── MediaSession state ───────────────────────────────────────────────────

    private fun updatePlaybackState() {
        val state = if (player.isPlaying) PlaybackStateCompat.STATE_PLAYING
                    else PlaybackStateCompat.STATE_PAUSED
        mediaSession.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setActions(MEDIA_SESSION_ACTIONS)
                .setState(state, player.currentPosition, player.playbackParameters.speed)
                .build()
        )
    }

    private fun updateMetadata(index: Int) {
        if (index < 0 || index >= tracks.size) return
        val t = tracks[index]
        mediaSession.setMetadata(
            MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE,  t.title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, t.artist)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM,  t.album)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, t.duration)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, t.albumArtUri.toString())
                .build()
        )
    }

    // ─── Notification ─────────────────────────────────────────────────────────

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(
                NOTIF_CHANNEL_ID, "DPad Player",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                setShowBadge(false)
                enableLights(false)
                enableVibration(false)
            }
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(ch)
        }
    }

    private fun buildNotification(): android.app.Notification {
        createNotificationChannel()
        val t = if (tracks.isNotEmpty()) tracks[currentIndex] else null

        val openIntent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val prevAction = NotificationCompat.Action.Builder(
            android.R.drawable.ic_media_previous, "Previous",
            MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
        ).build()

        val playPauseAction = if (player.isPlaying) {
            NotificationCompat.Action.Builder(
                android.R.drawable.ic_media_pause, "Pause",
                MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PAUSE)
            ).build()
        } else {
            NotificationCompat.Action.Builder(
                android.R.drawable.ic_media_play, "Play",
                MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY)
            ).build()
        }

        val nextAction = NotificationCompat.Action.Builder(
            android.R.drawable.ic_media_next, "Next",
            MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)
        ).build()

        return NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle(t?.title ?: "DPad Player")
            .setContentText(t?.let { "${it.artist} — ${it.album}" } ?: "")
            .setContentIntent(openIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSilent(true)
            .setShowWhen(false)
            .setOngoing(player.isPlaying)
            .addAction(prevAction)
            .addAction(playPauseAction)
            .addAction(nextAction)
            .setStyle(
                MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .build()
    }

    private fun tryStartForeground() {
        try {
            startForeground(NOTIF_ID, buildNotification())
        } catch (e: Exception) {
            // ForegroundServiceStartNotAllowedException on API 31+ if called from background
            e.printStackTrace()
        }
    }

    private fun updateNotification() {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIF_ID, buildNotification())
    }

    // ─── Position polling ─────────────────────────────────────────────────────

    private fun startPositionPolling() {
        stopPositionPolling()
        mainHandler.post(positionRunnable)
    }

    private fun stopPositionPolling() {
        mainHandler.removeCallbacks(positionRunnable)
    }
}
