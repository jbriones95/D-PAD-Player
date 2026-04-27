package com.example.dpadplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class AlbumAdapter(
    private var items: List<Album>,
    private val onAlbumClick: (Album) -> Unit
) : RecyclerView.Adapter<AlbumAdapter.VH>() {

    fun update(newItems: List<Album>) {
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
        val art: ImageView  = view.findViewById(R.id.iv_album_art)
        val name: TextView  = view.findViewById(R.id.tv_album_name)
        val info: TextView  = view.findViewById(R.id.tv_album_info)
        init {
            val clickable = view.findViewById<View>(R.id.clickable_item) ?: view
            applyItemFocusBackground(clickable)
            clickable.setOnClickListener { onAlbumClick(items[bindingAdapterPosition]) }
            clickable.setupDpadItem { onAlbumClick(items[bindingAdapterPosition]) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_album, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val album = items[position]
        holder.name.text = album.name
        val songWord = if (album.songCount == 1) "song" else "songs"
        holder.info.text = buildString {
            append(album.artist)
            if (album.year > 0) append(" · ${album.year}")
            append(" · ${album.songCount} $songWord")
        }
        Glide.with(holder.itemView)
            .load(album.albumArtUri)
            .placeholder(R.drawable.ic_music_note)
            .error(R.drawable.ic_music_note)
            .fallback(R.drawable.ic_music_note)
            .into(holder.art)
    }
}
