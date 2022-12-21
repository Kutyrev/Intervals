package com.github.kutyrev.intervals.repository.preferences

import android.content.Context
import androidx.preference.PreferenceManager
import com.github.kutyrev.intervals.app.di.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val FAST_ADD_BUTTON_KEY = "fast_add_button"

class LocalPreferencesRepository @Inject constructor(
    @ApplicationContext private val appContext: Context,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : PreferencesRepository {

    override suspend fun getFastAddButtonProperty(): Boolean =
        withContext(dispatcher) {
            val sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(appContext)

            sharedPreferences.getBoolean(FAST_ADD_BUTTON_KEY, false)
        }
}
