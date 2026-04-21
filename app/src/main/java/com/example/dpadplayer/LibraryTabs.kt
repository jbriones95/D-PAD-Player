package com.example.dpadplayer

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
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
            // Prefer the clickable overlay inside the item so D-pad moves between items cleanly.
            val clickable = target?.findViewById<View?>(R.id.clickable_item) ?: target
            clickable?.requestFocus()
        }
    }
}

// ── Albums tab ───────────────────────────────────────────────────────────────

class AlbumsTabFragment : Fragment(), TabWithRecycler {
    private val viewModel: MusicViewModel by activityViewModels()
    private var recyclerRef: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        inflater.inflate(R.layout.fragment_tab_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recycler = view.findViewById<RecyclerView>(R.id.recycler)
        recyclerRef = recycler
        val adapter = AlbumAdapter(emptyList()) { album ->
            (activity as? MainActivity)?.openAlbumDetail(album)
        }
        recycler.adapter = adapter
        recycler.layoutManager = FocusLinearLayoutManager(requireContext())
        viewModel.albums.observe(viewLifecycleOwner) { adapter.update(it) }
    }

    override fun recyclerView(): RecyclerView? = recyclerRef

    override fun requestInitialFocus() {
        recyclerRef?.post {
            val lm = recyclerRef?.layoutManager
            val target = try { lm?.findViewByPosition(0) } catch (_: Exception) { null }
            val child = (target ?: recyclerRef?.getChildAt(0))
            val clickable = child?.findViewById<View?>(R.id.clickable_item) ?: child
            clickable?.requestFocus()
        }
    }
}

// ── Artists tab ──────────────────────────────────────────────────────────────

class ArtistsTabFragment : Fragment(), TabWithRecycler {
    private val viewModel: MusicViewModel by activityViewModels()
    private var recyclerRef: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        inflater.inflate(R.layout.fragment_tab_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recycler = view.findViewById<RecyclerView>(R.id.recycler)
        recyclerRef = recycler
        val adapter = ArtistAdapter(emptyList()) { artist ->
            (activity as? MainActivity)?.openArtistDetail(artist)
        }
        recycler.adapter = adapter
        recycler.layoutManager = FocusLinearLayoutManager(requireContext())
        viewModel.artists.observe(viewLifecycleOwner) { adapter.update(it) }
    }

    override fun recyclerView(): RecyclerView? = recyclerRef

    override fun requestInitialFocus() {
        recyclerRef?.post {
            val lm = recyclerRef?.layoutManager
            val target = try { lm?.findViewByPosition(0) } catch (_: Exception) { null }
            val child = (target ?: recyclerRef?.getChildAt(0))
            val clickable = child?.findViewById<View?>(R.id.clickable_item) ?: child
            clickable?.requestFocus()
        }
    }
}

// ── Genres tab ───────────────────────────────────────────────────────────────

class GenresTabFragment : Fragment(), TabWithRecycler {
    private val viewModel: MusicViewModel by activityViewModels()
    private var recyclerRef: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        inflater.inflate(R.layout.fragment_tab_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recycler = view.findViewById<RecyclerView>(R.id.recycler)
        recyclerRef = recycler
        val adapter = GenreAdapter(emptyList()) { genre ->
            (activity as? MainActivity)?.openGenreDetail(genre)
        }
        recycler.adapter = adapter
        recycler.layoutManager = FocusLinearLayoutManager(requireContext())
        viewModel.genres.observe(viewLifecycleOwner) { adapter.update(it) }
    }

    override fun recyclerView(): RecyclerView? = recyclerRef

    override fun requestInitialFocus() {
        recyclerRef?.post {
            val lm = recyclerRef?.layoutManager
            val target = try { lm?.findViewByPosition(0) } catch (_: Exception) { null }
            val child = (target ?: recyclerRef?.getChildAt(0))
            val clickable = child?.findViewById<View?>(R.id.clickable_item) ?: child
            clickable?.requestFocus()
        }
    }
}

// ── Playlists tab ─────────────────────────────────────────────────────────────

class PlaylistsTabFragment : Fragment(), TabWithRecycler {
    private val viewModel: MusicViewModel by activityViewModels()
    private var recyclerRef: RecyclerView? = null

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
        recycler.layoutManager = FocusLinearLayoutManager(requireContext())
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
            val target = try { lm?.findViewByPosition(0) } catch (_: Exception) { null }
            val child = (target ?: recyclerRef?.getChildAt(0))
            val clickable = child?.findViewById<View?>(R.id.clickable_item) ?: child
            clickable?.requestFocus()
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
