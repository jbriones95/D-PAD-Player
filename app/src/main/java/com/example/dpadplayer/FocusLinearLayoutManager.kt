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

    override fun onInterceptFocusSearch(focused: View, direction: Int): View? {
        // Intercept DPAD_UP / DPAD_DOWN so focus moves between list items predictably.
        // If focus is inside a RecyclerView child, find the next/previous child and return
        // its focusable clickable overlay (if present). Returning a view here tells the
        // framework where to move focus next and prevents jumps.
        val parentRv = findParentRecyclerView(focused) ?: return null

        val childCount = parentRv.childCount
        if (childCount == 0) return null

        // Find the immediate child that contains the focused view
        var currentChild: View? = focused
        while (currentChild != null && currentChild.parent != parentRv) {
            currentChild = currentChild.parent as? View
        }
        val currentIndex = if (currentChild != null) parentRv.getChildAdapterPosition(currentChild) else RecyclerView.NO_POSITION
        if (currentIndex == RecyclerView.NO_POSITION) return null

        val nextIndex = when (direction) {
            View.FOCUS_DOWN -> currentIndex + 1
            View.FOCUS_UP   -> currentIndex - 1
            else -> return null
        }
        if (nextIndex < 0 || nextIndex >= parentRv.adapter?.itemCount ?: childCount) return null

        // Try to find the next child's main focusable overlay (clickable_item) and return it.
        val nextChild = try { parentRv.layoutManager?.findViewByPosition(nextIndex) } catch (_: Exception) { null }
            ?: parentRv.getChildAt(nextIndex)
        if (nextChild == null) return null

        // Look for a view with id clickable_item inside the child, otherwise return the child itself.
        val clickable = nextChild.findViewById<View?>(R.id.clickable_item) ?: nextChild
        return clickable
    }

    private fun findParentRecyclerView(view: View): RecyclerView? {
        var v: View? = view
        while (v != null && v !is RecyclerView) v = v.parent as? View
        return v as? RecyclerView
    }
}
