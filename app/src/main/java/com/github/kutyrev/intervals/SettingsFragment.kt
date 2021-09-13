package com.github.kutyrev.intervals

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        val aboutPref: Preference? = findPreference<Preference>("pref_about")
        aboutPref?.title = aboutPref?.title.toString() + BuildConfig.VERSION_NAME
    }
}