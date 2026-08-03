package com.guestgallery.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guestgallery.domain.model.AppSettings
import com.guestgallery.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
        private val settingsRepository: SettingsRepository,
    ) : ViewModel() {
        val settings: StateFlow<AppSettings?> =
            settingsRepository.observeSettings()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = null,
                )

        fun setScreenPinningReminder(enabled: Boolean) {
            viewModelScope.launch {
                settingsRepository.setScreenPinningReminder(enabled)
            }
        }
    }
