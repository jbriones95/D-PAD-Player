package com.example.dpadplayer

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

/**
 * iPod-style home screen: a simple list of music categories.
 * Each row navigates to the corresponding list screen.
 */
class HomeFragment : Fragment() {

    private val viewModel: MusicViewModel by activityViewModels()

    private lateinit var miniPlayer: MiniPlayerBinder
    private lateinit var librarySummary: TextView

    data class MenuItem(val labelRes: Int, val iconRes: Int, val tag: String)

    private var lastFocusedPos = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lastFocusedPos = viewModel.homeMenuFocusPos
        librarySummary = view.findViewById(R.id.tv_home_subtitle)

        val menuItems = listOf(
            MenuItem(R.string.menu_songs,     R.drawable.ic_music_note,    "songs"),
            MenuItem(R.string.menu_albums,    R.drawable.ic_album,         "albums"),
            MenuItem(R.string.menu_artists,   R.drawable.ic_account_circle,"artists"),
            MenuItem(R.string.menu_genres,    R.drawable.ic_genre,         "genres"),
            MenuItem(R.string.menu_playlists, R.drawable.ic_queue_music,   "playlists"),
            MenuItem(R.string.menu_settings,  R.drawable.ic_more_vert,     "settings"),
        )

        val recycler = view.findViewById<RecyclerView>(R.id.recycler_menu)
        val lm = FocusLinearLayoutManager(requireContext())
        lm.onFocusPosition = {
            lastFocusedPos = it
            viewModel.homeMenuFocusPos = it
        }
        recycler.layoutManager = lm
        recycler.adapter = HomeMenuAdapter(menuItems) { item ->
            when (item.tag) {
                "songs"     -> (activity as? MainActivity)?.openLibraryTab(0)
                "albums"    -> (activity as? MainActivity)?.openLibraryTab(1)
                "artists"   -> (activity as? MainActivity)?.openLibraryTab(2)
                "genres"    -> (activity as? MainActivity)?.openLibraryTab(3)
                "playlists" -> (activity as? MainActivity)?.openLibraryTab(4)
                "settings"  -> (activity as? MainActivity)?.openSettings()
            }
        }

        miniPlayer = MiniPlayerBinder(view.findViewById(R.id.mini_player), requireActivity() as MainActivity)
            .attach(R.id.recycler_menu) { focusMenuItem(recycler) }

        // Request focus on the last-focused item once the recycler is laid out
        focusMenuItem(recycler)

        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        val recycler = view?.findViewById<RecyclerView>(R.id.recycler_menu) ?: return
        focusMenuItem(recycler)
    }

    private fun focusMenuItem(recycler: RecyclerView) {
        fun requestFocusNow() {
            if (!isAdded || view == null || !isVisible) return
            val child = recycler.findViewHolderForAdapterPosition(lastFocusedPos)?.itemView
                ?: recycler.findViewHolderForAdapterPosition(0)?.itemView
            val target = child?.findViewById<View>(R.id.clickable_item) ?: child
            target?.requestFocus()
        }
        if (recycler.isLaidOut) {
            requestFocusNow()
        } else {
            recycler.post {
                if (!isAdded || view == null || !isVisible) return@post
                requestFocusNow()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            librarySummary.text = resources.getQuantityString(
                R.plurals.home_track_count,
                tracks.size,
                tracks.size
            )
        }
        viewModel.currentIndex.observe(viewLifecycleOwner) { refreshMiniPlayer() }
        viewModel.isPlaying.observe(viewLifecycleOwner)   { playing ->
            miniPlayer.setPlaying(playing)
            refreshMiniPlayer()
        }
        viewModel.position.observe(viewLifecycleOwner)    { pos -> miniPlayer.setPosition(pos) }
    }

    private fun refreshMiniPlayer() {
        val activity = activity as? MainActivity ?: return
        miniPlayer.bind(
            track = activity.currentTrack(),
            isPlaying = viewModel.isPlaying.value == true,
            positionMs = viewModel.position.value ?: 0L
        )
    }
}

// ── Home menu adapter ─────────────────────────────────────────────────────────

class HomeMenuAdapter(
    private val items: List<HomeFragment.MenuItem>,
    private val onClick: (HomeFragment.MenuItem) -> Unit
) : RecyclerView.Adapter<HomeMenuAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView  = view.findViewById(R.id.iv_menu_icon)
        val label: TextView  = view.findViewById(R.id.tv_menu_label)
        val clickable: View = view.findViewById(R.id.clickable_item) ?: view

        init {
            applyItemFocusBackground(clickable)
            clickable.setOnClickListener { onClick(items[bindingAdapterPosition]) }
            clickable.setupDpadItem { onClick(items[bindingAdapterPosition]) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_home_menu, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.label.setText(item.labelRes)
        holder.icon.setImageResource(item.iconRes)
        holder.clickable.announceAs(holder.label.text)
    }

    override fun getItemCount() = items.size
}
