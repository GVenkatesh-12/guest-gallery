package com.guestgallery.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.guestgallery.domain.model.AppSettings
import com.guestgallery.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/** Stores only the user choice to show the screen-pinning reminder. */
@Singleton
class SettingsRepositoryImpl
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
        @ApplicationContext private val context: Context,
    ) : SettingsRepository {
        override fun observeSettings(): Flow<AppSettings> =
            dataStore.data.map { preferences ->
                AppSettings(
                    enableScreenPinningReminder =
                        preferences[PreferencesKeys.SCREEN_PINNING_REMINDER] ?: true,
                )
            }

        override suspend fun setScreenPinningReminder(enabled: Boolean) {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.SCREEN_PINNING_REMINDER] = enabled
            }
        }

        override suspend fun clearCache() {
            context.cacheDir.deleteContents()
            context.externalCacheDirs.filterNotNull().forEach { it.deleteContents() }
        }
    }

private fun File.deleteContents() {
    listFiles()?.forEach { child ->
        if (child.isDirectory) {
            child.deleteRecursively()
        } else {
            child.delete()
        }
    }
}
