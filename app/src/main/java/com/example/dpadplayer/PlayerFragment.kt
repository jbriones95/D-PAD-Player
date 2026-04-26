package com.example.dpadplayer

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import com.google.android.material.button.MaterialButton

/**
 * Full now-playing screen.
 * Back button + D-pad BACK key return to LibraryFragment.
 * D-pad LEFT/RIGHT on seekbar scrubs ±5 seconds.
 */
class PlayerFragment : Fragment() {

    private val viewModel: MusicViewModel by activityViewModels()

    private lateinit var btnBack: MaterialButton
    private lateinit var tvTrackCounter: TextView
    private lateinit var albumArt: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvArtist: TextView
    private lateinit var tvAlbum: TextView
    private lateinit var tvPosition: TextView
    private lateinit var tvDuration: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var btnRepeat: MaterialButton
    private lateinit var btnPrev: MaterialButton
    private lateinit var btnPlay: MaterialButton
    private lateinit var btnNext: MaterialButton
    private lateinit var btnShuffle: MaterialButton

    private var seekBarDragging = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_player, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnBack        = view.findViewById(R.id.btn_back)
        tvTrackCounter = view.findViewById(R.id.tv_track_counter)
        albumArt       = view.findViewById(R.id.album_art)
        tvTitle        = view.findViewById(R.id.tv_title)
        tvArtist       = view.findViewById(R.id.tv_artist)
        tvAlbum        = view.findViewById(R.id.tv_album)
        tvPosition     = view.findViewById(R.id.tv_position)
        tvDuration     = view.findViewById(R.id.tv_duration)
        seekBar        = view.findViewById(R.id.seek_bar)
        btnRepeat      = view.findViewById(R.id.btn_repeat)
        btnPrev        = view.findViewById(R.id.btn_prev)
        btnPlay        = view.findViewById(R.id.btn_play)
        btnNext        = view.findViewById(R.id.btn_next)
        btnShuffle     = view.findViewById(R.id.btn_shuffle)

        btnBack.setOnClickListener { navigateBack() }
        applyItemFocusBackground(btnBack)
        btnBack.setupDpadItem { navigateBack() }
        setupButtons()
        setupSeekBar()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        btnPlay.requestFocus()
        refreshNowPlaying()
    }

    // ── Back navigation ───────────────────────────────────────────────────────

    fun navigateBack() {
        parentFragmentManager.popBackStack()
    }

    // ── Buttons ───────────────────────────────────────────────────────────────

    private fun setupButtons() {
        btnPlay.setOnClickListener    { (activity as? MainActivity)?.togglePlayPause() }
        btnPrev.setOnClickListener    { (activity as? MainActivity)?.sendCmd("PREV") }
        btnNext.setOnClickListener    { (activity as? MainActivity)?.sendCmd("NEXT") }
        btnRepeat.setOnClickListener  { (activity as? MainActivity)?.cycleRepeat() }
        btnShuffle.setOnClickListener { (activity as? MainActivity)?.toggleShuffle() }
    }

    // ── SeekBar — touch dragging + D-pad scrubbing ────────────────────────────

    private fun setupSeekBar() {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(sb: SeekBar) { seekBarDragging = true }
            override fun onProgressChanged(sb: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) tvPosition.text = formatMs(progress.toLong())
            }
            override fun onStopTrackingTouch(sb: SeekBar) {
                seekBarDragging = false
                (activity as? MainActivity)?.seekTo(sb.progress.toLong())
            }
        })

        // D-pad LEFT/RIGHT on the seekbar scrubs by the user's configured step
        seekBar.setOnKeyListener { _, keyCode, event ->
            if (event.action != KeyEvent.ACTION_DOWN) return@setOnKeyListener false
            val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val step = prefs.getString("seek_step", "5000")?.toLongOrNull() ?: 5_000L
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    val newPos = (seekBar.progress.toLong() - step).coerceAtLeast(0L)
                    seekBar.progress = newPos.toInt()
                    tvPosition.text = formatMs(newPos)
                    (activity as? MainActivity)?.seekTo(newPos)
                    true
                }
                KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    val newPos = (seekBar.progress.toLong() + step).coerceAtMost(seekBar.max.toLong())
                    seekBar.progress = newPos.toInt()
                    tvPosition.text = formatMs(newPos)
                    (activity as? MainActivity)?.seekTo(newPos)
                    true
                }
                else -> false
            }
        }
    }

    // ── ViewModel observation ─────────────────────────────────────────────────

    private fun observeViewModel() {
        viewModel.currentIndex.observe(viewLifecycleOwner) {
            refreshNowPlaying()
            updateTrackCounter()
        }
        viewModel.isPlaying.observe(viewLifecycleOwner) { playing ->
            updatePlayPauseIcon(playing)
        }
        viewModel.position.observe(viewLifecycleOwner) { pos ->
            if (!seekBarDragging) {
                seekBar.progress = pos.toInt()
                tvPosition.text  = formatMs(pos)
            }
        }
        viewModel.repeatMode.observe(viewLifecycleOwner) { mode ->
            updateRepeatIcon(mode)
        }
        viewModel.shuffleOn.observe(viewLifecycleOwner) { on ->
            updateShuffleIcon(on)
        }
    }

    // ── UI refresh ────────────────────────────────────────────────────────────

    private fun refreshNowPlaying() {
        val activity = activity as? MainActivity ?: return
        val track = activity.currentTrack() ?: return

        tvTitle.text    = track.title
        tvArtist.text   = track.artist
        tvAlbum.text    = track.album
        tvDuration.text = formatMs(track.duration)
        seekBar.max     = track.duration.toInt()

        // Marquee requires the view to be selected
        tvTitle.isSelected = true

        updatePlayPauseIcon(viewModel.isPlaying.value ?: false)
        updateTrackCounter()

        val loaded = try {
            requireContext().contentResolver.openInputStream(track.albumArtUri)?.use { true } ?: false
        } catch (_: Exception) { false }
        if (loaded) albumArt.setImageURI(track.albumArtUri)
        else { albumArt.setImageURI(null); albumArt.setImageResource(R.drawable.ic_music_note) }
    }

    private fun updatePlayPauseIcon(isPlaying: Boolean) {
        val icon = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        btnPlay.icon = ContextCompat.getDrawable(requireContext(), icon)
    }

    private fun updateRepeatIcon(mode: Int) {
        val icon = when (mode) {
            MusicViewModel.REPEAT_ALL -> R.drawable.ic_repeat_all
            MusicViewModel.REPEAT_ONE -> R.drawable.ic_repeat_one
            else                      -> R.drawable.ic_repeat_off
        }
        btnRepeat.icon = ContextCompat.getDrawable(requireContext(), icon)
    }

    private fun updateShuffleIcon(on: Boolean) {
        val icon = if (on) R.drawable.ic_shuffle_on else R.drawable.ic_shuffle_off
        btnShuffle.icon = ContextCompat.getDrawable(requireContext(), icon)
    }

    private fun updateTrackCounter() {
        val total = viewModel.tracks.value?.size ?: 0
        val idx   = viewModel.currentIndex.value ?: -1
        tvTrackCounter.text = if (total > 0 && idx >= 0) "${idx + 1} / $total" else ""
    }

    private fun formatMs(ms: Long): String {
        val s = ms / 1000
        return "%d:%02d".format(s / 60, s % 60)
    }
}
