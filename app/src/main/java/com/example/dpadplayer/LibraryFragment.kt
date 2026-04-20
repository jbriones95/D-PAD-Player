package com.example.dpadplayer

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

/**
 * Library screen: shows the full track list.
 * A mini now-playing bar at the bottom taps/D-pads into PlayerFragment.
 */
class LibraryFragment : Fragment() {

    private val viewModel: MusicViewModel by activityViewModels()

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: TrackAdapter
    private lateinit var miniPlayer: View
    private lateinit var miniArt: ImageView
    private lateinit var miniTitle: TextView
    private lateinit var miniArtist: TextView
    private lateinit var miniBtnPlay: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_library, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler    = view.findViewById(R.id.track_list)
        miniPlayer  = view.findViewById(R.id.mini_player)
        miniArt     = view.findViewById(R.id.mini_art)
        miniTitle   = view.findViewById(R.id.mini_title)
        miniArtist  = view.findViewById(R.id.mini_artist)
        miniBtnPlay = view.findViewById(R.id.mini_btn_play)

        adapter = TrackAdapter(emptyList()) { index ->
            (activity as? MainActivity)?.playTrack(index)
            miniBtnPlay.requestFocus()
        }
        recycler.adapter = adapter
        recycler.layoutManager = FocusLinearLayoutManager(requireContext())

        // Mini player opens full player screen
        val openPlayer = View.OnClickListener {
            (activity as? MainActivity)?.openPlayer()
        }
        miniPlayer.setOnClickListener(openPlayer)
        miniPlayer.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN &&
                (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER)) {
                (activity as? MainActivity)?.openPlayer()
                true
            } else false
        }

        miniBtnPlay.setOnClickListener {
            (activity as? MainActivity)?.togglePlayPause()
        }

        // D-pad: Down from last list item → mini player; Up from mini player → list
        recycler.addOnChildAttachStateChangeListener(object :
            RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(v: View) {}
            override fun onChildViewDetachedFromWindow(v: View) {}
        })

        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        // Restore focus to the playing track row or first item
        val idx = (viewModel.currentIndex.value ?: -1).coerceAtLeast(0)
        recycler.post {
            recycler.findViewHolderForAdapterPosition(idx)?.itemView?.requestFocus()
                ?: recycler.requestFocus()
        }
    }

    private fun observeViewModel() {
        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            adapter.updateTracks(tracks)
        }
        viewModel.currentIndex.observe(viewLifecycleOwner) { index ->
            adapter.setSelectedIndex(index)
            if (index >= 0) recycler.smoothScrollToPosition(index)
            refreshMiniPlayer()
        }
        viewModel.isPlaying.observe(viewLifecycleOwner) { playing ->
            updateMiniPlayIcon(playing)
        }
    }

    private fun refreshMiniPlayer() {
        val activity = activity as? MainActivity ?: return
        val track = activity.currentTrack() ?: return
        miniTitle.text  = track.title
        miniArtist.text = track.artist

        val loaded = try {
            requireContext().contentResolver.openInputStream(track.albumArtUri)?.use { true } ?: false
        } catch (_: Exception) { false }
        if (loaded) miniArt.setImageURI(track.albumArtUri)
        else { miniArt.setImageURI(null); miniArt.setImageResource(R.drawable.ic_music_note) }
    }

    private fun updateMiniPlayIcon(isPlaying: Boolean) {
        val icon = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        miniBtnPlay.icon = ContextCompat.getDrawable(requireContext(), icon)
    }

    /** Called by MainActivity's dispatchKeyEvent to handle D-pad navigation out of the list. */
    fun onDpadDown(focusedPos: Int): Boolean {
        return if (focusedPos >= adapter.itemCount - 1) {
            miniPlayer.requestFocus()
            true
        } else false
    }

    fun onDpadUpFromMini(): Boolean {
        val idx = (viewModel.currentIndex.value ?: -1).coerceAtLeast(0)
        recycler.scrollToPosition(idx)
        recycler.post {
            recycler.findViewHolderForAdapterPosition(idx)?.itemView?.requestFocus()
                ?: recycler.requestFocus()
        }
        return true
    }

    fun isMiniPlayerFocused(): Boolean =
        miniPlayer.hasFocus() || miniPlayer.isFocused || miniBtnPlay.hasFocus()

    fun recyclerView(): RecyclerView = recycler
}
