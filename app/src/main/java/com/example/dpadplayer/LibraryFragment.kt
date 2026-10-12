package com.example.dpadplayer

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import android.content.SharedPreferences
import android.util.Log
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayoutMediator

class LibraryFragment : Fragment() {

    private val viewModel: MusicViewModel by activityViewModels()

    private lateinit var viewPager: androidx.viewpager2.widget.ViewPager2
    private lateinit var tabLayout: com.google.android.material.tabs.TabLayout
    private lateinit var btnBack: MaterialButton
    private lateinit var btnSearch: MaterialButton
    private lateinit var btnTabs: MaterialButton
    private lateinit var miniPlayer: MiniPlayerBinder
    private var restoringInitialTab = false
    private var requestedInitialTab = 0

    private val tabTitles by lazy { resources.getStringArray(R.array.library_tab_titles) }

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

        viewPager       = view.findViewById(R.id.view_pager)
        tabLayout       = view.findViewById(R.id.tab_layout)
        btnBack         = view.findViewById(R.id.btn_back)
        btnSearch       = view.findViewById(R.id.btn_search)
        btnTabs         = view.findViewById(R.id.btn_tabs)
        miniPlayer      = MiniPlayerBinder(view.findViewById(R.id.mini_player), requireActivity() as MainActivity)
            .attach(R.id.view_pager) { requestFocusForCurrentTab() }

        // Setup ViewPager2 with tabs
        viewPager.adapter = LibraryPagerAdapter(this)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
        tabLayout.isFocusable = false
        tabLayout.isFocusableInTouchMode = false
        tabLayout.descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
        styleTabViews()

        // Apply "show_top_bar" preference immediately
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        applyTopBarVisibility(prefs.getBoolean("show_top_bar", true))

        prefs.registerOnSharedPreferenceChangeListener(prefChangeListener)

