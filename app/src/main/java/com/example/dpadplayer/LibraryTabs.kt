package com.example.dpadplayer

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter


// ── Songs tab ────────────────────────────────────────────────────────────────

class SongsTabFragment : Fragment(), TabWithRecycler {
    private val viewModel: MusicViewModel by activityViewModels()
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: TrackAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        inflater.inflate(R.layout.fragment_tab_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recycler = view.findViewById(R.id.recycler)
        adapter = TrackAdapter(
            items = emptyList(),
            onTrackClick = { index -> (activity as? MainActivity)?.playTrack(index) }
        )
        adapter.menuClickListener = { anchor, track, _ ->
            showTrackMenu(anchor, track)
        }
        recycler.adapter = adapter
        recycler.layoutManager = FocusLinearLayoutManager(requireContext())

        viewModel.tracks.observe(viewLifecycleOwner) { adapter.updateTracks(it) }
        viewModel.currentIndex.observe(viewLifecycleOwner) { adapter.setSelectedIndex(it) }
    }

    private fun showTrackMenu(anchor: View, track: com.example.dpadplayer.playback.Track) {
        val popup = PopupMenu(requireContext(), anchor)
        popup.menu.add(0, 1, 0, "Add to playlist")
        popup.setOnMenuItemClickListener { item ->
            if (item.itemId == 1) showAddToPlaylistDialog(track)
            true
        }
        popup.show()
    }

