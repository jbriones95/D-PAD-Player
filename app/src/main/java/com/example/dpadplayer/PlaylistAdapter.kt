package com.example.dpadplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dpadplayer.db.PlaylistEntity

class PlaylistAdapter(
    private var items: List<PlaylistEntity>,
    private val onPlaylistClick: (PlaylistEntity) -> Unit
) : RecyclerView.Adapter<PlaylistAdapter.VH>() {

    fun update(newItems: List<PlaylistEntity>) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = items.size
            override fun getNewListSize() = newItems.size
            override fun areItemsTheSame(o: Int, n: Int) = items[o].id == newItems[n].id
            override fun areContentsTheSame(o: Int, n: Int) = items[o] == newItems[n]
        })
        items = newItems
        diff.dispatchUpdatesTo(this)
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_playlist_name)
        val info: TextView = view.findViewById(R.id.tv_playlist_info)
        init { view.setOnClickListener { onPlaylistClick(items[bindingAdapterPosition]) }
               view.setOnFocusChangeListener { v, f -> v.isSelected = f } }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_playlist, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.name.text = items[position].name
        holder.info.text = ""   // song count filled in by PlaylistDetailFragment when opened
    }
}
