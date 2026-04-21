package com.example.dpadplayer

import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dpadplayer.playback.Track

class TrackAdapter(
    private var items: List<Track>,
    private val onTrackClick: (Int) -> Unit,
    private val onTrackLongClick: ((Int) -> Boolean)? = null,
    /** Optional: override the popup menu items. If null, default (Add to playlist) is used. */
    private val onMenuClick: ((anchor: View, track: Track, index: Int) -> Unit)? = null
) : RecyclerView.Adapter<TrackAdapter.VH>() {

    private var selectedIndex = -1

    /** Called from outside to bind a popup-menu handler. */
    var menuClickListener: ((anchor: View, track: Track, index: Int) -> Unit)? = onMenuClick

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
        val menuBtn: ImageView = view.findViewById(R.id.btn_track_menu)
        val indicator: View    = view.findViewById(R.id.playing_indicator)

        init {
            applyItemFocusBackground(view)
            view.setOnClickListener { onTrackClick(bindingAdapterPosition) }
            view.setOnLongClickListener {
                onTrackLongClick?.invoke(bindingAdapterPosition) ?: false
            }
            view.setupDpadItem { onTrackClick(bindingAdapterPosition) }
            menuBtn.setOnClickListener { v ->
                val pos = bindingAdapterPosition
                if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
                val track = items[pos]
                val listener = menuClickListener
                if (listener != null) listener(v, track, pos)
                else showDefaultMenu(v, track)
            }
            // Also trigger menu on long-press Enter when btn_track_menu is focused
            menuBtn.setOnKeyListener { v, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN &&
                    (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER)) {
                    v.performClick()
                    true
                } else false
            }
        }

        private fun showDefaultMenu(anchor: View, track: Track) {
            // Default menu: Add to playlist (handled by parent if they set menuClickListener)
            // Fallback — show nothing; fragments should set menuClickListener
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val track = items[position]
        holder.title.text  = track.title
        // Show "Artist · 3:45" in secondary line
        holder.artist.text = "${track.artist} · ${formatMs(track.duration)}"

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
