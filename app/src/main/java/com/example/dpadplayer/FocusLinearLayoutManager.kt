package com.example.dpadplayer

import android.content.Context
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FocusLinearLayoutManager(context: Context) : LinearLayoutManager(context) {

    /** Called whenever a child at [position] receives focus. Use to track last-focused position. */
    var onFocusPosition: ((position: Int) -> Unit)? = null

    override fun onRequestChildFocus(
        parent: RecyclerView,
        state: RecyclerView.State,
        child: View,
        focused: View?
    ): Boolean {
        val position = getPosition(child)
        val rvName = parent.context.resources.getResourceEntryName(parent.id)
        Log.d("DPAD_FOCUS", "onRequestChildFocus pos=$position rv=$rvName focused=${runCatching { focused?.let { it.context.resources.getResourceEntryName(it.id) } }.getOrNull()}")
        if (position != RecyclerView.NO_POSITION) {
            scrollToPosition(position)
            onFocusPosition?.invoke(position)
        }
        return true
    }

    override fun onInterceptFocusSearch(focused: View, direction: Int): View? {
        if (direction != View.FOCUS_DOWN && direction != View.FOCUS_UP) return null

        val rv = findOwnerRecyclerView(focused) ?: run {
            Log.d("DPAD_FOCUS", "onInterceptFocusSearch: no owner RV found for ${focused.id}")
            return null
        }

        var itemView: View = focused
        while (itemView.parent !== rv) {
            itemView = (itemView.parent as? View) ?: return null
        }

        val currentPos = rv.getChildAdapterPosition(itemView)
        if (currentPos == RecyclerView.NO_POSITION) {
            Log.d("DPAD_FOCUS", "onInterceptFocusSearch: NO_POSITION for itemView")
            return null
        }

        val nextPos = when (direction) {
            View.FOCUS_DOWN -> currentPos + 1
            View.FOCUS_UP   -> currentPos - 1
            else -> return null
        }

        val itemCount = rv.adapter?.itemCount ?: return null
        if (nextPos < 0 || nextPos >= itemCount) {
            Log.d("DPAD_FOCUS", "onInterceptFocusSearch: nextPos=$nextPos out of bounds (count=$itemCount) → boundary")
            // Allow default focus search to take over when we hit list bounds
            return null
        }

        val nextView = findViewByPosition(nextPos) ?: run {
            Log.d("DPAD_FOCUS", "onInterceptFocusSearch: nextPos=$nextPos not yet laid out")
            return null
        }
        val target = nextView.findViewById<View?>(R.id.clickable_item) ?: nextView
        Log.d("DPAD_FOCUS", "onInterceptFocusSearch: $currentPos→$nextPos dir=${dirName(direction)} target=${target.id}")
        return target
    }

    private fun findOwnerRecyclerView(view: View): RecyclerView? {
        var v: View? = view
        while (v != null) {
            if (v is RecyclerView && v.layoutManager === this) return v
            v = v.parent as? View
        }
        return null
    }

    private fun dirName(d: Int) = when (d) {
        View.FOCUS_UP -> "UP"; View.FOCUS_DOWN -> "DOWN"
        View.FOCUS_LEFT -> "LEFT"; View.FOCUS_RIGHT -> "RIGHT"
        else -> d.toString()
    }
}
