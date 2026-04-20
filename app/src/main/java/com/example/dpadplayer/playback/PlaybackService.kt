package com.example.dpadplayer.playback

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import androidx.media.session.MediaButtonReceiver
import com.example.dpadplayer.MainActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import java.io.File

class PlaybackService : Service() {

    companion object {
        const val TAG = "PlaybackService"

        const val COMMAND_PLAY = "com.example.dpadplayer.action.PLAY"
        const val COMMAND_PAUSE = "com.example.dpadplayer.action.PAUSE"
        const val COMMAND_NEXT = "com.example.dpadplayer.action.NEXT"
        const val COMMAND_PREV = "com.example.dpadplayer.action.PREV"
        const val COMMAND_SEEK_FORWARD = "com.example.dpadplayer.action.SEEK_FWD"
        const val COMMAND_SEEK_BACKWARD = "com.example.dpadplayer.action.SEEK_BWD"

        const val NOTIF_CHANNEL_ID = "dpad_player_channel"
        const val NOTIF_ID = 1
    }

    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSessionCompat
    private val tracks = mutableListOf<Track>()
    private var currentIndex = 0

    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(this).build()

        mediaSession = MediaSessionCompat(this, "dpad_player_session")
        mediaSession.setCallback(object : MediaSessionCompat.Callback() {
            override fun onPlay() { startPlayback() }
            override fun onPause() { pausePlayback() }
            override fun onSkipToNext() { next() }
            override fun onSkipToPrevious() { prev() }
        })

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                showNotification(isPlaying)
            }
        })

        scanStorageForMp3()
        if (tracks.isNotEmpty()) prepareAndPlay(currentIndex)
    }

    private fun scanStorageForMp3() {
        val external = getExternalFilesDir(null)?.parentFile?.parentFile ?: filesDir
        scanDirForMp3(external)
    }

    private fun scanDirForMp3(dir: File?) {
        if (dir == null || !dir.exists()) return
        dir.listFiles()?.forEach { f ->
            if (f.isDirectory) scanDirForMp3(f)
            else if (f.isFile && f.name.lowercase().endsWith(".mp3")) {
                tracks.add(Track(uri = f.absolutePath, title = f.nameWithoutExtension, artist = ""))
            }
        }
    }

    private fun prepareAndPlay(index: Int) {
        if (index < 0 || index >= tracks.size) return
        player.clearMediaItems()
        val item = MediaItem.fromUri(tracks[index].uri)
        player.setMediaItem(item)
        player.prepare()
        player.play()
        updateMetadataForIndex(index)
    }

    private fun updateMetadataForIndex(index: Int) {
        val t = tracks[index]
        val meta = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, t.title)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, t.artist)
            .build()
        mediaSession.setMetadata(meta)
    }

    private fun startPlayback() { if (!player.isPlaying) player.play() }
    private fun pausePlayback() { if (player.isPlaying) player.pause() }

    private fun next() {
        if (tracks.isEmpty()) return
        currentIndex = (currentIndex + 1) % tracks.size
        prepareAndPlay(currentIndex)
    }

    private fun prev() {
        if (tracks.isEmpty()) return
        currentIndex = if (currentIndex - 1 < 0) tracks.size - 1 else currentIndex - 1
        prepareAndPlay(currentIndex)
    }

    private fun showNotification(isPlaying: Boolean) {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(NOTIF_CHANNEL_ID, "DPad Player", NotificationManager.IMPORTANCE_LOW)
            nm.createNotificationChannel(ch)
        }

        val openIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseAction = if (isPlaying) {
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

        val notif = NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
            .setContentTitle(mediaSession.controller.metadata?.description?.title ?: "DPad Player")
            .setContentText(mediaSession.controller.metadata?.description?.subtitle)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(openIntent)
            .addAction(
                NotificationCompat.Action.Builder(
                    android.R.drawable.ic_media_previous, "Prev",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
                ).build()
            )
            .addAction(playPauseAction)
            .addAction(
                NotificationCompat.Action.Builder(
                    android.R.drawable.ic_media_next, "Next",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)
                ).build()
            )
            .setStyle(MediaStyle().setMediaSession(mediaSession.sessionToken))
            .build()

        startForeground(NOTIF_ID, notif)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        when (action) {
            COMMAND_PLAY -> startPlayback()
            COMMAND_PAUSE -> pausePlayback()
            COMMAND_NEXT -> next()
            COMMAND_PREV -> prev()
            COMMAND_SEEK_FORWARD -> player.seekTo(player.currentPosition + 15000)
            COMMAND_SEEK_BACKWARD -> player.seekTo((player.currentPosition - 15000).coerceAtLeast(0))
            else -> showNotification(player.isPlaying)
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        player.release()
        mediaSession.release()
        super.onDestroy()
    }
}
