package com.guestgallery.data.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * DataStore preference keys for every field in [com.guestgallery.domain.model.AppSettings].
 *
 * Keys are organized by category and use snake_case names that directly
 * correspond to the camelCase field names in [AppSettings].
 */
object PreferencesKeys {

    // ── Security ─────────────────────────────────────────────────────────────
    val ENABLE_SCREEN_PINNING_REMINDER = booleanPreferencesKey("enable_screen_pinning_reminder")
    val AUTO_LOCK_ON_EXIT = booleanPreferencesKey("auto_lock_on_exit")
    val REQUIRE_FINGERPRINT_TO_EXIT = booleanPreferencesKey("require_fingerprint_to_exit")
    val REQUIRE_PIN_TO_EXIT = booleanPreferencesKey("require_pin_to_exit")
    val DISABLE_SCREENSHOTS = booleanPreferencesKey("disable_screenshots")
    val DISABLE_SCREEN_RECORDING = booleanPreferencesKey("disable_screen_recording")
    val HIDE_NOTIFICATIONS = booleanPreferencesKey("hide_notifications")
    val HIDE_STATUS_BAR = booleanPreferencesKey("hide_status_bar")
    val HIDE_NAVIGATION_BAR = booleanPreferencesKey("hide_navigation_bar")
    val PREVENT_BRIGHTNESS_CHANGE = booleanPreferencesKey("prevent_brightness_change")
    val AUTO_CLOSE_AFTER_LAST_IMAGE = booleanPreferencesKey("auto_close_after_last_image")
    val AUTO_CLOSE_AFTER_TIMEOUT_MINUTES = intPreferencesKey("auto_close_after_timeout_minutes")
    val LOCK_ORIENTATION = booleanPreferencesKey("lock_orientation")
    val HIDE_RECENT_APPS_PREVIEW = booleanPreferencesKey("hide_recent_apps_preview")
    val BLANK_SCREEN_ON_APP_SWITCH = booleanPreferencesKey("blank_screen_on_app_switch")
    val SECURE_WINDOW_FLAG = booleanPreferencesKey("secure_window_flag")

    // ── Viewer ───────────────────────────────────────────────────────────────
    val SHOW_IMAGE_COUNTER = booleanPreferencesKey("show_image_counter")
    val SHOW_FILE_NAME = booleanPreferencesKey("show_file_name")
    val SHOW_RESOLUTION = booleanPreferencesKey("show_resolution")
    val SHOW_FILE_SIZE = booleanPreferencesKey("show_file_size")
    val ENABLE_ZOOM = booleanPreferencesKey("enable_zoom")
    val ENABLE_ROTATION = booleanPreferencesKey("enable_rotation")
    val ENABLE_DOUBLE_TAP_ZOOM = booleanPreferencesKey("enable_double_tap_zoom")
    val ENABLE_SLIDESHOW = booleanPreferencesKey("enable_slideshow")
    val LOOP_SLIDESHOW = booleanPreferencesKey("loop_slideshow")
    val SLIDESHOW_SPEED_SECONDS = intPreferencesKey("slideshow_speed_seconds")
    val GESTURE_SENSITIVITY = floatPreferencesKey("gesture_sensitivity")
    val MAXIMUM_ZOOM = floatPreferencesKey("maximum_zoom")
    val KEEP_SCREEN_AWAKE = booleanPreferencesKey("keep_screen_awake")
    val VIEWER_BACKGROUND = stringPreferencesKey("viewer_background")
    val EDGE_PADDING = intPreferencesKey("edge_padding")
    val TRANSITION_STYLE = stringPreferencesKey("transition_style")

    // ── Appearance ───────────────────────────────────────────────────────────
    val THEME_MODE = stringPreferencesKey("theme_mode")
    val DYNAMIC_COLORS = booleanPreferencesKey("dynamic_colors")
    val OLED_BLACK_MODE = booleanPreferencesKey("oled_black_mode")
    val CORNER_RADIUS = intPreferencesKey("corner_radius")
    val ANIMATION_SPEED = floatPreferencesKey("animation_speed")
    val ACCENT_COLOR = longPreferencesKey("accent_color")
    val FONT_SIZE = floatPreferencesKey("font_size")
    val MINIMAL_UI = booleanPreferencesKey("minimal_ui")
    val GLASS_EFFECT = booleanPreferencesKey("glass_effect")
    val BLUR_EFFECTS = booleanPreferencesKey("blur_effects")
    val ROUNDED_BUTTONS = booleanPreferencesKey("rounded_buttons")

    // ── Privacy ──────────────────────────────────────────────────────────────
    val DELETE_SESSION_HISTORY = booleanPreferencesKey("delete_session_history")
    val NEVER_STORE_SHARED_IMAGES = booleanPreferencesKey("never_store_shared_images")
    val CLEAR_CACHE_ON_EXIT = booleanPreferencesKey("clear_cache_on_exit")
    val MEMORY_OPTIMIZATION = booleanPreferencesKey("memory_optimization")
    val AUTO_DELETE_TEMP_FILES = booleanPreferencesKey("auto_delete_temp_files")
    val INCOGNITO_MODE = booleanPreferencesKey("incognito_mode")
    val ANONYMOUS_CRASH_REPORTS = booleanPreferencesKey("anonymous_crash_reports")

    // ── Performance ──────────────────────────────────────────────────────────
    val IMAGE_CACHE_SIZE_MB = intPreferencesKey("image_cache_size_mb")
    val PRELOAD_COUNT = intPreferencesKey("preload_count")
    val HARDWARE_DECODE = booleanPreferencesKey("hardware_decode")
    val SOFTWARE_DECODE = booleanPreferencesKey("software_decode")
    val ANIMATION_QUALITY = stringPreferencesKey("animation_quality")
    val MEMORY_SAVER = booleanPreferencesKey("memory_saver")
    val BATTERY_SAVER = booleanPreferencesKey("battery_saver")

    // ── Accessibility ────────────────────────────────────────────────────────
    val LARGE_TEXT = booleanPreferencesKey("large_text")
    val HIGH_CONTRAST = booleanPreferencesKey("high_contrast")
    val TALK_BACK_SUPPORT = booleanPreferencesKey("talk_back_support")
    val ONE_HAND_MODE = booleanPreferencesKey("one_hand_mode")
    val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
    val REDUCED_MOTION = booleanPreferencesKey("reduced_motion")
}
