package com.example.dpadplayer

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import android.content.SharedPreferences
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.tabs.TabLayoutMediator

class LibraryFragment : Fragment() {

    private val viewModel: MusicViewModel by activityViewModels()

    private lateinit var viewPager: androidx.viewpager2.widget.ViewPager2
    private lateinit var tabLayout: com.google.android.material.tabs.TabLayout
    private lateinit var btnBack: MaterialButton
    private lateinit var miniPlayer: View
    private lateinit var miniArt: ImageView
    private lateinit var miniTitle: TextView
    private lateinit var miniArtist: TextView
    private lateinit var miniBtnPlay: MaterialButton
    private lateinit var miniBtnNext: MaterialButton
    private lateinit var miniProgress: LinearProgressIndicator

    private val tabTitles = listOf("Songs", "Albums", "Artists", "Genres", "Playlists")

    companion object {
        private const val ARG_TAB = "tab"
        fun newInstance(tabIndex: Int = 0) = LibraryFragment().apply {
            arguments = Bundle().also { it.putInt(ARG_TAB, tabIndex) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_library, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager    = view.findViewById(R.id.view_pager)
        tabLayout  = view.findViewById(R.id.tab_layout)
        btnBack     = view.findViewById(R.id.btn_back)
        miniPlayer = view.findViewById(R.id.mini_player)
        miniArt    = view.findViewById(R.id.mini_art)
        miniTitle = view.findViewById(R.id.mini_title)
        miniArtist = view.findViewById(R.id.mini_artist)
        miniBtnPlay = view.findViewById(R.id.mini_btn_play)
        miniBtnNext = view.findViewById(R.id.mini_btn_next)
        miniProgress = view.findViewById(R.id.mini_progress)

        // Setup ViewPager2 with tabs
        viewPager.adapter = LibraryPagerAdapter(this)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        // Apply "show_top_bar" preference immediately — hide only the tab bar, keep the pager content
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val showTopBar = prefs.getBoolean("show_top_bar", true)
        tabLayout.visibility = if (showTopBar) View.VISIBLE else View.GONE

        // Always keep the pager visible so users can navigate directly to the Songs/Albums content
        viewPager.visibility = View.VISIBLE

        // Listen for changes to apply immediately
        prefs.registerOnSharedPreferenceChangeListener(prefChangeListener)

        // Navigate to initial tab
        val initialTab = arguments?.getInt(ARG_TAB, 0) ?: 0
        if (initialTab > 0) viewPager.setCurrentItem(initialTab, false)

        // Marquee requires isSelected = true
        miniTitle.isSelected = true

        // Back button
        btnBack.setOnClickListener { parentFragmentManager.popBackStack() }

        // Mini-player interactions
        miniPlayer.setOnClickListener {
            (activity as? MainActivity)?.openPlayer()
        }
        miniPlayer.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN &&
                (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER)) {
                (activity as? MainActivity)?.openPlayer()
                true
            } else false
        }

        miniBtnPlay.setOnClickListener {
            (activity as? MainActivity)?.togglePlayPause()
        }
        miniBtnNext.setOnClickListener {
            (activity as? MainActivity)?.sendCmd("NEXT")
        }

        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Unregister preference listener to avoid leaks
        try {
            PreferenceManager.getDefaultSharedPreferences(requireContext())
                .unregisterOnSharedPreferenceChangeListener(prefChangeListener)
        } catch (_: Exception) { }
    }

    private val prefChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            if (key == "show_top_bar") {
                val show = prefs.getBoolean(key, true)
                tabLayout.visibility = if (show) View.VISIBLE else View.GONE
                // Keep viewPager visible regardless — preference hides only the top bar
                viewPager.visibility = View.VISIBLE
            }
        }

    fun selectTab(index: Int) {
        if (::viewPager.isInitialized) viewPager.setCurrentItem(index, true)
    }

    private fun observeViewModel() {
        viewModel.currentIndex.observe(viewLifecycleOwner) { _ ->
            refreshMiniPlayer()
        }
        viewModel.isPlaying.observe(viewLifecycleOwner) { playing ->
            updateMiniPlayIcon(playing)
        }
        viewModel.position.observe(viewLifecycleOwner) { pos ->
            updateProgressBar(pos)
        }
    }

    private fun refreshMiniPlayer() {
        val activity = activity as? MainActivity ?: return
        val track = activity.currentTrack() ?: return
        miniTitle.text  = track.title
        miniArtist.text = track.artist
        miniProgress.max = track.duration.toInt()

        val loaded = try {
            requireContext().contentResolver.openInputStream(track.albumArtUri)?.use { true } ?: false
        } catch (_: Exception) { false }
        if (loaded) miniArt.setImageURI(track.albumArtUri)
        else { miniArt.setImageURI(null); miniArt.setImageResource(R.drawable.ic_music_note) }
    }

    private fun updateMiniPlayIcon(isPlaying: Boolean) {
        val icon = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        miniBtnPlay.icon = ContextCompat.getDrawable(requireContext(), icon)
    }

    private fun updateProgressBar(pos: Long) {
        miniProgress.progress = pos.toInt()
    }

    fun onDpadDown(focusedPos: Int): Boolean {
        val itemCount = viewPager.adapter?.itemCount ?: 0
        return if (focusedPos >= itemCount - 1) {
            miniPlayer.requestFocus()
            true
        } else false
    }

    fun onDpadUpFromMini(): Boolean {
        viewPager.requestFocus()
        return true
    }

    fun isMiniPlayerFocused(): Boolean =
        miniPlayer.hasFocus() || miniPlayer.isFocused ||
        miniBtnPlay.hasFocus() || miniBtnNext.hasFocus()

    fun recyclerView(): RecyclerView? {
        val frag = childFragmentManager.findFragmentById(R.id.view_pager)
        return if (frag is SongsTabFragment) frag.recyclerView() else null
    }
}
