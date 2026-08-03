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

    /** Enables or disables the screen-pinning reminder shown before viewing. */
    suspend fun setScreenPinningReminder(enabled: Boolean)

    /** Clear all cached data. */
    suspend fun clearCache()
}
