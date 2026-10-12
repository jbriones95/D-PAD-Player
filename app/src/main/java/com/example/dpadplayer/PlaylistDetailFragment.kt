package com.example.dpadplayer

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.dpadplayer.db.PlaylistEntity
import com.example.dpadplayer.playback.Track
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope

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
    private val importM3uLauncher =
        registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) importM3u(uri)
        }
    private val exportM3uLauncher =
        registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.CreateDocument("audio/x-mpegurl")) { uri ->
            if (uri != null) exportM3u(uri)
        }

    private fun launchM3uPicker() {
        importM3uLauncher.launch(arrayOf("audio/x-mpegurl", "audio/mpegurl", "application/vnd.apple.mpegurl", "*/*"))
    }

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
        applyPlayerControlFocusBackground(btnBack)
        btnBack.setupDpadItem(onFocusChanged = materialButtonFocusChangeHandler(btnBack)) {
            parentFragmentManager.popBackStack()
        }

        val adapter = TrackAdapter(
            items = emptyList(),
            onTrackClick = { index ->
                (activity as? MainActivity)?.playPlaylist(playlistId, index)
            },
            onMenuClick = { anchor, track, _ ->
                (activity as? MainActivity)?.showTrackMenu(anchor, track)
            }
        )
        var playlistTracks: List<Track> = emptyList()
        fun updatePlayingTrack() {
            val currentTrackId = (activity as? MainActivity)?.currentTrack()?.id
            adapter.setSelectedIndex(
                if (currentTrackId == null) -1 else playlistTracks.indexOfFirst { it.id == currentTrackId }
            )
        }
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
        viewModel.currentIndex.observe(viewLifecycleOwner) { updatePlayingTrack() }
        viewModel.queue.observe(viewLifecycleOwner) { updatePlayingTrack() }

        // Observe playlist songs
        viewModel.observePlaylistTracks(playlistId).observe(viewLifecycleOwner) { tracks ->
            playlistTracks = tracks
            adapter.updateTracks(tracks)
            updatePlayingTrack()
            bindListState(
                root = view,
                itemCount = tracks.size,
                isLoading = false,
                isSearching = false,
                query = "",
                emptyTitle = getString(R.string.empty_title_playlist_empty),
                emptyBody = getString(R.string.empty_body_playlist_empty),
                emptyIcon = R.drawable.ic_playlist_play,
                actionLabel = getString(R.string.menu_import_m3u),
                onAction = { launchM3uPicker() }
            )
            if (!focusRequested) {
                focusRequested = true
                recycler.post {
                    if (tracks.isEmpty()) {
                        val importAction = view.findViewById<View>(R.id.state_action)
                        if (importAction.visibility == View.VISIBLE && importAction.requestFocus()) return@post
                    }
                    val first = recycler.layoutManager?.findViewByPosition(0) ?: recycler.getChildAt(0)
                    (first?.findViewById<View?>(R.id.clickable_item) ?: first)?.requestFocus()
                }
            }
        }

        // Overflow menu: rename / delete / import / export
        applyPlayerControlFocusBackground(btnMenu)
        btnMenu.setupDpadItem(onFocusChanged = materialButtonFocusChangeHandler(btnMenu)) {
            showOverflowMenu(btnMenu)
        }
        btnMenu.setOnClickListener { anchor -> showOverflowMenu(anchor) }
    }

    private fun showOverflowMenu(anchor: android.view.View) {
        val popup = PopupMenu(requireContext(), anchor)
        popup.menu.add(0, 1, 0, getString(R.string.action_rename))
        popup.menu.add(0, 2, 1, getString(R.string.menu_delete_playlist))
        popup.menu.add(0, 3, 2, getString(R.string.menu_import_m3u))
        popup.menu.add(0, 4, 3, getString(R.string.menu_export_m3u))
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> showRenameDialog()
                2 -> confirmDelete()
                3 -> launchM3uPicker()
                4 -> {
                    val name = viewModel.playlists.value?.find { it.id == playlistId }?.name
                        ?: getString(R.string.menu_playlists)
                    exportM3uLauncher.launch("$name.m3u")
                }
            }
            true
        }
        popup.show()
    }

    private fun importM3u(uri: android.net.Uri) {
        val ctx = context ?: return
        lifecycleScope.launch {
            val content = PlaylistIo.readM3uFromUri(ctx, uri)
            if (content.isNullOrBlank()) {
                toast(getString(R.string.msg_m3u_read_failed))
                return@launch
            }
            val parsed = PlaylistIo.parseM3u(content)
            val allTracks = viewModel.tracks.value ?: emptyList()
            val resolved = PlaylistIo.resolveEntriesToTracks(parsed.entries, allTracks)
            if (resolved.isEmpty()) {
                toast(getString(R.string.msg_m3u_no_matches))
                return@launch
            }
            viewModel.rewritePlaylist(playlistId, resolved)
            toast(resources.getQuantityString(R.plurals.msg_m3u_imported, resolved.size, resolved.size))
        }
    }

    private fun exportM3u(uri: android.net.Uri) {
        val ctx = context ?: return
        lifecycleScope.launch {
            val tracks = viewModel.resolvePlaylistTracks(playlistId)
            val name = viewModel.playlists.value?.find { it.id == playlistId }?.name
                ?: getString(R.string.menu_playlists)
            val content = PlaylistIo.buildM3uContent(name, tracks)
            val ok = PlaylistIo.writeM3uToUri(ctx, uri, content)
            toast(getString(if (ok) R.string.msg_m3u_exported else R.string.msg_m3u_export_failed))
        }
    }

    private fun showRenameDialog() {
        val editText = EditText(requireContext())
        val current = viewModel.playlists.value?.find { it.id == playlistId }?.name ?: ""
        editText.setText(current)
        editText.selectAll()
        editText.requestFocus()
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_rename_playlist_title)
            .setView(editText)
            .setPositiveButton(R.string.action_rename) { _, _ ->
                val newName = editText.text.toString().trim()
                if (newName.isEmpty()) {
                    toast(getString(R.string.msg_playlist_rename_empty))
                } else if (newName != current) {
                    viewModel.renamePlaylist(playlistId, newName)
                    toast(getString(R.string.msg_playlist_renamed, newName))
                }
            }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }

    private fun confirmDelete() {
        val name = viewModel.playlists.value?.find { it.id == playlistId }?.name ?: ""
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_delete_playlist_title)
            .setMessage(getString(R.string.dialog_delete_playlist_message, name))
            .setPositiveButton(R.string.dialog_delete) { _, _ ->
                viewModel.deletePlaylist(playlistId)
                parentFragmentManager.popBackStack()
                toast(getString(R.string.msg_playlist_deleted, name))
            }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }

    private fun toast(message: CharSequence) {
        (activity as? MainActivity)?.showMessage(message)
            ?: Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
