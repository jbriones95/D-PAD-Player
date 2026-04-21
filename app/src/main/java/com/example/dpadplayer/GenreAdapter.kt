package com.example.dpadplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class GenreAdapter(
    private var items: List<Genre>,
    private val onGenreClick: (Genre) -> Unit
) : RecyclerView.Adapter<GenreAdapter.VH>() {

    fun update(newItems: List<Genre>) {
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
        val name: TextView = view.findViewById(R.id.tv_genre_name)
        val info: TextView = view.findViewById(R.id.tv_genre_info)
        init {
            val clickable = view.findViewById<View>(R.id.clickable_item) ?: view
            applyItemFocusBackground(clickable)
            clickable.setOnClickListener { onGenreClick(items[bindingAdapterPosition]) }
            clickable.setupDpadItem { onGenreClick(items[bindingAdapterPosition]) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_genre, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val genre = items[position]
        holder.name.text = genre.name
        val songWord = if (genre.songCount == 1) "song" else "songs"
        holder.info.text = "${genre.songCount} $songWord"
    }
}
