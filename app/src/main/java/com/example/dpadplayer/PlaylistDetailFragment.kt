package com.example.dpadplayer

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.dpadplayer.db.PlaylistEntity
import com.google.android.material.button.MaterialButton

/**
 * Shows songs in a single playlist with rename/delete options.
 * Pass [ARG_PLAYLIST_ID] as the playlist's Room ID.
 */
class PlaylistDetailFragment : Fragment() {

    companion object {
        const val ARG_PLAYLIST_ID = "playlist_id"
        fun newInstance(playlistId: Long) = PlaylistDetailFragment().apply {
            arguments = Bundle().also { it.putLong(ARG_PLAYLIST_ID, playlistId) }
        }
    }

    private val viewModel: MusicViewModel by activityViewModels()
    private var playlistId = -1L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        inflater.inflate(R.layout.fragment_playlist_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        playlistId = arguments?.getLong(ARG_PLAYLIST_ID, -1L) ?: -1L
        if (playlistId < 0) return

        val btnBack    = view.findViewById<MaterialButton>(R.id.btn_back)
        val tvTitle    = view.findViewById<TextView>(R.id.tv_playlist_title)
        val btnMenu    = view.findViewById<MaterialButton>(R.id.btn_playlist_menu)
        val recycler   = view.findViewById<RecyclerView>(R.id.recycler_playlist)

        btnBack.setOnClickListener { parentFragmentManager.popBackStack() }
        applyItemFocusBackground(btnBack)
        btnBack.setupDpadItem { parentFragmentManager.popBackStack() }

        val adapter = TrackAdapter(
            items = emptyList(),
            onTrackClick = { index ->
                // play resolved tracks starting at index
                (activity as? MainActivity)?.playPlaylist(playlistId, index)
            }
        )
        adapter.menuClickListener = { anchor, track, _ ->
            val popup = android.widget.PopupMenu(requireContext(), anchor)
            popup.menu.add(0, 1, 0, "Remove from playlist")
            popup.setOnMenuItemClickListener { item ->
                if (item.itemId == 1) viewModel.removeSongFromPlaylist(playlistId, track.id)
                true
            }
            popup.show()
        }
        recycler.adapter = adapter
        recycler.layoutManager = FocusLinearLayoutManager(requireContext())

        var focusRequested = false

        // Observe playlists for name updates
        viewModel.playlists.observe(viewLifecycleOwner) { list ->
            val pl = list.find { it.id == playlistId }
            tvTitle.text = pl?.name ?: "Playlist"
        }

        // Observe playlist songs
        viewModel.observePlaylistTracks(playlistId).observe(viewLifecycleOwner) { tracks ->
            adapter.updateTracks(tracks)
            if (!focusRequested && tracks.isNotEmpty()) {
                focusRequested = true
                recycler.post {
                    val first = recycler.layoutManager?.findViewByPosition(0) ?: recycler.getChildAt(0)
                    (first?.findViewById<View?>(R.id.clickable_item) ?: first)?.requestFocus()
                }
            }
        }

        // Overflow menu: rename / delete
        applyItemFocusBackground(btnMenu)
        btnMenu.setupDpadItem {
            val popup = PopupMenu(requireContext(), btnMenu)
            popup.menu.add(0, 1, 0, "Rename")
            popup.menu.add(0, 2, 1, "Delete playlist")
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    1 -> showRenameDialog()
                    2 -> confirmDelete()
                }
                true
            }
            popup.show()
        }
        btnMenu.setOnClickListener { anchor ->
            val popup = PopupMenu(requireContext(), anchor)
            popup.menu.add(0, 1, 0, "Rename")
            popup.menu.add(0, 2, 1, "Delete playlist")
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    1 -> showRenameDialog()
                    2 -> confirmDelete()
                }
                true
            }
            popup.show()
        }
    }

    private fun showRenameDialog() {
        val editText = EditText(requireContext())
        val current = viewModel.playlists.value?.find { it.id == playlistId }?.name ?: ""
        editText.setText(current)
        editText.selectAll()
        editText.requestFocus()
        AlertDialog.Builder(requireContext())
            .setTitle("Rename playlist")
            .setView(editText)
            .setPositiveButton("Rename") { _, _ ->
                val newName = editText.text.toString().trim()
                if (newName.isNotEmpty()) viewModel.renamePlaylist(playlistId, newName)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun confirmDelete() {
        val name = viewModel.playlists.value?.find { it.id == playlistId }?.name ?: "this playlist"
        AlertDialog.Builder(requireContext())
            .setTitle("Delete \"$name\"?")
            .setMessage("This cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deletePlaylist(playlistId)
                parentFragmentManager.popBackStack()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
