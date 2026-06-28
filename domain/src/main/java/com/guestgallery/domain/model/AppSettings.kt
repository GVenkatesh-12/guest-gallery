package com.guestgallery.domain.model

import com.guestgallery.domain.model.TransitionStyle.CROSSFADE
import com.guestgallery.domain.model.ViewerBackground.BLACK

/**
 * Complete application settings model.
 *
 * All settings have sensible defaults. This data class serves as the
 * single source of truth for the app's configuration state.
 */
data class AppSettings(
    // ── Security ─────────────────────────────────────────────────────────────
    val enableScreenPinningReminder: Boolean = true,
    val autoLockOnExit: Boolean = true,
    val requireFingerprintToExit: Boolean = false,
    val requirePinToExit: Boolean = false,
    val disableScreenshots: Boolean = true,
    val disableScreenRecording: Boolean = true,
    val hideNotifications: Boolean = true,
    val hideStatusBar: Boolean = true,
    val hideNavigationBar: Boolean = true,
    val preventBrightnessChange: Boolean = false,
    val autoCloseAfterLastImage: Boolean = false,
    val autoCloseAfterTimeoutMinutes: Int = 0,
    val lockOrientation: Boolean = false,
    val hideRecentAppsPreview: Boolean = true,
    val blankScreenOnAppSwitch: Boolean = true,
    val secureWindowFlag: Boolean = true,

    // ── Viewer ───────────────────────────────────────────────────────────────
    val showImageCounter: Boolean = true,
    val showFileName: Boolean = false,
    val showResolution: Boolean = false,
    val showFileSize: Boolean = false,
    val enableZoom: Boolean = true,
    val enableRotation: Boolean = false,
    val enableDoubleTapZoom: Boolean = true,
    val enableSlideshow: Boolean = false,
    val loopSlideshow: Boolean = true,
    val slideshowSpeedSeconds: Int = 3,
    val gestureSensitivity: Float = 1.0f,
    val maximumZoom: Float = 5.0f,
    val keepScreenAwake: Boolean = true,
    val viewerBackground: ViewerBackground = BLACK,
    val edgePadding: Int = 0,
    val transitionStyle: TransitionStyle = CROSSFADE,

    // ── Appearance ───────────────────────────────────────────────────────────
    val themeMode: String = "system",
    val dynamicColors: Boolean = true,
    val oledBlackMode: Boolean = false,
    val cornerRadius: Int = 16,
    val animationSpeed: Float = 1.0f,
    val accentColor: Long = 0,
    val fontSize: Float = 1.0f,
    val minimalUi: Boolean = false,
    val glassEffect: Boolean = false,
    val blurEffects: Boolean = true,
    val roundedButtons: Boolean = true,

    // ── Privacy ──────────────────────────────────────────────────────────────
    val deleteSessionHistory: Boolean = true,
    val neverStoreSharedImages: Boolean = true,
    val clearCacheOnExit: Boolean = true,
    val memoryOptimization: Boolean = true,
    val autoDeleteTempFiles: Boolean = true,
    val incognitoMode: Boolean = false,
    val anonymousCrashReports: Boolean = false,

    // ── Performance ──────────────────────────────────────────────────────────
    val imageCacheSizeMb: Int = 128,
    val preloadCount: Int = 2,
    val hardwareDecode: Boolean = true,
    val softwareDecode: Boolean = false,
    val animationQuality: String = "high",
    val memorySaver: Boolean = false,
    val batterySaver: Boolean = false,

    // ── Accessibility ────────────────────────────────────────────────────────
    val largeText: Boolean = false,
    val highContrast: Boolean = false,
    val talkBackSupport: Boolean = true,
    val oneHandMode: Boolean = false,
    val hapticFeedback: Boolean = true,
    val reducedMotion: Boolean = false,
)

/** Background color options for the image viewer. */
enum class ViewerBackground {
    BLACK,
    DARK_GRAY,
    GRAY,
    SYSTEM,
}

/** Transition animation styles between images. */
enum class TransitionStyle {
    CROSSFADE,
    SLIDE,
    DEPTH,
    NONE,
}
