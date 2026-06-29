package com.guestgallery.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.guestgallery.domain.model.AppSettings
import com.guestgallery.domain.model.TransitionStyle
import com.guestgallery.domain.model.ViewerBackground
import com.guestgallery.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [SettingsRepository] implementation backed by Jetpack DataStore.
 *
 * Each preference key corresponds to a field in [AppSettings]. The
 * [observeSettings] method maps the raw DataStore [Preferences] into the
 * domain model, falling back to [AppSettings] defaults for any missing key.
 */
@Singleton
class SettingsRepositoryImpl
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
        @ApplicationContext private val context: Context,
    ) : SettingsRepository {
        private val defaults = AppSettings()

        @Suppress("LongMethod", "CyclomaticComplexMethod")
        override fun observeSettings(): Flow<AppSettings> =
            dataStore.data.map { prefs ->
                AppSettings(
                    // ── Security ─────────────────────────────────────────────────────
                    enableScreenPinningReminder =
                        prefs[PreferencesKeys.ENABLE_SCREEN_PINNING_REMINDER]
                            ?: defaults.enableScreenPinningReminder,
                    autoLockOnExit =
                        prefs[PreferencesKeys.AUTO_LOCK_ON_EXIT]
                            ?: defaults.autoLockOnExit,
                    requireFingerprintToExit =
                        prefs[PreferencesKeys.REQUIRE_FINGERPRINT_TO_EXIT]
                            ?: defaults.requireFingerprintToExit,
                    requirePinToExit =
                        prefs[PreferencesKeys.REQUIRE_PIN_TO_EXIT]
                            ?: defaults.requirePinToExit,
                    disableScreenshots =
                        prefs[PreferencesKeys.DISABLE_SCREENSHOTS]
                            ?: defaults.disableScreenshots,
                    disableScreenRecording =
                        prefs[PreferencesKeys.DISABLE_SCREEN_RECORDING]
                            ?: defaults.disableScreenRecording,
                    hideNotifications =
                        prefs[PreferencesKeys.HIDE_NOTIFICATIONS]
                            ?: defaults.hideNotifications,
                    hideStatusBar =
                        prefs[PreferencesKeys.HIDE_STATUS_BAR]
                            ?: defaults.hideStatusBar,
                    hideNavigationBar =
                        prefs[PreferencesKeys.HIDE_NAVIGATION_BAR]
                            ?: defaults.hideNavigationBar,
                    preventBrightnessChange =
                        prefs[PreferencesKeys.PREVENT_BRIGHTNESS_CHANGE]
                            ?: defaults.preventBrightnessChange,
                    autoCloseAfterLastImage =
                        prefs[PreferencesKeys.AUTO_CLOSE_AFTER_LAST_IMAGE]
                            ?: defaults.autoCloseAfterLastImage,
                    autoCloseAfterTimeoutMinutes =
                        prefs[PreferencesKeys.AUTO_CLOSE_AFTER_TIMEOUT_MINUTES]
                            ?: defaults.autoCloseAfterTimeoutMinutes,
                    lockOrientation =
                        prefs[PreferencesKeys.LOCK_ORIENTATION]
                            ?: defaults.lockOrientation,
                    hideRecentAppsPreview =
                        prefs[PreferencesKeys.HIDE_RECENT_APPS_PREVIEW]
                            ?: defaults.hideRecentAppsPreview,
                    blankScreenOnAppSwitch =
                        prefs[PreferencesKeys.BLANK_SCREEN_ON_APP_SWITCH]
                            ?: defaults.blankScreenOnAppSwitch,
                    secureWindowFlag =
                        prefs[PreferencesKeys.SECURE_WINDOW_FLAG]
                            ?: defaults.secureWindowFlag,
                    // ── Viewer ───────────────────────────────────────────────────────
                    showImageCounter =
                        prefs[PreferencesKeys.SHOW_IMAGE_COUNTER]
                            ?: defaults.showImageCounter,
                    showFileName =
                        prefs[PreferencesKeys.SHOW_FILE_NAME]
                            ?: defaults.showFileName,
                    showResolution =
                        prefs[PreferencesKeys.SHOW_RESOLUTION]
                            ?: defaults.showResolution,
                    showFileSize =
                        prefs[PreferencesKeys.SHOW_FILE_SIZE]
                            ?: defaults.showFileSize,
                    enableZoom =
                        prefs[PreferencesKeys.ENABLE_ZOOM]
                            ?: defaults.enableZoom,
                    enableRotation =
                        prefs[PreferencesKeys.ENABLE_ROTATION]
                            ?: defaults.enableRotation,
                    enableDoubleTapZoom =
                        prefs[PreferencesKeys.ENABLE_DOUBLE_TAP_ZOOM]
                            ?: defaults.enableDoubleTapZoom,
                    enableSlideshow =
                        prefs[PreferencesKeys.ENABLE_SLIDESHOW]
                            ?: defaults.enableSlideshow,
                    loopSlideshow =
                        prefs[PreferencesKeys.LOOP_SLIDESHOW]
                            ?: defaults.loopSlideshow,
                    slideshowSpeedSeconds =
                        prefs[PreferencesKeys.SLIDESHOW_SPEED_SECONDS]
                            ?: defaults.slideshowSpeedSeconds,
                    gestureSensitivity =
                        prefs[PreferencesKeys.GESTURE_SENSITIVITY]
                            ?: defaults.gestureSensitivity,
                    maximumZoom =
                        prefs[PreferencesKeys.MAXIMUM_ZOOM]
                            ?: defaults.maximumZoom,
                    keepScreenAwake =
                        prefs[PreferencesKeys.KEEP_SCREEN_AWAKE]
                            ?: defaults.keepScreenAwake,
                    viewerBackground =
                        prefs[PreferencesKeys.VIEWER_BACKGROUND]
                            ?.let { runCatching { ViewerBackground.valueOf(it) }.getOrNull() }
                            ?: defaults.viewerBackground,
                    edgePadding =
                        prefs[PreferencesKeys.EDGE_PADDING]
                            ?: defaults.edgePadding,
                    transitionStyle =
                        prefs[PreferencesKeys.TRANSITION_STYLE]
                            ?.let { runCatching { TransitionStyle.valueOf(it) }.getOrNull() }
                            ?: defaults.transitionStyle,
                    // ── Appearance ───────────────────────────────────────────────────
                    themeMode =
                        prefs[PreferencesKeys.THEME_MODE]
                            ?: defaults.themeMode,
                    dynamicColors =
                        prefs[PreferencesKeys.DYNAMIC_COLORS]
                            ?: defaults.dynamicColors,
                    oledBlackMode =
                        prefs[PreferencesKeys.OLED_BLACK_MODE]
                            ?: defaults.oledBlackMode,
                    cornerRadius =
                        prefs[PreferencesKeys.CORNER_RADIUS]
                            ?: defaults.cornerRadius,
                    animationSpeed =
                        prefs[PreferencesKeys.ANIMATION_SPEED]
                            ?: defaults.animationSpeed,
                    accentColor =
                        prefs[PreferencesKeys.ACCENT_COLOR]
                            ?: defaults.accentColor,
                    fontSize =
                        prefs[PreferencesKeys.FONT_SIZE]
                            ?: defaults.fontSize,
                    minimalUi =
                        prefs[PreferencesKeys.MINIMAL_UI]
                            ?: defaults.minimalUi,
                    glassEffect =
                        prefs[PreferencesKeys.GLASS_EFFECT]
                            ?: defaults.glassEffect,
                    blurEffects =
                        prefs[PreferencesKeys.BLUR_EFFECTS]
                            ?: defaults.blurEffects,
                    roundedButtons =
                        prefs[PreferencesKeys.ROUNDED_BUTTONS]
                            ?: defaults.roundedButtons,
                    // ── Privacy ──────────────────────────────────────────────────────
                    deleteSessionHistory =
                        prefs[PreferencesKeys.DELETE_SESSION_HISTORY]
                            ?: defaults.deleteSessionHistory,
                    neverStoreSharedImages =
                        prefs[PreferencesKeys.NEVER_STORE_SHARED_IMAGES]
                            ?: defaults.neverStoreSharedImages,
                    clearCacheOnExit =
                        prefs[PreferencesKeys.CLEAR_CACHE_ON_EXIT]
                            ?: defaults.clearCacheOnExit,
                    memoryOptimization =
                        prefs[PreferencesKeys.MEMORY_OPTIMIZATION]
                            ?: defaults.memoryOptimization,
                    autoDeleteTempFiles =
                        prefs[PreferencesKeys.AUTO_DELETE_TEMP_FILES]
                            ?: defaults.autoDeleteTempFiles,
                    incognitoMode =
                        prefs[PreferencesKeys.INCOGNITO_MODE]
                            ?: defaults.incognitoMode,
                    anonymousCrashReports =
                        prefs[PreferencesKeys.ANONYMOUS_CRASH_REPORTS]
                            ?: defaults.anonymousCrashReports,
                    // ── Performance ──────────────────────────────────────────────────
                    imageCacheSizeMb =
                        prefs[PreferencesKeys.IMAGE_CACHE_SIZE_MB]
                            ?: defaults.imageCacheSizeMb,
                    preloadCount =
                        prefs[PreferencesKeys.PRELOAD_COUNT]
                            ?: defaults.preloadCount,
                    hardwareDecode =
                        prefs[PreferencesKeys.HARDWARE_DECODE]
                            ?: defaults.hardwareDecode,
                    softwareDecode =
                        prefs[PreferencesKeys.SOFTWARE_DECODE]
                            ?: defaults.softwareDecode,
                    animationQuality =
                        prefs[PreferencesKeys.ANIMATION_QUALITY]
                            ?: defaults.animationQuality,
                    memorySaver =
                        prefs[PreferencesKeys.MEMORY_SAVER]
                            ?: defaults.memorySaver,
                    batterySaver =
                        prefs[PreferencesKeys.BATTERY_SAVER]
                            ?: defaults.batterySaver,
                    // ── Accessibility ────────────────────────────────────────────────
                    largeText =
                        prefs[PreferencesKeys.LARGE_TEXT]
                            ?: defaults.largeText,
                    highContrast =
                        prefs[PreferencesKeys.HIGH_CONTRAST]
                            ?: defaults.highContrast,
                    talkBackSupport =
                        prefs[PreferencesKeys.TALK_BACK_SUPPORT]
                            ?: defaults.talkBackSupport,
                    oneHandMode =
                        prefs[PreferencesKeys.ONE_HAND_MODE]
                            ?: defaults.oneHandMode,
                    hapticFeedback =
                        prefs[PreferencesKeys.HAPTIC_FEEDBACK]
                            ?: defaults.hapticFeedback,
                    reducedMotion =
                        prefs[PreferencesKeys.REDUCED_MOTION]
                            ?: defaults.reducedMotion,
                )
            }

        override suspend fun updateBooleanSetting(
            key: String,
            value: Boolean,
        ) {
            if (shouldSkipPersisting(key)) return

            val prefKey = booleanPreferencesKey(key.toPreferenceName())
            dataStore.edit { it[prefKey] = value }
        }

        override suspend fun updateIntSetting(
            key: String,
            value: Int,
        ) {
            if (shouldSkipPersisting(key)) return

            val prefKey = intPreferencesKey(key.toPreferenceName())
            dataStore.edit { it[prefKey] = value }
        }

        override suspend fun updateFloatSetting(
            key: String,
            value: Float,
        ) {
            if (shouldSkipPersisting(key)) return

            val prefKey = floatPreferencesKey(key.toPreferenceName())
            dataStore.edit { it[prefKey] = value }
        }

        override suspend fun updateStringSetting(
            key: String,
            value: String,
        ) {
            if (shouldSkipPersisting(key)) return

            val prefKey = stringPreferencesKey(key.toPreferenceName())
            dataStore.edit { it[prefKey] = value }
        }

        override suspend fun updateLongSetting(
            key: String,
            value: Long,
        ) {
            if (shouldSkipPersisting(key)) return

            val prefKey = longPreferencesKey(key.toPreferenceName())
            dataStore.edit { it[prefKey] = value }
        }

        override suspend fun resetToDefaults() {
            dataStore.edit { it.clear() }
        }

        override suspend fun clearCache() {
            context.cacheDir.deleteContents()
            context.externalCacheDirs.filterNotNull().forEach { it.deleteContents() }
        }

        private suspend fun shouldSkipPersisting(key: String): Boolean {
            val preferenceName = key.toPreferenceName()
            if (preferenceName == PreferencesKeys.INCOGNITO_MODE.name) return false

            return dataStore.data.first()[PreferencesKeys.INCOGNITO_MODE] == true
        }
    }

private val camelCaseBoundary = Regex("([a-z0-9])([A-Z])")

private fun String.toPreferenceName(): String =
    if ('_' in this) {
        this
    } else {
        replace(camelCaseBoundary, "$1_$2").lowercase()
    }

private fun File.deleteContents() {
    listFiles()?.forEach { child ->
        if (child.isDirectory) {
            child.deleteRecursively()
        } else {
            child.delete()
        }
    }
}
