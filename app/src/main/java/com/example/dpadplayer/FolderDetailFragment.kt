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
import java.io.File

class FolderDetailFragment : Fragment() {

    companion object {
        private const val ARG_FOLDER_PATH = "folder_path"

        fun newInstance(folderPath: String) = FolderDetailFragment().apply {
            arguments = Bundle().also { it.putString(ARG_FOLDER_PATH, folderPath) }
        }
    }

    private val viewModel: MusicViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        inflater.inflate(R.layout.fragment_folder_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val folderPath = arguments?.getString(ARG_FOLDER_PATH)?.takeIf { it.isNotBlank() } ?: return

        val btnBack = view.findViewById<MaterialButton>(R.id.btn_back)
        val tvFolderName = view.findViewById<TextView>(R.id.tv_folder_name)
        val tvEmpty = view.findViewById<TextView>(R.id.tv_empty)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_folder)

        btnBack.setOnClickListener { parentFragmentManager.popBackStack() }
        applyPlayerControlFocusBackground(btnBack)
        btnBack.setupDpadItem(onFocusChanged = materialButtonFocusChangeHandler(btnBack)) {
            parentFragmentManager.popBackStack()
        }

        tvFolderName.text = File(folderPath).name.ifBlank { folderPath }

        val adapter = TrackAdapter(
            items = emptyList(),
            onTrackClick = { index ->
                val tracks = tracksForFolder(folderPath)
                if (tracks.isNotEmpty()) {
                    (activity as? MainActivity)?.playTracks(tracks, index.coerceIn(0, tracks.size - 1))
                }
            },
            onMenuClick = { anchor, track, _ ->
                (activity as? MainActivity)?.showTrackMenu(anchor, track)
            }
        )
        recycler.adapter = adapter
        recycler.layoutManager = FocusLinearLayoutManager(requireContext())

        var focusRequested = false
        viewModel.tracks.observe(viewLifecycleOwner) {
            val folderTracks = tracksForFolder(folderPath)
            adapter.updateTracks(folderTracks)
            tvEmpty.visibility = if (folderTracks.isEmpty()) View.VISIBLE else View.GONE
            recycler.visibility = if (folderTracks.isEmpty()) View.GONE else View.VISIBLE
            if (!focusRequested && folderTracks.isNotEmpty()) {
                focusRequested = true
                recycler.post {
                    val first = recycler.layoutManager?.findViewByPosition(0) ?: recycler.getChildAt(0)
                    (first?.findViewById<View?>(R.id.clickable_item) ?: first)?.requestFocus()
                }
            }
        }

        viewModel.currentIndex.observe(viewLifecycleOwner) { adapter.setSelectedIndex(it) }
    }

    private fun tracksForFolder(folderPath: String): List<com.example.dpadplayer.playback.Track> {
        val normalized = File(folderPath).absolutePath
        val tracks = viewModel.tracks.value ?: emptyList()
        return tracks.filter { t ->
            val p = t.filePath
            if (p.isBlank()) return@filter false
            val parent = File(p).parentFile?.absolutePath ?: return@filter false
            parent == normalized
        }
    }
}
