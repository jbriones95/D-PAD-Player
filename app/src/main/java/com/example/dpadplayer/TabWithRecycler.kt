package com.example.dpadplayer

import androidx.recyclerview.widget.RecyclerView

/** Implement on tab fragments that host a RecyclerView so LibraryFragment can move focus into them. */
interface TabWithRecycler {
    fun recyclerView(): RecyclerView?
    /** Request initial focus into the recycler (first focusable child). */
    fun requestInitialFocus() {}
}
