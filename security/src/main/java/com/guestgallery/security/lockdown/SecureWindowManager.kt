package com.guestgallery.security.lockdown

import android.view.Window
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages window-level security and display flags.
 *
 * Provides methods to toggle [WindowManager.LayoutParams.FLAG_SECURE],
 * immersive mode (hiding status and navigation bars), and the keep-screen-on
 * flag. All operations are idempotent and safe to call multiple times.
 */
@Singleton
class SecureWindowManager @Inject constructor() {

    // ── FLAG_SECURE ──────────────────────────────────────────────────────────

    /**
     * Enables [FLAG_SECURE][WindowManager.LayoutParams.FLAG_SECURE] to
     * prevent screenshots, screen recording, and the recent-apps preview.
     */
    fun enableSecureMode(window: Window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }

    /** Disables [FLAG_SECURE][WindowManager.LayoutParams.FLAG_SECURE]. */
    fun disableSecureMode(window: Window) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }

    // ── Immersive mode ───────────────────────────────────────────────────────

    /**
     * Enters immersive sticky mode — hides both the status bar and the
     * navigation bar. Bars reappear temporarily on a swipe gesture.
     */
    fun enableImmersiveMode(window: Window) {
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
    }

    /** Exits immersive mode and restores system bars. */
    fun disableImmersiveMode(window: Window) {
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.show(WindowInsetsCompat.Type.systemBars())
    }

    // ── Keep screen awake ────────────────────────────────────────────────────

    /**
     * Toggles [FLAG_KEEP_SCREEN_ON][WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON].
     *
     * @param window  The activity window.
     * @param enabled `true` to keep the screen awake, `false` to allow sleep.
     */
    fun keepScreenAwake(window: Window, enabled: Boolean) {
        if (enabled) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}
