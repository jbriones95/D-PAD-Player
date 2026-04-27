package com.example.dpadplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

/**
 * Shows a single album's songs, sorted by disc/track.
 * Pass [ARG_ALBUM_ID] as the album key string.
 */
class AlbumDetailFragment : Fragment() {

    companion object {
        const val ARG_ALBUM_ID = "album_id"
        fun newInstance(albumId: String) = AlbumDetailFragment().apply {
            arguments = Bundle().also { it.putString(ARG_ALBUM_ID, albumId) }
        }
    }

    private val viewModel: MusicViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        inflater.inflate(R.layout.fragment_album_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val albumId = arguments?.getString(ARG_ALBUM_ID) ?: return

        val btnBack      = view.findViewById<MaterialButton>(R.id.btn_back)
        val tvTitle      = view.findViewById<TextView>(R.id.tv_detail_title)
        val ivArt        = view.findViewById<ImageView>(R.id.iv_detail_art)
        val tvAlbum      = view.findViewById<TextView>(R.id.tv_detail_album)
        val tvArtist     = view.findViewById<TextView>(R.id.tv_detail_artist)
        val tvMeta       = view.findViewById<TextView>(R.id.tv_detail_meta)
        val recycler     = view.findViewById<RecyclerView>(R.id.recycler_detail)

        btnBack.setOnClickListener { parentFragmentManager.popBackStack() }
        applyPlayerControlFocusBackground(btnBack)
        btnBack.setupDpadItem(onFocusChanged = materialButtonFocusChangeHandler(btnBack)) {
            parentFragmentManager.popBackStack()
        }

        val adapter = TrackAdapter(
            items = emptyList(),
            onTrackClick = { index ->
                // find global index in full track list
                val album = viewModel.albums.value?.find { it.id == albumId } ?: return@TrackAdapter
                val track = album.songs.getOrNull(index) ?: return@TrackAdapter
                val globalIndex = viewModel.tracks.value?.indexOfFirst { it.id == track.id } ?: -1
                if (globalIndex >= 0) (activity as? MainActivity)?.playTrack(globalIndex)
            },
            onMenuClick = { anchor, track, _ ->
                (activity as? MainActivity)?.showTrackMenu(anchor, track)
            }
        )
        recycler.adapter = adapter
        recycler.layoutManager = FocusLinearLayoutManager(requireContext())

        var focusRequested = false
        viewModel.albums.observe(viewLifecycleOwner) { albums ->
            val album = albums.find { it.id == albumId } ?: return@observe
            tvTitle.text  = album.name
            tvAlbum.text  = album.name
            tvArtist.text = album.artist
            val yearStr   = if (album.year > 0) "${album.year} · " else ""
            val songWord  = if (album.songCount == 1) "song" else "songs"
            tvMeta.text   = "$yearStr${album.songCount} $songWord"

            val loaded = try {
                requireContext().contentResolver.openInputStream(album.albumArtUri)?.use { true } ?: false
            } catch (_: Exception) { false }
            if (loaded) ivArt.setImageURI(album.albumArtUri)
            else { ivArt.setImageURI(null); ivArt.setImageResource(R.drawable.ic_music_note) }

            adapter.updateTracks(album.songs)
            if (!focusRequested && album.songs.isNotEmpty()) {
                focusRequested = true
                recycler.post {
                    val first = recycler.layoutManager?.findViewByPosition(0) ?: recycler.getChildAt(0)
                    (first?.findViewById<View?>(R.id.clickable_item) ?: first)?.requestFocus()
                }
            }
        }
    }
}
