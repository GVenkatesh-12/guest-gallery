package com.guestgallery.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guestgallery.domain.model.AppSettings
import com.guestgallery.domain.model.ViewingSession
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

/**
 * Root-level ViewModel that orchestrates app-wide state transitions.
 *
 * Manages the lifecycle of viewing sessions and coordinates
 * exit-authentication logic based on user settings.
 */
@HiltViewModel
class MainViewModel
    @Inject
    constructor(
        private val createSessionUseCase: CreateSessionUseCase,
        private val destroySessionUseCase: DestroySessionUseCase,
        getSettingsUseCase: GetSettingsUseCase,
    ) : ViewModel() {
        /** Observable settings stream, shared across collectors. */
        val settings: StateFlow<AppSettings> =
            getSettingsUseCase()
                .stateIn(viewModelScope, SharingStarted.Eagerly, AppSettings())

        private val _appState = MutableStateFlow<AppState>(AppState.Welcome)
        val appState: StateFlow<AppState> = _appState.asStateFlow()

        /** Whether the host Activity should finish itself. */
        private val _finishEvent = MutableStateFlow(false)
        val finishEvent: StateFlow<Boolean> = _finishEvent.asStateFlow()

        // ── Public API ───────────────────────────────────────────────────────────

        /**
         * Called when images are received via a share intent.
         * Creates a new viewing session and transitions to [AppState.Viewing].
         */
        fun onImagesReceived(uris: List<String>) {
            if (uris.isEmpty()) return
            viewModelScope.launch {
                createSessionUseCase(uris)
                    .onSuccess { session ->
                        _appState.value = AppState.Viewing(session)
                    }
                    .onFailure {
                        // If session creation fails, stay on Welcome
                        _appState.value = AppState.Welcome
                    }
            }
        }

        /**
         * Called when the user (device owner) wants to exit.
         * If authentication is required by settings, transitions to [AppState.ExitAuth];
         * otherwise destroys the session and finishes immediately.
         */
        fun requestExit() {
            val currentSettings = settings.value
            val authRequired =
                currentSettings.requireFingerprintToExit ||
                    currentSettings.requirePinToExit

            if (authRequired) {
                _appState.value = AppState.ExitAuth
            } else {
                destroyAndFinish()
            }
        }

        /** Called after the device owner successfully authenticates to exit. */
        fun onAuthSuccess() {
            destroyAndFinish()
        }

        /** Called when the owner cancels exit authentication — returns to viewing. */
        fun onAuthCancel() {
            val current = _appState.value
            // Only revert if we're on the auth screen; otherwise keep current state
            if (current is AppState.ExitAuth) {
                _appState.value = AppState.Viewing(null)
            }
        }

        /** Resets the finish event after Activity has consumed it. */
        fun onFinishConsumed() {
            _finishEvent.value = false
        }

        // ── Internal ─────────────────────────────────────────────────────────────

        private fun destroyAndFinish() {
            viewModelScope.launch {
                destroySessionUseCase()
                _finishEvent.value = true
            }
        }
    }

/**
 * Sealed class representing the top-level application state.
 */
sealed class AppState {
    /** Initial state — app launched directly, no images shared. */
    data object Loading : AppState()

    /** Welcome screen shown when no session exists. */
    data object Welcome : AppState()

    /** Actively viewing shared images. */
    data class Viewing(val session: ViewingSession?) : AppState()

    /** Awaiting authentication before exiting. */
    data object ExitAuth : AppState()
}
