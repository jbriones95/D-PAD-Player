package com.example.dpadplayer

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dpadplayer.playback.PlaybackService
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private val viewModel: MusicViewModel by viewModels()

    // Views
    private lateinit var recycler: RecyclerView
    private lateinit var albumArt: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvArtist: TextView
    private lateinit var tvAlbum: TextView
    private lateinit var tvPosition: TextView
    private lateinit var tvDuration: TextView
    private lateinit var tvTrackCounter: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var btnPlay: MaterialButton
    private lateinit var btnPrev: MaterialButton
    private lateinit var btnNext: MaterialButton
    private lateinit var btnSeekFwd: MaterialButton
    private lateinit var btnSeekBwd: MaterialButton

    private lateinit var adapter: TrackAdapter

    // Service binding
    private var service: PlaybackService? = null
    private var bound = false
    private var seekBarDragging = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            service = (binder as PlaybackService.LocalBinder).getService()
            bound = true
            viewModel.tracks.value?.let { tracks ->
                if (tracks.isNotEmpty()) {
                    service!!.tracks.clear()
                    service!!.tracks.addAll(tracks)
                }
            }
            val idx = service!!.currentIndex
            if (idx >= 0) {
                adapter.setSelectedIndex(idx)
                recycler.scrollToPosition(idx)
            }
            refreshNowPlaying()
            setupServiceCallbacks()
        }
        override fun onServiceDisconnected(name: ComponentName) {
            bound = false
            service = null
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            val granted = results.values.any { it }
            if (granted) onPermissionGranted()
            else Toast.makeText(this, "Storage permission needed to find music", Toast.LENGTH_LONG).show()
        }

    // ─── Lifecycle ────────────────────────────────────────────────────────────

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()
        applyScreenProportions()
        setupRecyclerView()
        setupButtons()
        setupSeekBar()
        observeViewModel()
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
            service?.onTrackChanged = null
            service?.onPlaybackStateChanged = null
            service?.onPositionChanged = null
            unbindService(connection)
            bound = false
        }
    }

    override fun onResume() {
        super.onResume()
        btnPlay.requestFocus()
    }

    // ─── View binding ─────────────────────────────────────────────────────────

    private fun bindViews() {
        recycler       = findViewById(R.id.track_list)
        albumArt       = findViewById(R.id.album_art)
        tvTitle        = findViewById(R.id.tv_title)
        tvArtist       = findViewById(R.id.tv_artist)
        tvAlbum        = findViewById(R.id.tv_album)
        tvPosition     = findViewById(R.id.tv_position)
        tvDuration     = findViewById(R.id.tv_duration)
        tvTrackCounter = findViewById(R.id.tv_track_counter)
        seekBar        = findViewById(R.id.seek_bar)
        btnPlay        = findViewById(R.id.btn_play)
        btnPrev        = findViewById(R.id.btn_prev)
        btnNext        = findViewById(R.id.btn_next)
        btnSeekFwd     = findViewById(R.id.btn_seek_fwd)
        btnSeekBwd     = findViewById(R.id.btn_seek_bwd)
    }

    /**
     * Reads device screen dp dimensions and adjusts panel proportions at runtime.
     * Target: player panel = 28% of screen height, track list gets the rest.
     * This keeps the UI proportional across any screen size.
     */
    private fun applyScreenProportions() {
        val dm = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(dm)
        val screenHeightDp = (dm.heightPixels / dm.density).toInt()
        val screenWidthDp  = (dm.widthPixels  / dm.density).toInt()

        // Player panel: 28% of screen height, min 120dp, max 180dp
        val panelDp = (screenHeightDp * 0.28f).toInt().coerceIn(120, 180)
        val panelPx = (panelDp * dm.density).toInt()

        val playerPanel = findViewById<View>(R.id.player_panel)
        val lp = playerPanel.layoutParams
        lp.height = panelPx
        playerPanel.layoutParams = lp

        // Scale album art: ~12% of screen height, min 40dp, max 64dp
        val artDp = (screenHeightDp * 0.12f).toInt().coerceIn(40, 64)
        val artPx = (artDp * dm.density).toInt()
        val artLp = albumArt.layoutParams
        artLp.width  = artPx
        artLp.height = artPx
        albumArt.layoutParams = artLp
    }

    // ─── RecyclerView ─────────────────────────────────────────────────────────

    private fun setupRecyclerView() {
        adapter = TrackAdapter(emptyList()) { index ->
            service?.prepareAndPlay(index)
            btnPlay.requestFocus()
        }
        recycler.adapter = adapter
        recycler.layoutManager = FocusLinearLayoutManager(this)
    }

    // ─── Buttons ──────────────────────────────────────────────────────────────

    private fun setupButtons() {
        btnPlay.setOnClickListener {
            val svc = service ?: return@setOnClickListener
            if (svc.isPlaying) sendCmd(PlaybackService.COMMAND_PAUSE)
            else sendCmd(PlaybackService.COMMAND_PLAY)
        }
        btnPrev.setOnClickListener    { sendCmd(PlaybackService.COMMAND_PREV) }
        btnNext.setOnClickListener    { sendCmd(PlaybackService.COMMAND_NEXT) }
        btnSeekFwd.setOnClickListener { sendCmd(PlaybackService.COMMAND_SEEK_FWD) }
        btnSeekBwd.setOnClickListener { sendCmd(PlaybackService.COMMAND_SEEK_BWD) }
    }

    // ─── SeekBar ──────────────────────────────────────────────────────────────

    private fun setupSeekBar() {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(sb: SeekBar) { seekBarDragging = true }
            override fun onProgressChanged(sb: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) tvPosition.text = formatMs(progress.toLong())
            }
            override fun onStopTrackingTouch(sb: SeekBar) {
                seekBarDragging = false
                val intent = Intent(this@MainActivity, PlaybackService::class.java)
                intent.action = PlaybackService.COMMAND_SEEK_TO
                intent.putExtra(PlaybackService.EXTRA_POSITION, sb.progress.toLong())
                startService(intent)
            }
        })
    }

    // ─── ViewModel observation ────────────────────────────────────────────────

    private fun observeViewModel() {
        viewModel.tracks.observe(this) { tracks ->
            adapter.updateTracks(tracks)
            service?.let { svc ->
                svc.tracks.clear()
                svc.tracks.addAll(tracks)
            }
            updateTrackCounter()
        }
        viewModel.currentIndex.observe(this) { index ->
            adapter.setSelectedIndex(index)
            if (index >= 0) recycler.smoothScrollToPosition(index)
            refreshNowPlaying()
            updateTrackCounter()
        }
    }

    // ─── Service callbacks ────────────────────────────────────────────────────

    private fun setupServiceCallbacks() {
        val svc = service ?: return
        svc.onTrackChanged = { index ->
            runOnUiThread {
                viewModel.setCurrentIndex(index)
                refreshNowPlaying()
                updateTrackCounter()
            }
        }
        svc.onPlaybackStateChanged = { isPlaying ->
            runOnUiThread {
                updatePlayPauseIcon(isPlaying)
                viewModel.setPlaying(isPlaying)
            }
        }
        svc.onPositionChanged = { pos ->
            runOnUiThread {
                if (!seekBarDragging) {
                    seekBar.progress = pos.toInt()
                    tvPosition.text = formatMs(pos)
                }
            }
        }
    }

    private fun refreshNowPlaying() {
        val svc = service ?: return
        if (svc.tracks.isEmpty()) return
        val idx = svc.currentIndex
        val t = svc.tracks[idx]

        tvTitle.text    = t.title
        tvArtist.text   = t.artist
        tvAlbum.text    = t.album
        tvDuration.text = formatMs(t.duration)
        seekBar.max     = t.duration.toInt()

        updatePlayPauseIcon(svc.isPlaying)

        val artLoaded = try {
            contentResolver.openInputStream(t.albumArtUri)?.use { true } ?: false
        } catch (_: Exception) { false }
        if (artLoaded) {
            albumArt.setImageURI(t.albumArtUri)
        } else {
            albumArt.setImageURI(null)
            albumArt.setImageResource(R.drawable.ic_music_note)
        }
    }

    private fun updatePlayPauseIcon(isPlaying: Boolean) {
        val icon = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        btnPlay.icon = ContextCompat.getDrawable(this, icon)
    }

    private fun updateTrackCounter() {
        val total = viewModel.tracks.value?.size ?: 0
        val idx   = viewModel.currentIndex.value ?: -1
        tvTrackCounter.text = if (total > 0 && idx >= 0) "${idx + 1}/$total" else ""
    }

    // ─── D-pad key handling ───────────────────────────────────────────────────

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            val focused = currentFocus
            val isInRecycler = focused != null && isDescendantOf(focused, recycler)

            when (event.keyCode) {
                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    if (isInRecycler) {
                        val itemCount = adapter.itemCount
                        val focusedPos = if (focused != null)
                            recycler.getChildAdapterPosition(focused) else RecyclerView.NO_POSITION
                        if (focusedPos != RecyclerView.NO_POSITION && focusedPos >= itemCount - 1) {
                            btnPlay.requestFocus()
                            return true
                        }
                    }
                }
                KeyEvent.KEYCODE_DPAD_UP -> {
                    if (!isInRecycler && focused != null && isTransportControl(focused)) {
                        val idx = (viewModel.currentIndex.value ?: -1).coerceAtLeast(0)
                        recycler.scrollToPosition(idx)
                        recycler.post {
                            recycler.findViewHolderForAdapterPosition(idx)?.itemView?.requestFocus()
                        }
                        return true
                    }
                }
                KeyEvent.KEYCODE_DPAD_CENTER,
                KeyEvent.KEYCODE_ENTER -> {
                    focused?.performClick()
                    return true
                }
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                    val svc = service
                    if (svc != null) {
                        if (svc.isPlaying) sendCmd(PlaybackService.COMMAND_PAUSE)
                        else sendCmd(PlaybackService.COMMAND_PLAY)
                    }
                    return true
                }
                KeyEvent.KEYCODE_MEDIA_NEXT     -> { sendCmd(PlaybackService.COMMAND_NEXT); return true }
                KeyEvent.KEYCODE_MEDIA_PREVIOUS -> { sendCmd(PlaybackService.COMMAND_PREV); return true }
            }
        }
        return super.dispatchKeyEvent(event)
    }

    private fun isDescendantOf(view: View, parent: View): Boolean {
        var v: View? = view
        while (v != null) {
            if (v === parent) return true
            v = v.parent as? View
        }
        return false
    }

    private fun isTransportControl(view: View): Boolean =
        view === btnPlay || view === btnPrev || view === btnNext ||
        view === btnSeekFwd || view === btnSeekBwd || view === seekBar

    // ─── Permissions ──────────────────────────────────────────────────────────

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
        viewModel.loadTracks()
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private fun sendCmd(command: String) {
        val intent = Intent(this, PlaybackService::class.java)
        intent.action = command
        startService(intent)
    }

    private fun formatMs(ms: Long): String {
        val totalSec = ms / 1000
        val min = totalSec / 60
        val sec = totalSec % 60
        return "%d:%02d".format(min, sec)
    }
}
