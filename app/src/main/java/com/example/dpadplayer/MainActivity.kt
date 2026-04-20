package com.example.dpadplayer

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.KeyEvent
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
        // Put D-pad focus on Play button when coming back to app
        btnPlay.requestFocus()
    }

    // ─── View binding ─────────────────────────────────────────────────────────

    private fun bindViews() {
        recycler     = findViewById(R.id.track_list)
        albumArt     = findViewById(R.id.album_art)
        tvTitle      = findViewById(R.id.tv_title)
        tvArtist     = findViewById(R.id.tv_artist)
        tvAlbum      = findViewById(R.id.tv_album)
        tvPosition   = findViewById(R.id.tv_position)
        tvDuration   = findViewById(R.id.tv_duration)
        seekBar      = findViewById(R.id.seek_bar)
        btnPlay      = findViewById(R.id.btn_play)
        btnPrev      = findViewById(R.id.btn_prev)
        btnNext      = findViewById(R.id.btn_next)
        btnSeekFwd   = findViewById(R.id.btn_seek_fwd)
        btnSeekBwd   = findViewById(R.id.btn_seek_bwd)
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
        }
        viewModel.currentIndex.observe(this) { index ->
            adapter.setSelectedIndex(index)
            if (index >= 0) recycler.smoothScrollToPosition(index)
            refreshNowPlaying()
        }
    }

    // ─── Service callbacks ────────────────────────────────────────────────────

    private fun setupServiceCallbacks() {
        val svc = service ?: return
        svc.onTrackChanged = { index ->
            runOnUiThread {
                viewModel.setCurrentIndex(index)
                refreshNowPlaying()
            }
        }
        svc.onPlaybackStateChanged = { isPlaying ->
            runOnUiThread {
                btnPlay.text = if (isPlaying) "Pause" else "Play"
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

        btnPlay.text = if (svc.isPlaying) "Pause" else "Play"

        // Load album art — try the MediaStore URI, fall back to placeholder
        try {
            albumArt.setImageURI(t.albumArtUri)
            if (albumArt.drawable == null) albumArt.setImageResource(android.R.drawable.ic_media_play)
        } catch (e: Exception) {
            albumArt.setImageResource(android.R.drawable.ic_media_play)
        }
    }

    // ─── Permissions ──────────────────────────────────────────────────────────

    private fun checkPermissionsAndLoad() {
        val permissions = buildList {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.READ_MEDIA_AUDIO)
            } else {
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
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

    // ─── D-pad key handling ───────────────────────────────────────────────────

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_ENTER -> {
                currentFocus?.performClick()
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
            KeyEvent.KEYCODE_MEDIA_NEXT -> { sendCmd(PlaybackService.COMMAND_NEXT); return true }
            KeyEvent.KEYCODE_MEDIA_PREVIOUS -> { sendCmd(PlaybackService.COMMAND_PREV); return true }
        }
        return super.onKeyDown(keyCode, event)
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
