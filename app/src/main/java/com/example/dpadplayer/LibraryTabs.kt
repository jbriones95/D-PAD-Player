package com.example.dpadplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter

// ── Songs tab ────────────────────────────────────────────────────────────────

class SongsTabFragment : Fragment() {
    private val viewModel: MusicViewModel by activityViewModels()
    private lateinit var adapter: TrackAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        inflater.inflate(R.layout.fragment_tab_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recycler = view.findViewById<RecyclerView>(R.id.recycler)
        adapter = TrackAdapter(emptyList()) { index ->
            (activity as? MainActivity)?.playTrack(index)
        }
        recycler.adapter = adapter
        recycler.layoutManager = FocusLinearLayoutManager(requireContext())

        viewModel.tracks.observe(viewLifecycleOwner) { adapter.updateTracks(it) }
        viewModel.currentIndex.observe(viewLifecycleOwner) { adapter.setSelectedIndex(it) }
    }
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
        inflater.inflate(R.layout.fragment_tab_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recycler = view.findViewById<RecyclerView>(R.id.recycler)
        val adapter = PlaylistAdapter(emptyList()) { playlist ->
            (activity as? MainActivity)?.openPlaylistDetail(playlist)
        }
        recycler.adapter = adapter
        recycler.layoutManager = FocusLinearLayoutManager(requireContext())
        viewModel.playlists.observe(viewLifecycleOwner) { adapter.update(it) }
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
