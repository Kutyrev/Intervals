package com.github.kutyrev.intervals.features.detail.di

import com.github.kutyrev.intervals.repository.preferences.LocalPreferencesRepository
import com.github.kutyrev.intervals.repository.preferences.PreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class DetailsModule {

    @Binds
    abstract fun bindPreferencesRepository(
        localPreferencesRepository: LocalPreferencesRepository
    ): PreferencesRepository
}
