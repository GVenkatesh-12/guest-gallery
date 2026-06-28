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
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsRepository.observeSettings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings(),
        )

    fun updateBoolean(key: String, value: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateBooleanSetting(key, value)
        }
    }

    fun updateInt(key: String, value: Int) {
        viewModelScope.launch {
            settingsRepository.updateIntSetting(key, value)
        }
    }

    fun updateFloat(key: String, value: Float) {
        viewModelScope.launch {
            settingsRepository.updateFloatSetting(key, value)
        }
    }

    fun updateString(key: String, value: String) {
        viewModelScope.launch {
            settingsRepository.updateStringSetting(key, value)
        }
    }

    fun updateLong(key: String, value: Long) {
        viewModelScope.launch {
            settingsRepository.updateLongSetting(key, value)
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            settingsRepository.resetToDefaults()
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            settingsRepository.clearCache()
        }
    }
}
