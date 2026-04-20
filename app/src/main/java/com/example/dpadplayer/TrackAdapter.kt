package com.example.dpadplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dpadplayer.playback.Track

class TrackAdapter(
    private var items: List<Track>,
    private val onTrackClick: (Int) -> Unit
) : RecyclerView.Adapter<TrackAdapter.VH>() {

    private var selectedIndex = -1

    fun updateTracks(newItems: List<Track>) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = items.size
            override fun getNewListSize() = newItems.size
            override fun areItemsTheSame(o: Int, n: Int) = items[o].id == newItems[n].id
            override fun areContentsTheSame(o: Int, n: Int) = items[o] == newItems[n]
        })
        items = newItems
        diff.dispatchUpdatesTo(this)
    }

    fun setSelectedIndex(index: Int) {
        val old = selectedIndex
        selectedIndex = index
        if (old >= 0) notifyItemChanged(old)
        if (index >= 0) notifyItemChanged(index)
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val art: ImageView     = view.findViewById(R.id.tv_item_art)
        val title: TextView    = view.findViewById(R.id.tv_item_title)
        val artist: TextView   = view.findViewById(R.id.tv_item_artist)
        val duration: TextView = view.findViewById(R.id.tv_item_duration)
        val indicator: View    = view.findViewById(R.id.playing_indicator)

        init {
            view.setOnClickListener { onTrackClick(bindingAdapterPosition) }
            view.setOnFocusChangeListener { v, hasFocus ->
                v.isSelected = hasFocus
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val track = items[position]
        holder.title.text    = track.title
        holder.artist.text   = track.artist
        holder.duration.text = formatMs(track.duration)

        // Load album art thumbnail; fall back to music note placeholder
        val loaded = try {
            holder.art.context.contentResolver.openInputStream(track.albumArtUri)?.use { true } ?: false
        } catch (_: Exception) { false }
        if (loaded) {
            holder.art.setImageURI(track.albumArtUri)
        } else {
            holder.art.setImageURI(null)
            holder.art.setImageResource(R.drawable.ic_music_note)
        }

        val isActive = position == selectedIndex
        holder.itemView.isActivated = isActive
        holder.indicator.visibility = if (isActive) View.VISIBLE else View.INVISIBLE
    }

    override fun getItemCount() = items.size

    private fun formatMs(ms: Long): String {
        val totalSec = ms / 1000
        val min = totalSec / 60
        val sec = totalSec % 60
        return "%d:%02d".format(min, sec)
    }
}
