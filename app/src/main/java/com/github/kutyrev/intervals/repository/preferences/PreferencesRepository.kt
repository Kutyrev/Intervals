package com.github.kutyrev.intervals.repository.preferences

interface PreferencesRepository {
    suspend fun getFastAddButtonProperty(): Boolean
}
