package com.guestgallery.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.guestgallery.settings.ui.SettingsScreen

const val SETTINGS_ROUTE = "settings"

fun NavController.navigateToSettings(navOptions: NavOptions? = null) {
    navigate(SETTINGS_ROUTE, navOptions)
}

fun NavGraphBuilder.settingsScreen(
    onBackClick: () -> Unit,
) {
    composable(route = SETTINGS_ROUTE) {
        SettingsScreen(
            onBackClick = onBackClick,
        )
    }
}
