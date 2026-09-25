package com.example.dpadplayer

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
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

        tvTitle.text = getString(R.string.title_now_playing_queue)

        btnBack.setOnClickListener { parentFragmentManager.popBackStack() }
        applyPlayerControlFocusBackground(btnBack)
        btnBack.setupDpadItem(onFocusChanged = materialButtonFocusChangeHandler(btnBack)) {
            parentFragmentManager.popBackStack()
        }

        val activity = activity as? MainActivity

        val adapter = TrackAdapter(
            items = emptyList(),
            isQueue = true,
            onTrackClick = { index ->
                activity?.playQueueItem(index)
            },
            onMenuClick = { anchor, track, _ ->
                activity?.showTrackMenu(anchor, track)
            }
        )
        
        recycler.adapter = adapter
        recycler.layoutManager = FocusLinearLayoutManager(requireContext())

        val touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val from = viewHolder.bindingAdapterPosition
                val to = target.bindingAdapterPosition
                if (from == RecyclerView.NO_POSITION || to == RecyclerView.NO_POSITION) return false
                if (!adapter.moveItem(from, to)) return false
                activity?.moveQueueItem(from, to)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = Unit
            override fun isLongPressDragEnabled(): Boolean = true
        })
        touchHelper.attachToRecyclerView(recycler)

        // D-pad reordering: LEFT/RIGHT on a focused row moves it within the
        // queue. UP/DOWN keep their normal "move between rows" meaning so the
        // list stays predictable, while LEFT/RIGHT are otherwise unused here.
        recycler.setOnKeyListener { _, keyCode, event ->
            if (event.action != KeyEvent.ACTION_DOWN) return@setOnKeyListener false
            if (keyCode != KeyEvent.KEYCODE_DPAD_LEFT && keyCode != KeyEvent.KEYCODE_DPAD_RIGHT) {
                return@setOnKeyListener false
            }

            val focused = recycler.findFocus() ?: return@setOnKeyListener false
            var child: View = focused
            while (child.parent != recycler && child.parent is View) child = child.parent as View
            val from = recycler.getChildAdapterPosition(child)
            if (from == RecyclerView.NO_POSITION) return@setOnKeyListener false

            val count = adapter.itemCount
            val to = if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) from - 1 else from + 1
            if (to !in 0 until count) {
                // Already at the edge — still swallow the key so focus doesn't
                // jump out of the list into the surrounding chrome.
                return@setOnKeyListener true
            }
            if (!adapter.moveItem(from, to)) return@setOnKeyListener true
            activity?.moveQueueItem(from, to)
            recycler.post {
                val moved = recycler.layoutManager?.findViewByPosition(to)
                (moved?.findViewById<View?>(R.id.clickable_item) ?: moved)?.requestFocus()
            }
            activity?.showMessage(
                getString(R.string.msg_moved_queue_item, adapter.itemAt(to)?.title ?: "", to + 1)
            )
            true
        }

        // Focus once the queue actually has rows. Keyed off "first non-empty"
        // rather than "first emission" so opening the queue before playback
        // starts still lands focus on a row when tracks finally arrive.
        var focusSeeded = false
        viewModel.queue.observe(viewLifecycleOwner) { q ->
            adapter.updateTracks(q)
            bindListState(
                root = view,
                itemCount = q.size,
                isLoading = false,
                isSearching = false,
                query = "",
                emptyTitle = getString(R.string.empty_title_queue_empty),
                emptyBody = getString(R.string.empty_body_queue_empty),
                emptyIcon = R.drawable.ic_queue_music
            )
            if (!focusSeeded && q.isNotEmpty()) {
                focusSeeded = true
                val targetIndex = (viewModel.currentIndex.value ?: 0).coerceIn(0, q.lastIndex)
                recycler.post {
                    recycler.scrollToPosition(targetIndex)
                    recycler.post {
                        val target = recycler.layoutManager?.findViewByPosition(targetIndex)
                            ?: recycler.getChildAt(0)
                        (target?.findViewById<View?>(R.id.clickable_item) ?: target)?.requestFocus()
                    }
                }
            }
        }

        // Focus the currently playing track highlight
        viewModel.currentIndex.observe(viewLifecycleOwner) { currentIndex ->
            adapter.setSelectedIndex(currentIndex)
        }
    }
}