        // ViewPager must not steal focus directly, but it acts as a group
        viewPager.isFocusable = true
        viewPager.descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
        viewPager.registerOnPageChangeCallback(object: androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (restoringInitialTab && position != requestedInitialTab) {
                    Log.d("DPAD_FOCUS", "Ignoring unexpected onPageSelected position=$position while restoring tab=$requestedInitialTab")
                    return
                }
                Log.d("DPAD_FOCUS", "ViewPager onPageSelected: position=$position (old activeTabPosition=${viewModel.activeLibraryTab})")
                viewModel.activeLibraryTab = position
                requestFocusForTab(position)
            }
        })

        // Restore tab position from memory or arguments
        requestedInitialTab = if (viewModel.activeLibraryTab >= 0) viewModel.activeLibraryTab else (arguments?.getInt(ARG_TAB, 0) ?: 0)
        restoringInitialTab = true
        viewPager.setCurrentItem(requestedInitialTab, false)

        // Fire initial focus for the starting tab once the pager settles.
        viewPager.post {
            if (viewPager.currentItem != requestedInitialTab) {
                viewPager.setCurrentItem(requestedInitialTab, false)
            }
            restoringInitialTab = false
            requestFocusForTab(viewPager.currentItem)
        }

        // Back button
        btnBack.setOnClickListener { parentFragmentManager.popBackStack() }
        applyPlayerControlFocusBackground(btnBack)
        btnBack.setupDpadItem(onFocusChanged = materialButtonFocusChangeHandler(btnBack)) {
            parentFragmentManager.popBackStack()
        }

        btnSearch.setOnClickListener { openSearchDialog() }
        applyPlayerControlFocusBackground(btnSearch)
        btnSearch.setupDpadItem(onFocusChanged = materialButtonFocusChangeHandler(btnSearch)) {
            openSearchDialog()
        }

        // When the tab strip is hidden this button is the only way to reach the
        // other sections, so it always opens a tab picker.
        btnTabs.setOnClickListener { showTabPicker() }
        applyPlayerControlFocusBackground(btnTabs)
        btnTabs.setupDpadItem(onFocusChanged = materialButtonFocusChangeHandler(btnTabs)) {
            showTabPicker()
        }

        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        Log.d("DPAD_FOCUS", "LibraryFragment.onResume — re-requesting focus into active tab")
        viewPager.post {
            requestFocusForCurrentTab()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            PreferenceManager.getDefaultSharedPreferences(requireContext())
                .unregisterOnSharedPreferenceChangeListener(prefChangeListener)
        } catch (_: Exception) { }
    }

    private val prefChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            if (key == "show_top_bar") {
                applyTopBarVisibility(prefs.getBoolean(key, true))
            }
        }

    /**
     * Hiding the tab strip must not hide the other sections, so the toolbar
     * gains a tab-picker button whenever the strip is off.
     */
    private fun applyTopBarVisibility(showTopBar: Boolean) {
        tabLayout.visibility = if (showTopBar) View.VISIBLE else View.GONE
        btnTabs.visibility = if (showTopBar) View.GONE else View.VISIBLE
        viewPager.visibility = View.VISIBLE
    }

    private fun showTabPicker() {
        val current = (viewModel.activeLibraryTab.takeIf { it >= 0 } ?: viewPager.currentItem)
        android.app.AlertDialog.Builder(requireContext())
            .setTitle(R.string.cd_switch_tabs)
            .setSingleChoiceItems(tabTitles, current) { dialog, which ->
                selectTab(which)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }

    fun selectTab(index: Int) {
        if (::viewPager.isInitialized) {
            viewModel.activeLibraryTab = index
            viewPager.setCurrentItem(index, true)
        }
    }

    private fun styleTabViews() {
        tabLayout.post {
            val tabStrip = tabLayout.getChildAt(0) as? ViewGroup ?: return@post
            val horizontalMargin = (4 * resources.displayMetrics.density).toInt()
            val verticalMargin = (4 * resources.displayMetrics.density).toInt()
            for (i in 0 until tabStrip.childCount) {
                val tabView = tabStrip.getChildAt(i)
                applyTopBarTabFocusBackground(tabView)
                tabView.isFocusable = true
                tabView.isFocusableInTouchMode = false

                val params = tabView.layoutParams as? ViewGroup.MarginLayoutParams ?: continue
                if (params.marginStart != horizontalMargin ||
                    params.marginEnd != horizontalMargin ||
                    params.topMargin != verticalMargin ||
                    params.bottomMargin != verticalMargin) {
                    params.marginStart = horizontalMargin
                    params.marginEnd = horizontalMargin
                    params.topMargin = verticalMargin
                    params.bottomMargin = verticalMargin
                    tabView.layoutParams = params
                }
            }
        }
    }

    private fun requestFocusForTab(position: Int) {
        val tag = "f$position"
        val f = childFragmentManager.findFragmentByTag(tag)
        Log.d("DPAD_FOCUS", "requestFocusForTab position=$position tag=$tag fragment=$f")
        if (f is TabWithRecycler) {
            f.requestInitialFocus()
            return
        }
        for (frag in childFragmentManager.fragments) {
            if (frag is TabWithRecycler && frag.isResumed) {
                frag.requestInitialFocus()
                break
            }
        }
    }

    private fun requestFocusForCurrentTab() {
        val currentItem = viewModel.activeLibraryTab.takeIf { it >= 0 } ?: viewPager.currentItem
        if (viewPager.currentItem != currentItem) {
            viewPager.setCurrentItem(currentItem, false)
        }
        Log.d("DPAD_FOCUS", "LibraryFragment.onResume — active tab is position=$currentItem")
        requestFocusForTab(currentItem)
    }

    private fun observeViewModel() {
        viewModel.currentIndex.observe(viewLifecycleOwner) { _ -> refreshMiniPlayer() }
        viewModel.isPlaying.observe(viewLifecycleOwner)   { playing ->
            miniPlayer.setPlaying(playing)
            refreshMiniPlayer()
        }
        viewModel.position.observe(viewLifecycleOwner)    { pos -> miniPlayer.setPosition(pos) }
        viewModel.searchQuery.observe(viewLifecycleOwner) { query ->
            val suffix = if (query.isBlank()) "" else " (${query.trim()})"
            view?.findViewById<TextView>(R.id.tv_app_title)?.text = getString(R.string.home_title) + suffix
        }
    }

    private fun openSearchDialog() {
        val current = viewModel.searchQuery.value.orEmpty()
        val input = EditText(requireContext()).apply {
            setText(current)
            setSelection(text.length)
            hint = getString(R.string.hint_search)
        }
        android.app.AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_search_title)
            .setView(input)
            .setPositiveButton(R.string.dialog_apply) { _, _ ->
                viewModel.setSearchQuery(input.text.toString())
            }
            .setNeutralButton(R.string.search_clear) { _, _ ->
                viewModel.setSearchQuery("")
            }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
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
