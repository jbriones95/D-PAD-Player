package com.example.dpadplayer

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.example.dpadplayer.playback.PlaybackService
import com.example.dpadplayer.playback.Track
import com.example.dpadplayer.db.PlaylistEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    val viewModel: MusicViewModel by viewModels()

    // Service binding
    private var service: PlaybackService? = null
    private var bound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            service = (binder as PlaybackService.LocalBinder).getService()
            bound = true
            // Push current track list into service
            viewModel.tracks.value?.let { tracks ->
                if (tracks.isNotEmpty()) {
                    service!!.tracks.clear()
                    service!!.tracks.addAll(tracks)
                }
            }
            // Restore selected index in adapter
            val idx = service!!.currentIndex
            if (idx >= 0) viewModel.setCurrentIndex(idx)
            setupServiceCallbacks()
        }
        override fun onServiceDisconnected(name: ComponentName) {
            bound = false
            service = null
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            if (results.values.any { it }) onPermissionGranted()
            else Toast.makeText(this, "Storage permission needed to find music", Toast.LENGTH_LONG).show()
        }

    // ── Lifecycle ──────────────────────────────────────────────────────────────

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply saved accent + night mode before inflating
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        SettingsFragment.applyTheme(prefs.getString("theme", "system") ?: "system")
        setTheme(SettingsFragment.accentThemeRes(prefs.getString("accent", "deep_purple") ?: "deep_purple"))

        setContentView(R.layout.activity_main)

        // Show HomeFragment as the root screen
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment(), TAG_HOME)
                .commit()
        }

        checkPermissionsAndLoad()
    }

    override fun onStart() {
        super.onStart()
        Intent(this, PlaybackService::class.java).also { intent ->
            ContextCompat.startForegroundService(this, intent)
            bindService(intent, connection, BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (bound) {
            service?.onTrackChanged        = null
            service?.onPlaybackStateChanged = null
            service?.onPositionChanged      = null
            unbindService(connection)
            bound = false
        }
    }

    // ── Fragment navigation ────────────────────────────────────────────────────

    fun openLibraryTab(tabIndex: Int) {
        val existing = supportFragmentManager.findFragmentByTag(TAG_LIBRARY) as? LibraryFragment
        if (existing != null && existing.isVisible) {
            existing.selectTab(tabIndex)
            return
        }
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                                 android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .replace(R.id.fragment_container, LibraryFragment.newInstance(tabIndex), TAG_LIBRARY)
            .addToBackStack(null)
            .commit()
    }

    fun openSettings() {
        if (supportFragmentManager.findFragmentByTag(TAG_SETTINGS) != null) return
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                                 android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .replace(R.id.fragment_container, SettingsFragment(), TAG_SETTINGS)
            .addToBackStack(null)
            .commit()
    }

    fun openPlayer() {
        if (supportFragmentManager.findFragmentByTag(TAG_PLAYER) != null) return
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right, R.anim.slide_out_left,
                android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .replace(R.id.fragment_container, PlayerFragment(), TAG_PLAYER)
            .addToBackStack(null)
            .commit()
    }

    fun openAlbumDetail(album: Album) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right, R.anim.slide_out_left,
                android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .replace(R.id.fragment_container, AlbumDetailFragment.newInstance(album.id), TAG_ALBUM)
            .addToBackStack(null)
            .commit()
    }

    fun openArtistDetail(artist: Artist) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                                 android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .replace(R.id.fragment_container, ArtistDetailFragment.newInstance(artist.id), TAG_ARTIST)
            .addToBackStack(null)
            .commit()
    }

    fun openGenreDetail(genre: Genre) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                                 android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .replace(R.id.fragment_container, GenreDetailFragment.newInstance(genre.id), TAG_GENRE)
            .addToBackStack(null)
            .commit()
    }

    fun openPlaylistDetail(playlist: PlaylistEntity) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                                 android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .replace(R.id.fragment_container, PlaylistDetailFragment.newInstance(playlist.id), TAG_PLAYLIST)
            .addToBackStack(null)
            .commit()
    }

    fun playPlaylist(playlistId: Long, startIndex: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            val tracks = viewModel.resolvePlaylistTracks(playlistId)
            if (tracks.isNotEmpty()) {
                service?.tracks?.clear()
                service?.tracks?.addAll(tracks)
                service?.prepareAndPlay(startIndex.coerceIn(0, tracks.size - 1))
            }
        }
    }

    private fun isPlayerVisible() =
        supportFragmentManager.findFragmentByTag(TAG_PLAYER)?.isVisible == true

    // ── D-pad key handling ─────────────────────────────────────────────────────

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                // Back key always pops the stack
                KeyEvent.KEYCODE_BACK -> {
                    if (supportFragmentManager.backStackEntryCount > 0) {
                        supportFragmentManager.popBackStack()
                        return true
                    }
                }
                // Media keys work from any screen
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> { togglePlayPause(); return true }
                KeyEvent.KEYCODE_MEDIA_NEXT       -> { sendCmd("NEXT"); return true }
                KeyEvent.KEYCODE_MEDIA_PREVIOUS   -> { sendCmd("PREV"); return true }
            }
        }
        return super.dispatchKeyEvent(event)
    }

    // ── Public API for fragments ───────────────────────────────────────────────

    fun togglePlayPause() {
        val svc = service ?: return
        if (svc.isPlaying) sendCmd("PAUSE") else sendCmd("PLAY")
    }

    fun playTrack(index: Int) {
        service?.prepareAndPlay(index)
    }

    fun seekTo(posMs: Long) {
        service?.seekTo(posMs)
    }

    fun currentTrack(): Track? {
        val svc = service ?: return null
        if (svc.tracks.isEmpty()) return null
        val idx = svc.currentIndex
        return if (idx >= 0 && idx < svc.tracks.size) svc.tracks[idx] else null
    }

    fun toggleShuffle() {
        service?.toggleShuffle()
    }

    fun cycleRepeat() {
        service?.cycleRepeat()
    }

    fun sendCmd(cmd: String) {
        val action = when (cmd) {
            "PLAY"     -> PlaybackService.COMMAND_PLAY
            "PAUSE"    -> PlaybackService.COMMAND_PAUSE
            "NEXT"     -> PlaybackService.COMMAND_NEXT
            "PREV"     -> PlaybackService.COMMAND_PREV
            "SEEK_FWD" -> PlaybackService.COMMAND_SEEK_FWD
            "SEEK_BWD" -> PlaybackService.COMMAND_SEEK_BWD
            else       -> return
        }
        startService(Intent(this, PlaybackService::class.java).also { it.action = action })
    }

    // ── Service callbacks ──────────────────────────────────────────────────────

    private fun setupServiceCallbacks() {
        val svc = service ?: return
        svc.onTrackChanged = { index ->
            runOnUiThread {
                viewModel.setCurrentIndex(index)
            }
        }
        svc.onPlaybackStateChanged = { isPlaying ->
            runOnUiThread {
                viewModel.setPlaying(isPlaying)
            }
        }
        svc.onPositionChanged = { pos ->
            runOnUiThread {
                viewModel.setPosition(pos)
            }
        }
        svc.onShuffleChanged = { on ->
            runOnUiThread {
                viewModel.setShuffleOn(on)
            }
        }
        svc.onRepeatChanged = { mode ->
            runOnUiThread {
                viewModel.setRepeatMode(mode)
            }
        }
    }

    // ── Permissions ────────────────────────────────────────────────────────────

    private fun checkPermissionsAndLoad() {
        val permissions = buildList {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                add(Manifest.permission.READ_MEDIA_AUDIO)
            else
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }.toTypedArray()

        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
        if (allGranted) onPermissionGranted()
        else requestPermissionLauncher.launch(permissions)
    }

    private fun onPermissionGranted() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val sortOrder = prefs.getString("sort_order", "title") ?: "title"
        viewModel.loadTracks(sortOrder)
        // After tracks load, push them into the service
        viewModel.tracks.observeForever { tracks ->
            service?.let { svc ->
                svc.tracks.clear()
                svc.tracks.addAll(tracks)
            }
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private fun libraryFragment() =
        supportFragmentManager.findFragmentByTag(TAG_LIBRARY) as? LibraryFragment

    private fun isDescendantOf(view: android.view.View, parent: android.view.View): Boolean {
        var v: android.view.View? = view
        while (v != null) {
            if (v === parent) return true
            v = v.parent as? android.view.View
        }
        return false
    }

    companion object {
        const val TAG_HOME     = "home"
        const val TAG_LIBRARY  = "library"
        const val TAG_PLAYER   = "player"
        const val TAG_SETTINGS = "settings"
        const val TAG_ALBUM    = "album"
        const val TAG_PLAYLIST = "playlist"
        const val TAG_ARTIST   = "artist"
        const val TAG_GENRE    = "genre"
    }
}
