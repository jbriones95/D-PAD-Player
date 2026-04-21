package com.example.dpadplayer

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * LinearLayoutManager that:
 *  1. Scrolls the D-pad focused item into view (keeps it visible without forcing it to the top).
 *  2. Returns true from onRequestChildFocus so the RecyclerView does not try to do its own
 *     (often broken) focus scroll.
 */
class FocusLinearLayoutManager(context: Context) : LinearLayoutManager(context) {

    override fun onRequestChildFocus(
        parent: RecyclerView,
        state: RecyclerView.State,
        child: View,
        focused: View?
    ): Boolean {
        // Bring child into view without snapping to offset 0.
        // scrollToPosition() ensures visibility; if already visible it's a no-op.
        val position = getPosition(child)
        if (position != RecyclerView.NO_POSITION) {
            scrollToPosition(position)
        }
        return true   // consume — prevent RecyclerView's own scroll
    }

    override fun onInterceptFocusSearch(focused: View, direction: Int): View? = null
}
