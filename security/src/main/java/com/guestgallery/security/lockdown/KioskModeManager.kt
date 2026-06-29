package com.guestgallery.security.lockdown

import android.app.Activity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provides a fallback kiosk-like experience when full screen pinning
 * is unavailable or not desired.
 *
 * Kiosk mode combines:
 * - **Immersive mode** — hides status and navigation bars.
 * - **FLAG_SECURE** — prevents screenshots and recent-apps previews.
 * - **Keep screen awake** — prevents the screen from sleeping.
 * - **Back-button interception** — the [isKioskModeActive] flow allows
 *   the hosting Activity/ViewModel to block the back gesture.
 *
 * Uses [SecureWindowManager] internally for window flag management.
 */
@Singleton
class KioskModeManager
    @Inject
    constructor(
        private val secureWindowManager: SecureWindowManager,
    ) {
        private val _isKioskModeActive = MutableStateFlow(false)

        /** Observable state indicating whether kiosk mode is currently enabled. */
        val isKioskModeActive: StateFlow<Boolean> = _isKioskModeActive.asStateFlow()

        /**
         * Activates kiosk mode for the given [activity].
         *
         * Enables immersive mode, secure window flag, and keeps the screen
         * awake. Also sets [isKioskModeActive] to `true` so that the hosting
         * Activity can intercept the back button.
         */
        fun enableKioskMode(activity: Activity) {
            val window = activity.window
            secureWindowManager.enableImmersiveMode(window)
            secureWindowManager.enableSecureMode(window)
            secureWindowManager.keepScreenAwake(window, enabled = true)
            _isKioskModeActive.value = true
        }

        /**
         * Deactivates kiosk mode and restores all window flags to normal.
         */
        fun disableKioskMode(activity: Activity) {
            val window = activity.window
            secureWindowManager.disableImmersiveMode(window)
            secureWindowManager.disableSecureMode(window)
            secureWindowManager.keepScreenAwake(window, enabled = false)
            _isKioskModeActive.value = false
        }
    }
