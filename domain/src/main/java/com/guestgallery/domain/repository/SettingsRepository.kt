package com.guestgallery.domain.repository

import com.guestgallery.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for reading and writing application settings.
 * Implementations should persist settings across app restarts.
 */
interface SettingsRepository {

    /** Observe the complete settings state as a Flow. */
    fun observeSettings(): Flow<AppSettings>

    /** Update a boolean setting by key. */
    suspend fun updateBooleanSetting(key: String, value: Boolean)

    /** Update an integer setting by key. */
    suspend fun updateIntSetting(key: String, value: Int)

    /** Update a float setting by key. */
    suspend fun updateFloatSetting(key: String, value: Float)

    /** Update a string setting by key. */
    suspend fun updateStringSetting(key: String, value: String)

    /** Update a long setting by key. */
    suspend fun updateLongSetting(key: String, value: Long)

    /** Reset all settings to their default values. */
    suspend fun resetToDefaults()

    /** Clear all cached data. */
    suspend fun clearCache()
}
