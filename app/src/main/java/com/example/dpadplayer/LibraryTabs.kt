package com.example.dpadplayer

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

// ── Songs tab ────────────────────────────────────────────────────────────────

class SongsTabFragment : Fragment() {
    private val viewModel: MusicViewModel by activityViewModels()
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: TrackAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        inflater.inflate(R.layout.fragment_tab_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recycler = view.findViewById(R.id.recycler)
        adapter = TrackAdapter(
            items = emptyList(),
            onTrackClick = { index -> (activity as? MainActivity)?.playTrack(index) },
            onTrackLongClick = { index ->
                val tracks = viewModel.tracks.value ?: return@TrackAdapter false
                val track = tracks.getOrNull(index) ?: return@TrackAdapter false
                showAddToPlaylistDialog(track)
                true
            }
        )
        recycler.adapter = adapter
        recycler.layoutManager = FocusLinearLayoutManager(requireContext())

        viewModel.tracks.observe(viewLifecycleOwner) { adapter.updateTracks(it) }
        viewModel.currentIndex.observe(viewLifecycleOwner) { adapter.setSelectedIndex(it) }
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

    fun recyclerView(): RecyclerView = recycler
}

// ── Albums tab ───────────────────────────────────────────────────────────────

class AlbumsTabFragment : Fragment() {
    private val viewModel: MusicViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        inflater.inflate(R.layout.fragment_tab_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recycler = view.findViewById<RecyclerView>(R.id.recycler)
        val adapter = AlbumAdapter(emptyList()) { album ->
            (activity as? MainActivity)?.openAlbumDetail(album)
        }
        recycler.adapter = adapter
        recycler.layoutManager = FocusLinearLayoutManager(requireContext())
        viewModel.albums.observe(viewLifecycleOwner) { adapter.update(it) }
    }
}

// ── Artists tab ──────────────────────────────────────────────────────────────

class ArtistsTabFragment : Fragment() {
    private val viewModel: MusicViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        inflater.inflate(R.layout.fragment_tab_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recycler = view.findViewById<RecyclerView>(R.id.recycler)
        val adapter = ArtistAdapter(emptyList()) { artist ->
            (activity as? MainActivity)?.openArtistDetail(artist)
        }
        recycler.adapter = adapter
        recycler.layoutManager = FocusLinearLayoutManager(requireContext())
        viewModel.artists.observe(viewLifecycleOwner) { adapter.update(it) }
    }
}

// ── Genres tab ───────────────────────────────────────────────────────────────

class GenresTabFragment : Fragment() {
    private val viewModel: MusicViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        inflater.inflate(R.layout.fragment_tab_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recycler = view.findViewById<RecyclerView>(R.id.recycler)
        val adapter = GenreAdapter(emptyList()) { genre ->
            (activity as? MainActivity)?.openGenreDetail(genre)
        }
        recycler.adapter = adapter
        recycler.layoutManager = FocusLinearLayoutManager(requireContext())
        viewModel.genres.observe(viewLifecycleOwner) { adapter.update(it) }
    }
}

// ── Playlists tab ─────────────────────────────────────────────────────────────

class PlaylistsTabFragment : Fragment() {
    private val viewModel: MusicViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        inflater.inflate(R.layout.fragment_tab_playlists, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recycler = view.findViewById<RecyclerView>(R.id.recycler)
        val fab = view.findViewById<FloatingActionButton>(R.id.fab_create_playlist)

        val adapter = PlaylistAdapter(emptyList()) { playlist ->
            (activity as? MainActivity)?.openPlaylistDetail(playlist)
        }
        recycler.adapter = adapter
        recycler.layoutManager = FocusLinearLayoutManager(requireContext())
        viewModel.playlists.observe(viewLifecycleOwner) { adapter.update(it) }

        fab.setOnClickListener { showCreatePlaylistDialog() }
    }

    private fun showCreatePlaylistDialog() {
        val editText = EditText(requireContext())
        editText.hint = "Playlist name"
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
