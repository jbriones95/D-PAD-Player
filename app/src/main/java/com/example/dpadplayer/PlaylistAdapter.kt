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
    private val onPlaylistClick: (PlaylistEntity) -> Unit,
    /** Called when the "Create playlist" header row is activated. */
    var onCreateClick: (() -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_CREATE = 0
        private const val TYPE_PLAYLIST = 1
    }

    fun update(newItems: List<PlaylistEntity>) {
        val old = items
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = old.size
            override fun getNewListSize() = newItems.size
            override fun areItemsTheSame(o: Int, n: Int) = old[o].id == newItems[n].id
            override fun areContentsTheSame(o: Int, n: Int) = old[o] == newItems[n]
        })
        items = newItems
        // Shift diffs by 1 to account for the header row
        diff.dispatchUpdatesTo(object : androidx.recyclerview.widget.ListUpdateCallback {
            override fun onInserted(position: Int, count: Int) = notifyItemRangeInserted(position + 1, count)
            override fun onRemoved(position: Int, count: Int) = notifyItemRangeRemoved(position + 1, count)
            override fun onMoved(fromPosition: Int, toPosition: Int) = notifyItemMoved(fromPosition + 1, toPosition + 1)
            override fun onChanged(position: Int, count: Int, payload: Any?) = notifyItemRangeChanged(position + 1, count, payload)
        })
    }

    // ── Create-playlist header VH ─────────────────────────────────────────────

    inner class CreateVH(view: View) : RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener { onCreateClick?.invoke() }
            view.setOnFocusChangeListener { v, f -> v.isSelected = f }
        }
    }

    // ── Playlist item VH ──────────────────────────────────────────────────────

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_playlist_name)
        val info: TextView = view.findViewById(R.id.tv_playlist_info)
        init {
            view.setOnClickListener { onPlaylistClick(items[bindingAdapterPosition - 1]) }
            view.setOnFocusChangeListener { v, f -> v.isSelected = f }
        }
    }

    override fun getItemViewType(position: Int) = if (position == 0) TYPE_CREATE else TYPE_PLAYLIST

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_CREATE) {
            CreateVH(inflater.inflate(R.layout.item_create_playlist, parent, false))
        } else {
            VH(inflater.inflate(R.layout.item_playlist, parent, false))
        }
    }

    override fun getItemCount() = items.size + 1  // +1 for header

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is VH) {
            val playlist = items[position - 1]
            holder.name.text = playlist.name
            holder.info.text = ""
        }
        // CreateVH has no data to bind
    }
}
