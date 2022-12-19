package com.github.kutyrev.intervals.features.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.github.kutyrev.intervals.BuildConfig
import com.github.kutyrev.intervals.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        val aboutPref: Preference? = findPreference<Preference>("pref_about")
        aboutPref?.title = aboutPref?.title.toString() + BuildConfig.VERSION_NAME
    }
}