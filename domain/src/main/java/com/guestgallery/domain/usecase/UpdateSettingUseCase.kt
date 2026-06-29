package com.guestgallery.domain.usecase

import com.guestgallery.domain.repository.SettingsRepository

/**
 * Updates an individual application setting.
 * Supports Boolean, Int, Float, String, and Long setting types.
 */
class UpdateSettingUseCase(
    private val settingsRepository: SettingsRepository,
) {
    suspend fun updateBoolean(
        key: String,
        value: Boolean,
    ) {
        settingsRepository.updateBooleanSetting(key, value)
    }

    suspend fun updateInt(
        key: String,
        value: Int,
    ) {
        settingsRepository.updateIntSetting(key, value)
    }

    suspend fun updateFloat(
        key: String,
        value: Float,
    ) {
        settingsRepository.updateFloatSetting(key, value)
    }

    suspend fun updateString(
        key: String,
        value: String,
    ) {
        settingsRepository.updateStringSetting(key, value)
    }

    suspend fun updateLong(
        key: String,
        value: Long,
    ) {
        settingsRepository.updateLongSetting(key, value)
    }

    suspend fun resetAll() {
        settingsRepository.resetToDefaults()
    }
}
