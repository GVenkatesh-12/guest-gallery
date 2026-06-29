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
 * @param onExitClick     Callback when the user exits the viewer.
 */
fun NavGraphBuilder.viewerScreen(
    onExitClick: () -> Unit,
) {
    composable(VIEWER_ROUTE) {
        ViewerScreen(
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
