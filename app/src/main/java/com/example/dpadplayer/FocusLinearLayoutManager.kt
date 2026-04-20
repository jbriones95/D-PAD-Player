package com.example.dpadplayer

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/** Scrolls to keep the D-pad focused item visible automatically. */
class FocusLinearLayoutManager(context: Context) : LinearLayoutManager(context) {
    override fun onRequestChildFocus(
        parent: RecyclerView,
        state: RecyclerView.State,
        child: View,
        focused: View?
    ): Boolean {
        val position = getPosition(child)
        if (position != RecyclerView.NO_POSITION) {
            scrollToPositionWithOffset(position, 0)
        }
        return super.onRequestChildFocus(parent, state, child, focused)
    }
}
