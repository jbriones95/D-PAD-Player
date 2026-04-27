package com.example.dpadplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

/**
 * Shows all songs in a genre with a play-on-click behaviour.
 * Pass [ARG_GENRE_ID] as the genre key (lowercased genre name).
 */
class GenreDetailFragment : Fragment() {

    companion object {
        const val ARG_GENRE_ID = "genre_id"
        fun newInstance(genreId: String) = GenreDetailFragment().apply {
            arguments = Bundle().also { it.putString(ARG_GENRE_ID, genreId) }
        }
    }

    private val viewModel: MusicViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        inflater.inflate(R.layout.fragment_genre_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val genreId = arguments?.getString(ARG_GENRE_ID) ?: return

        val btnBack  = view.findViewById<MaterialButton>(R.id.btn_back)
        val tvName   = view.findViewById<TextView>(R.id.tv_genre_name)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_genre)

        btnBack.setOnClickListener { parentFragmentManager.popBackStack() }
        applyPlayerControlFocusBackground(btnBack)
        btnBack.setupDpadItem(onFocusChanged = materialButtonFocusChangeHandler(btnBack)) {
            parentFragmentManager.popBackStack()
        }

        val adapter = TrackAdapter(
            items = emptyList(),
            onTrackClick = { index ->
                val genre = viewModel.genres.value?.find { it.id == genreId } ?: return@TrackAdapter
                val track = genre.songs.getOrNull(index) ?: return@TrackAdapter
                val globalIndex = viewModel.tracks.value?.indexOfFirst { it.id == track.id } ?: -1
                if (globalIndex >= 0) (activity as? MainActivity)?.playTrack(globalIndex)
            }
        )
        recycler.adapter = adapter
        recycler.layoutManager = FocusLinearLayoutManager(requireContext())

        var focusRequested = false
        viewModel.genres.observe(viewLifecycleOwner) { genres ->
            val genre = genres.find { it.id == genreId } ?: return@observe
            tvName.text = genre.name
            adapter.updateTracks(genre.songs)
            if (!focusRequested && genre.songs.isNotEmpty()) {
                focusRequested = true
                recycler.post {
                    val first = recycler.layoutManager?.findViewByPosition(0) ?: recycler.getChildAt(0)
                    (first?.findViewById<View?>(R.id.clickable_item) ?: first)?.requestFocus()
                }
            }
        }

        viewModel.currentIndex.observe(viewLifecycleOwner) { adapter.setSelectedIndex(it) }
    }
}
