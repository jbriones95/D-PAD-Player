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
 * Shows albums and all songs for a single artist.
 * Pass [ARG_ARTIST_ID] as the artist's key string (lowercase sort name).
 */
class ArtistDetailFragment : Fragment() {

    companion object {
        const val ARG_ARTIST_ID = "artist_id"
        fun newInstance(artistId: String) = ArtistDetailFragment().apply {
            arguments = Bundle().also { it.putString(ARG_ARTIST_ID, artistId) }
        }
    }

    private val viewModel: MusicViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        inflater.inflate(R.layout.fragment_artist_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val artistId = arguments?.getString(ARG_ARTIST_ID) ?: return

        val btnBack        = view.findViewById<MaterialButton>(R.id.btn_back)
        val tvName         = view.findViewById<TextView>(R.id.tv_artist_name)
        val tvSectionAlbums = view.findViewById<TextView>(R.id.tv_section_albums)
        val recyclerAlbums = view.findViewById<RecyclerView>(R.id.recycler_albums)
        val recyclerSongs  = view.findViewById<RecyclerView>(R.id.recycler_songs)

        btnBack.setOnClickListener { parentFragmentManager.popBackStack() }

        val albumAdapter = AlbumAdapter(emptyList()) { album ->
            (activity as? MainActivity)?.openAlbumDetail(album)
        }
        recyclerAlbums.adapter = albumAdapter
        recyclerAlbums.layoutManager = FocusLinearLayoutManager(requireContext())
        recyclerAlbums.isNestedScrollingEnabled = false

        val songAdapter = TrackAdapter(
            items = emptyList(),
            onTrackClick = { index ->
                val artist = viewModel.artists.value?.find { it.id == artistId } ?: return@TrackAdapter
                val track = artist.songs.getOrNull(index) ?: return@TrackAdapter
                val globalIndex = viewModel.tracks.value?.indexOfFirst { it.id == track.id } ?: -1
                if (globalIndex >= 0) (activity as? MainActivity)?.playTrack(globalIndex)
            }
        )
        recyclerSongs.adapter = songAdapter
        recyclerSongs.layoutManager = FocusLinearLayoutManager(requireContext())
        recyclerSongs.isNestedScrollingEnabled = false

        viewModel.artists.observe(viewLifecycleOwner) { artists ->
            val artist = artists.find { it.id == artistId } ?: return@observe
            tvName.text = artist.name

            albumAdapter.update(artist.albums)
            tvSectionAlbums.visibility = if (artist.albums.isEmpty()) View.GONE else View.VISIBLE
            recyclerAlbums.visibility  = if (artist.albums.isEmpty()) View.GONE else View.VISIBLE

            songAdapter.updateTracks(artist.songs)
        }

        viewModel.currentIndex.observe(viewLifecycleOwner) { songAdapter.setSelectedIndex(it) }
    }
}
