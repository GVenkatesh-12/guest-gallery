package com.guestgallery.domain.model

/**
 * The only optional behavior in the guest-viewing flow.
 *
 * Secure fullscreen viewing, memory-only image handling, and cache cleanup are
 * fixed safeguards rather than configuration switches.
 */
data class AppSettings(
    val enableScreenPinningReminder: Boolean = true,
)
