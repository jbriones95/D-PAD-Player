package com.example.dpadplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.activityViewModels
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.button.MaterialButton

/**
 * Settings screen — launched from the three-dot menu in LibraryFragment.
 * Wraps PreferenceFragmentCompat in a custom layout that has a back-arrow header.
 *
 * Preferences persisted automatically by the Preference library.
 * Changes take effect immediately:
 *   - Theme: switches night mode on the fly via AppCompatDelegate
 *   - Seek step: read by PlayerFragment at scrub time from SharedPreferences
 *   - Sort order: triggers a track reload in the ViewModel
 */
class SettingsFragment : PreferenceFragmentCompat() {

    private val viewModel: MusicViewModel by activityViewModels()

    // ── PreferenceFragmentCompat contract ─────────────────────────────────────

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        // Theme — apply immediately on change
        findPreference<ListPreference>("theme")?.setOnPreferenceChangeListener { _, newValue ->
            applyTheme(newValue as String)
            true
        }

        // Accent color — recreate activity to re-apply setTheme()
        findPreference<ListPreference>("accent")?.setOnPreferenceChangeListener { _, _ ->
            activity?.recreate()
            true
        }

        // Sort order — reload tracks with new ordering
        findPreference<ListPreference>("sort_order")?.setOnPreferenceChangeListener { _, newValue ->
            viewModel.loadTracks(newValue as String)
            true
        }

        // Seek step — no immediate side-effect; PlayerFragment reads it at runtime
    }

    // ── View wrapping — inject our header above the preference list ───────────

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate our wrapper layout
        val wrapper = inflater.inflate(R.layout.fragment_settings, container, false)

        // Let PreferenceFragmentCompat inflate its own list view
        val prefView = super.onCreateView(inflater, container, savedInstanceState)

        // Place the pref view inside our container slot
        val prefContainer = wrapper.findViewById<ViewGroup>(R.id.settings_prefs_container)
        prefContainer.addView(prefView)

        // Back button
        val btnBack = wrapper.findViewById<MaterialButton>(R.id.btn_settings_back)
        btnBack.setOnClickListener { parentFragmentManager.popBackStack() }
        applyItemFocusBackground(btnBack)
        btnBack.setupDpadItem { parentFragmentManager.popBackStack() }

        return wrapper
    }

    // ── Static helpers ────────────────────────────────────────────────────────

    companion object {
        fun applyTheme(value: String) {
            val mode = when (value) {
                "dark"  -> AppCompatDelegate.MODE_NIGHT_YES
                "light" -> AppCompatDelegate.MODE_NIGHT_NO
                else    -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        fun accentThemeRes(accent: String): Int = when (accent) {
            "purple"     -> R.style.Theme_DPadPlayer_Purple
            "blue"       -> R.style.Theme_DPadPlayer_Blue
            "teal"       -> R.style.Theme_DPadPlayer_Teal
            "green"      -> R.style.Theme_DPadPlayer_Green
            "red"        -> R.style.Theme_DPadPlayer_Red
            "orange"     -> R.style.Theme_DPadPlayer_Orange
            "yellow"     -> R.style.Theme_DPadPlayer_Yellow
            "grey"       -> R.style.Theme_DPadPlayer_Grey
            "retro"      -> R.style.Theme_DPadPlayer_Retro
            else         -> R.style.Theme_DPadPlayer   // deep_purple default
        }
    }
}
