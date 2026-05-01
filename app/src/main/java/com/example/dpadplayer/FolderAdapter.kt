package com.example.dpadplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FolderAdapter(
    private var items: List<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<FolderAdapter.VH>() {

    fun update(newItems: List<String>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tv_item_title)
        val clickable: View = view.findViewById(R.id.clickable_item)
        init {
            clickable.setOnClickListener { onClick(items[bindingAdapterPosition]) }
            clickable.setupDpadItem { onClick(items[bindingAdapterPosition]) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.title.text = items[position]
    }

    override fun getItemCount() = items.size
}
