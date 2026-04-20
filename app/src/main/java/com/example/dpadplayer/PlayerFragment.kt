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
import com.google.android.material.button.MaterialButton

/**
 * Full now-playing screen.
 * Back button + D-pad BACK key return to LibraryFragment.
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
    private lateinit var btnPrev: MaterialButton
    private lateinit var btnSeekBwd: MaterialButton
    private lateinit var btnPlay: MaterialButton
    private lateinit var btnSeekFwd: MaterialButton
    private lateinit var btnNext: MaterialButton

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
        btnPrev        = view.findViewById(R.id.btn_prev)
        btnSeekBwd     = view.findViewById(R.id.btn_seek_bwd)
        btnPlay        = view.findViewById(R.id.btn_play)
        btnSeekFwd     = view.findViewById(R.id.btn_seek_fwd)
        btnNext        = view.findViewById(R.id.btn_next)

        btnBack.setOnClickListener { navigateBack() }

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
        btnSeekFwd.setOnClickListener { (activity as? MainActivity)?.sendCmd("SEEK_FWD") }
        btnSeekBwd.setOnClickListener { (activity as? MainActivity)?.sendCmd("SEEK_BWD") }
    }

    // ── SeekBar ───────────────────────────────────────────────────────────────

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

    private fun updateTrackCounter() {
        val total = viewModel.tracks.value?.size ?: 0
        val idx   = viewModel.currentIndex.value ?: -1
        tvTrackCounter.text = if (total > 0 && idx >= 0) "${idx + 1} / $total" else ""
    }

    // ── Position updates (called from MainActivity) ───────────────────────────

    fun updatePosition(pos: Long) {
        if (!seekBarDragging) {
            seekBar.progress = pos.toInt()
            tvPosition.text  = formatMs(pos)
        }
    }

    private fun formatMs(ms: Long): String {
        val s = ms / 1000
        return "%d:%02d".format(s / 60, s % 60)
    }
}
