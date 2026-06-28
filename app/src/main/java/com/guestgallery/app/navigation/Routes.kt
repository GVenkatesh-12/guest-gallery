package com.guestgallery.app.navigation

/**
 * Centralized navigation route constants for the app navigation graph.
 * Keeping all routes in a single object prevents magic-string duplication
 * and makes refactoring safer.
 */
object Routes {
    const val WELCOME = "welcome"
    const val VIEWER = "viewer"
    const val SETTINGS = "settings"
    const val ABOUT = "about"
    const val EXIT_AUTH = "exit_auth"
}
