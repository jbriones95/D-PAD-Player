package com.example.dpadplayer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dpadplayer.playback.PlaybackService
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var playButton: MaterialButton
    private lateinit var pauseButton: MaterialButton
    private lateinit var nextButton: MaterialButton
    private lateinit var prevButton: MaterialButton
    private lateinit var seekForwardButton: MaterialButton
    private lateinit var seekBackwardButton: MaterialButton
    private lateinit var recycler: RecyclerView

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) scanAndLoad()
            else Toast.makeText(this, "Storage permission required to find MP3 files", Toast.LENGTH_LONG).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playButton = findViewById(R.id.btn_play)
        pauseButton = findViewById(R.id.btn_pause)
        nextButton = findViewById(R.id.btn_next)
        prevButton = findViewById(R.id.btn_prev)
        seekForwardButton = findViewById(R.id.btn_seek_fwd)
        seekBackwardButton = findViewById(R.id.btn_seek_bwd)
        recycler = findViewById(R.id.track_list)

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = TrackAdapter(emptyList())

        playButton.setOnClickListener { sendCommand(PlaybackService.COMMAND_PLAY) }
        pauseButton.setOnClickListener { sendCommand(PlaybackService.COMMAND_PAUSE) }
        nextButton.setOnClickListener { sendCommand(PlaybackService.COMMAND_NEXT) }
        prevButton.setOnClickListener { sendCommand(PlaybackService.COMMAND_PREV) }
        seekForwardButton.setOnClickListener { sendCommand(PlaybackService.COMMAND_SEEK_FORWARD) }
        seekBackwardButton.setOnClickListener { sendCommand(PlaybackService.COMMAND_SEEK_BACKWARD) }

        checkPermissionsAndScan()
    }

    private fun checkPermissionsAndScan() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            scanAndLoad()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun scanAndLoad() {
        // start the service which will scan and manage playback
        val intent = Intent(this, PlaybackService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }

    private fun sendCommand(command: String) {
        val intent = Intent(this, PlaybackService::class.java)
        intent.action = command
        startService(intent)
    }

    // Basic D-pad handling to move focus between controls (optional custom behavior)
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Let the default focus system handle navigation; handle media keys if present
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                // Activate focused view
                currentFocus?.performClick()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

}
