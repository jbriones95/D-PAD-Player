package com.example.dpadplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class ArtistAdapter(
    private var items: List<Artist>,
    private val onArtistClick: (Artist) -> Unit
) : RecyclerView.Adapter<ArtistAdapter.VH>() {

    fun update(newItems: List<Artist>) {
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
        val name: TextView = view.findViewById(R.id.tv_artist_name)
        val info: TextView = view.findViewById(R.id.tv_artist_info)
        init { view.setOnClickListener { onArtistClick(items[bindingAdapterPosition]) }
               view.setOnFocusChangeListener { v, f -> v.isSelected = f } }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_artist, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val artist = items[position]
        holder.name.text = artist.name
        val albumWord = if (artist.albumCount == 1) "album" else "albums"
        val songWord  = if (artist.songs.size == 1) "song" else "songs"
        holder.info.text = "${artist.albumCount} $albumWord · ${artist.songs.size} $songWord"
    }
}