    private fun showAddToPlaylistDialog(track: com.example.dpadplayer.playback.Track) {
        val playlists = viewModel.playlists.value ?: emptyList()
        if (playlists.isEmpty()) {
            // No playlists — prompt to create one
            showCreateAndAddDialog(track)
            return
        }
        val options = (playlists.map { it.name } + listOf("+ New playlist")).toTypedArray()
        AlertDialog.Builder(requireContext())
            .setTitle("Add to playlist")
            .setItems(options) { _, which ->
                if (which < playlists.size) {
                    viewModel.addTracksToPlaylist(playlists[which].id, listOf(track))
                    Toast.makeText(requireContext(),
                        "Added to \"${playlists[which].name}\"", Toast.LENGTH_SHORT).show()
                } else {
                    showCreateAndAddDialog(track)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showCreateAndAddDialog(track: com.example.dpadplayer.playback.Track) {
        val editText = EditText(requireContext())
        editText.hint = "Playlist name"
        editText.requestFocus()
        AlertDialog.Builder(requireContext())
            .setTitle("New playlist")
            .setView(editText)
            .setPositiveButton("Create") { _, _ ->
                val name = editText.text.toString().trim()
                if (name.isNotEmpty()) {
                    viewModel.createPlaylist(name, listOf(track))
                    Toast.makeText(requireContext(), "Created \"$name\"", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun recyclerView(): RecyclerView = recycler

    override fun requestInitialFocus() {
        // Try to focus the currently selected track or the first visible child.
        recycler.post {
            val preferred = viewModel.currentIndex.value ?: 0
            val lm = recycler.layoutManager
            var target: View? = null
            try { target = lm?.findViewByPosition(preferred) } catch (_: Exception) { }
            if (target == null && recycler.childCount > 0) target = recycler.getChildAt(0)
            val clickable = target?.findViewById<View?>(R.id.clickable_item) ?: target
            Log.d("DPAD_FOCUS", "SongsTab.requestInitialFocus preferred=$preferred target=${clickable?.id} success=${clickable?.requestFocus()}")
        }
    }
}

// ── Albums tab ───────────────────────────────────────────────────────────────

class AlbumsTabFragment : Fragment(), TabWithRecycler {
    private val viewModel: MusicViewModel by activityViewModels()
    private var recyclerRef: RecyclerView? = null
    private var lastFocusedPos = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        inflater.inflate(R.layout.fragment_tab_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recycler = view.findViewById<RecyclerView>(R.id.recycler)
        recyclerRef = recycler
        val adapter = AlbumAdapter(emptyList()) { album ->
            (activity as? MainActivity)?.openAlbumDetail(album)
        }
        recycler.adapter = adapter
        val lm = FocusLinearLayoutManager(requireContext())
        lm.onFocusPosition = { lastFocusedPos = it }
        recycler.layoutManager = lm
        viewModel.albums.observe(viewLifecycleOwner) { adapter.update(it) }
    }

    override fun recyclerView(): RecyclerView? = recyclerRef

    override fun requestInitialFocus() {
        recyclerRef?.post {
            val lm = recyclerRef?.layoutManager
            val target = try { lm?.findViewByPosition(lastFocusedPos) } catch (_: Exception) { null }
            val child = (target ?: recyclerRef?.getChildAt(0))
            val clickable = child?.findViewById<View?>(R.id.clickable_item) ?: child
            Log.d("DPAD_FOCUS", "AlbumsTab.requestInitialFocus target=${clickable?.id} success=${clickable?.requestFocus()}")
        }
    }
}

// ── Artists tab ──────────────────────────────────────────────────────────────

class ArtistsTabFragment : Fragment(), TabWithRecycler {
    private val viewModel: MusicViewModel by activityViewModels()
    private var recyclerRef: RecyclerView? = null
    private var lastFocusedPos = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        inflater.inflate(R.layout.fragment_tab_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recycler = view.findViewById<RecyclerView>(R.id.recycler)
        recyclerRef = recycler
        val adapter = ArtistAdapter(emptyList()) { artist ->
            (activity as? MainActivity)?.openArtistDetail(artist)
        }
        recycler.adapter = adapter
        val lm = FocusLinearLayoutManager(requireContext())
        lm.onFocusPosition = { lastFocusedPos = it }
        recycler.layoutManager = lm
        viewModel.artists.observe(viewLifecycleOwner) { adapter.update(it) }
    }

    override fun recyclerView(): RecyclerView? = recyclerRef

    override fun requestInitialFocus() {
        recyclerRef?.post {
            val lm = recyclerRef?.layoutManager
            val target = try { lm?.findViewByPosition(lastFocusedPos) } catch (_: Exception) { null }
            val child = (target ?: recyclerRef?.getChildAt(0))
            val clickable = child?.findViewById<View?>(R.id.clickable_item) ?: child
            Log.d("DPAD_FOCUS", "ArtistsTab.requestInitialFocus target=${clickable?.id} success=${clickable?.requestFocus()}")
        }
    }
}

// ── Genres tab ───────────────────────────────────────────────────────────────

class GenresTabFragment : Fragment(), TabWithRecycler {
    private val viewModel: MusicViewModel by activityViewModels()
    private var recyclerRef: RecyclerView? = null
    private var lastFocusedPos = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        inflater.inflate(R.layout.fragment_tab_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recycler = view.findViewById<RecyclerView>(R.id.recycler)
        recyclerRef = recycler
        val adapter = GenreAdapter(emptyList()) { genre ->
            (activity as? MainActivity)?.openGenreDetail(genre)
        }
        recycler.adapter = adapter
        val lm = FocusLinearLayoutManager(requireContext())
        lm.onFocusPosition = { lastFocusedPos = it }
        recycler.layoutManager = lm
        viewModel.genres.observe(viewLifecycleOwner) { adapter.update(it) }
    }

    override fun recyclerView(): RecyclerView? = recyclerRef

    override fun requestInitialFocus() {
        val rv = recyclerRef ?: return
        // If adapter has items, focus immediately; otherwise wait for first data load
        if ((rv.adapter?.itemCount ?: 0) > 0) {
            focusFirstItem(rv)
        } else {
            // Data not loaded yet — fire once when it arrives
            val observer = object : androidx.lifecycle.Observer<List<Genre>> {
                override fun onChanged(value: List<Genre>) {
                    if (value.isNotEmpty()) {
                        focusFirstItem(rv)
                        viewModel.genres.removeObserver(this)
                    }
                }
            }
            viewModel.genres.observe(viewLifecycleOwner, observer)
        }
    }

    private fun focusFirstItem(rv: RecyclerView) {
        rv.post {
            val lm = rv.layoutManager
            val target = try { lm?.findViewByPosition(lastFocusedPos) } catch (_: Exception) { null }
            val child = target ?: rv.getChildAt(0)
            val clickable = child?.findViewById<View?>(R.id.clickable_item) ?: child
            Log.d("DPAD_FOCUS", "GenresTab.focusFirstItem target=${clickable?.id} success=${clickable?.requestFocus()}")
        }
    }
}

// ── Playlists tab ─────────────────────────────────────────────────────────────

class PlaylistsTabFragment : Fragment(), TabWithRecycler {
    private val viewModel: MusicViewModel by activityViewModels()
    private var recyclerRef: RecyclerView? = null
    private var lastFocusedPos = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        inflater.inflate(R.layout.fragment_tab_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recycler = view.findViewById<RecyclerView>(R.id.recycler)
        recyclerRef = recycler

        val adapter = PlaylistAdapter(
            items = emptyList(),
            onPlaylistClick = { playlist -> (activity as? MainActivity)?.openPlaylistDetail(playlist) }
        )
        adapter.onCreateClick = { showCreatePlaylistDialog() }
        recycler.adapter = adapter
        val lm = FocusLinearLayoutManager(requireContext())
        lm.onFocusPosition = { lastFocusedPos = it }
        recycler.layoutManager = lm
        viewModel.playlists.observe(viewLifecycleOwner) { adapter.update(it) }
    }

    private fun showCreatePlaylistDialog() {
        val editText = EditText(requireContext())
        editText.hint = "Playlist name"
        editText.requestFocus()
        AlertDialog.Builder(requireContext())
            .setTitle("New playlist")
            .setView(editText)
            .setPositiveButton("Create") { _, _ ->
                val name = editText.text.toString().trim()
                if (name.isNotEmpty()) viewModel.createPlaylist(name)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun recyclerView(): RecyclerView? = recyclerRef

    override fun requestInitialFocus() {
        recyclerRef?.post {
            val lm = recyclerRef?.layoutManager
            val target = try { lm?.findViewByPosition(lastFocusedPos) } catch (_: Exception) { null }
            val child = (target ?: recyclerRef?.getChildAt(0))
            val clickable = child?.findViewById<View?>(R.id.clickable_item) ?: child
            Log.d("DPAD_FOCUS", "PlaylistsTab.requestInitialFocus target=${clickable?.id} success=${clickable?.requestFocus()}")
        }
    }
}

// ── ViewPager2 adapter ────────────────────────────────────────────────────────

class LibraryPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = 5
    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> SongsTabFragment()
        1 -> AlbumsTabFragment()
        2 -> ArtistsTabFragment()
        3 -> GenresTabFragment()
        4 -> PlaylistsTabFragment()
        else -> SongsTabFragment()
    }
}
