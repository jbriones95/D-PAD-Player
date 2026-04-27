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

class QueueFragment : Fragment() {

    private val viewModel: MusicViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        inflater.inflate(R.layout.fragment_queue, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val btnBack = view.findViewById<MaterialButton>(R.id.btn_back)
        val tvTitle = view.findViewById<TextView>(R.id.tv_detail_title)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_queue)

        tvTitle.text = "Now Playing Queue"

        btnBack.setOnClickListener { parentFragmentManager.popBackStack() }
        applyPlayerControlFocusBackground(btnBack)
        btnBack.setupDpadItem(onFocusChanged = materialButtonFocusChangeHandler(btnBack)) {
            parentFragmentManager.popBackStack()
        }

        val activity = activity as? MainActivity
        val queue = activity?.getQueue() ?: emptyList()

        val adapter = TrackAdapter(
            items = queue,
            onTrackClick = { index ->
                activity?.playQueueItem(index)
            },
            onMenuClick = { anchor, track, _ ->
                activity?.showTrackMenu(anchor, track)
            }
        )
        
        recycler.adapter = adapter
        recycler.layoutManager = FocusLinearLayoutManager(requireContext())

        // Focus the currently playing track
        viewModel.currentIndex.observe(viewLifecycleOwner) { currentIndex ->
            adapter.setSelectedIndex(currentIndex)
        }

        if (queue.isNotEmpty()) {
            recycler.post {
                val targetIndex = viewModel.currentIndex.value ?: 0
                recycler.scrollToPosition(targetIndex)
                val first = recycler.layoutManager?.findViewByPosition(targetIndex) ?: recycler.getChildAt(0)
                (first?.findViewById<View?>(R.id.clickable_item) ?: first)?.requestFocus()
            }
        }
    }
}
