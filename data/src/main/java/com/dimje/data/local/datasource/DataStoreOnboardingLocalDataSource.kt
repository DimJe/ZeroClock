package com.dimje.data.local.datasource

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private val Context.onboardingStore by preferencesDataStore(name = "onboarding")

class DataStoreOnboardingLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) : OnboardingLocalDataSource {
    override suspend fun isCompleted(): Boolean =
        context.onboardingStore.data.first()[COMPLETED] ?: false

    override suspend fun complete() {
        context.onboardingStore.edit { it[COMPLETED] = true }
    }

    private companion object {
        val COMPLETED = booleanPreferencesKey("completed")
    }
}
