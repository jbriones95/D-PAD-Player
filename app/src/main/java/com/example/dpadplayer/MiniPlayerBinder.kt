package com.example.dpadplayer

import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.dpadplayer.playback.Track
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator

/**
 * Wires the shared `view_mini_player` bar to live playback state.
 *
 * Home and Library each embed the same bar, and both previously carried their
 * own copy of this wiring. Behaviour now lives in one place, including the
 * per-host `nextFocusUp` link, which differs because each screen stacks a
 * different widget above the bar.
 */
class MiniPlayerBinder(
    private val root: View,
    private val activity: MainActivity
) {

    private val art: ImageView = root.findViewById(R.id.mini_art)
    private val title: TextView = root.findViewById(R.id.mini_title)
    private val artist: TextView = root.findViewById(R.id.mini_artist)
    private val btnPlay: MaterialButton = root.findViewById(R.id.mini_btn_play)
    private val btnNext: MaterialButton = root.findViewById(R.id.mini_btn_next)
    private val progress: LinearProgressIndicator = root.findViewById(R.id.mini_progress)
    private val openPlayer: View = root.findViewById(R.id.mini_open_player)

    /** id of the widget directly above the bar, for DPAD_UP. */
    private var focusUpTargetId: Int = View.NO_ID

    /**
     * Handles DPAD_UP from any control in the bar. Hosts override this because
     * "the thing above the bar" is a tab list that needs to focus a row, not
     * just the RecyclerView itself.
     */
    private var onFocusUp: (() -> Unit)? = null

    fun attach(focusUpTargetId: Int, onFocusUp: (() -> Unit)? = null): MiniPlayerBinder {
        this.focusUpTargetId = focusUpTargetId
        this.onFocusUp = onFocusUp

        title.isSelected = true

        // Art + text are one focus stop that opens the full player.
        applyMiniPlayerFocusBackground(openPlayer)
        openPlayer.setOnClickListener { activity.openPlayer() }
        openPlayer.setupDpadItem { activity.openPlayer() }

        applyMiniPlayerFocusBackground(btnPlay)
        btnPlay.setOnClickListener { activity.togglePlayPause() }
        btnPlay.setupDpadItem(onFocusChanged = materialButtonFocusChangeHandler(btnPlay)) {
            activity.togglePlayPause()
        }

        applyMiniPlayerFocusBackground(btnNext)
        btnNext.setOnClickListener { activity.sendCmd("NEXT") }
        btnNext.setupDpadItem(onFocusChanged = materialButtonFocusChangeHandler(btnNext)) {
            activity.sendCmd("NEXT")
        }
        btnNext.contentDescription = root.context.getString(R.string.cd_next_track)

        // DPAD_UP leaves the bar for the content above it; DPAD_CENTER falls
        // back to click because setupDpadItem only covers ENTER-style keys.
        listOf(openPlayer, btnPlay, btnNext).forEach { control ->
            control.setOnKeyListener { v, keyCode, event ->
                if (event.action != KeyEvent.ACTION_DOWN) return@setOnKeyListener false
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_UP -> {
                        val handler = onFocusUp
                        if (handler != null) {
                            handler()
                        } else if (focusUpTargetId != View.NO_ID) {
                            root.findViewById<View?>(focusUpTargetId)?.requestFocus()
                        }
                        true
                    }
                    KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                        v.performClick()
                        true
                    }
                    else -> false
                }
            }
            if (focusUpTargetId != View.NO_ID) {
                control.nextFocusUpId = focusUpTargetId
            }
        }

        return this
    }

    fun bind(
        track: Track?,
        isPlaying: Boolean,
        positionMs: Long
    ) {
        // Nothing to show — hide the whole bar rather than a dead-looking
        // "No track playing" strip the user can't act on.
        if (track == null) {
            root.visibility = View.GONE
            return
        }
        root.visibility = View.VISIBLE

        title.text = track.title
        artist.text = track.artist
        openPlayer.contentDescription = root.context.getString(
            R.string.cd_mini_player_with_track, track.title, track.artist
        )

        Glide.with(root)
            .load(track.albumArtUri)
            .placeholder(R.drawable.ic_music_note)
            .error(R.drawable.ic_music_note)
            .fallback(R.drawable.ic_music_note)
            .into(art)

        progress.max = track.duration.coerceAtLeast(1L).toInt()
        setPosition(positionMs)
        setPlaying(isPlaying)
    }

    fun setPlaying(isPlaying: Boolean) {
        btnPlay.icon = ContextCompat.getDrawable(
            root.context,
            if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        )
        btnPlay.contentDescription = root.context.getString(
            if (isPlaying) R.string.cd_pause else R.string.cd_play
        )
    }

    fun setPosition(positionMs: Long) {
        val max = progress.max.coerceAtLeast(1)
        progress.progress = positionMs.coerceIn(0L, max.toLong()).toInt()
    }
}
