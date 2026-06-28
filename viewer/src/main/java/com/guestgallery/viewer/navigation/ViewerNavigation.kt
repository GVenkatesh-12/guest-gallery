package com.guestgallery.viewer.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.guestgallery.viewer.ui.ViewerScreen

/** Navigation route for the image viewer screen. */
const val VIEWER_ROUTE = "viewer"

/**
 * Registers the viewer screen destination in the navigation graph.
 *
 * @param onSettingsClick Callback when settings is requested from the viewer.
 * @param onExitClick     Callback when the user exits the viewer.
 */
fun NavGraphBuilder.viewerScreen(
    onSettingsClick: () -> Unit,
    onExitClick: () -> Unit,
) {
    composable(VIEWER_ROUTE) {
        ViewerScreen(
            onSettingsClick = onSettingsClick,
            onExitClick = onExitClick,
        )
    }
}

/** Navigate to the image viewer screen. */
fun NavController.navigateToViewer() {
    navigate(VIEWER_ROUTE) {
        launchSingleTop = true
    }
}
