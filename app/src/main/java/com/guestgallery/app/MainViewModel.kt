package com.guestgallery.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guestgallery.domain.model.AppSettings
import com.guestgallery.domain.model.ViewingSession
import com.guestgallery.domain.repository.SettingsRepository
import com.guestgallery.domain.usecase.CreateSessionUseCase
import com.guestgallery.domain.usecase.DestroySessionUseCase
import com.guestgallery.domain.usecase.GetSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Coordinates short-lived, memory-only shared-image sessions. */
@HiltViewModel
class MainViewModel
    @Inject
    constructor(
        private val createSessionUseCase: CreateSessionUseCase,
        private val destroySessionUseCase: DestroySessionUseCase,
        getSettingsUseCase: GetSettingsUseCase,
        private val settingsRepository: SettingsRepository,
    ) : ViewModel() {
        val settings: StateFlow<AppSettings?> =
            getSettingsUseCase()
                .stateIn(viewModelScope, SharingStarted.Eagerly, null)

        private val _appState = MutableStateFlow<AppState>(AppState.Welcome)
        val appState: StateFlow<AppState> = _appState.asStateFlow()

        private val _finishEvent = MutableStateFlow(false)
        val finishEvent: StateFlow<Boolean> = _finishEvent.asStateFlow()

        fun onImagesReceived(uris: List<String>) {
            if (uris.isEmpty()) return

            viewModelScope.launch {
                createSessionUseCase(uris)
                    .onSuccess { session -> _appState.value = AppState.Viewing(session) }
                    .onFailure { _appState.value = AppState.Welcome }
            }
        }

        /** Screen pinning supplies the system-level exit protection when enabled. */
        fun requestExit() {
            viewModelScope.launch {
                runCatching { settingsRepository.clearCache() }
                destroySessionUseCase()
                _finishEvent.value = true
            }
        }

        fun onFinishConsumed() {
            _finishEvent.value = false
        }
    }

sealed class AppState {
    data object Welcome : AppState()

    data class Viewing(val session: ViewingSession) : AppState()
}
