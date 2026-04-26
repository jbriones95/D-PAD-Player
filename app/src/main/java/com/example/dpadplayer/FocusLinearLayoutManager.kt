package com.example.dpadplayer

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * LinearLayoutManager with reliable D-pad focus behaviour for Android TV / D-pad remotes.
 *
 * What it does:
 *  1. onRequestChildFocus — scrolls the focused item into view smoothly.
 *  2. onInterceptFocusSearch — when navigating UP/DOWN, finds the next item's
 *     clickable_item overlay (or the item root) and returns it, so the framework
 *     moves focus there directly instead of doing its own unpredictable search.
 */
class FocusLinearLayoutManager(context: Context) : LinearLayoutManager(context) {

    override fun onRequestChildFocus(
        parent: RecyclerView,
        state: RecyclerView.State,
        child: View,
        focused: View?
    ): Boolean {
        val position = getPosition(child)
        if (position != RecyclerView.NO_POSITION) {
            scrollToPosition(position)
        }
        return true
    }

    override fun onInterceptFocusSearch(focused: View, direction: Int): View? {
        if (direction != View.FOCUS_DOWN && direction != View.FOCUS_UP) return null

        // Find the RecyclerView that uses THIS layout manager as parent of focused
        val rv = findOwnerRecyclerView(focused) ?: return null

        // Walk up from focused view to find the direct RV child that contains it
        var itemView: View = focused
        while (itemView.parent !== rv) {
            itemView = (itemView.parent as? View) ?: return null
        }

        val currentPos = rv.getChildAdapterPosition(itemView)
        if (currentPos == RecyclerView.NO_POSITION) return null

        val nextPos = when (direction) {
            View.FOCUS_DOWN -> currentPos + 1
            View.FOCUS_UP   -> currentPos - 1
            else -> return null
        }

        val itemCount = rv.adapter?.itemCount ?: return null
        if (nextPos < 0 || nextPos >= itemCount) return null

        val nextView = findViewByPosition(nextPos) ?: return null
        return nextView.findViewById<View?>(R.id.clickable_item) ?: nextView
    }

    /** Walks up the view hierarchy to find the RecyclerView that owns this layout manager. */
    private fun findOwnerRecyclerView(view: View): RecyclerView? {
        var v: View? = view
        while (v != null) {
            if (v is RecyclerView && v.layoutManager === this) return v
            v = v.parent as? View
        }
        return null
    }
}
