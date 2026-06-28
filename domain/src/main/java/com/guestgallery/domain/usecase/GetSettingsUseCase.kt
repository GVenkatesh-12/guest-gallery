package com.guestgallery.domain.usecase

import com.guestgallery.domain.model.AppSettings
import com.guestgallery.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

/**
 * Retrieves the current application settings as an observable Flow.
 * Emits a new value whenever any setting changes.
 */
class GetSettingsUseCase(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(): Flow<AppSettings> {
        return settingsRepository.observeSettings()
    }
}
