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
 * @param isScreenPinned  Whether Android screen pinning is active for the activity.
 */
fun NavGraphBuilder.viewerScreen(
    onExitClick: () -> Unit,
    isScreenPinned: Boolean,
) {
    composable(VIEWER_ROUTE) {
        ViewerScreen(
            onExitClick = onExitClick,
            isScreenPinned = isScreenPinned,
        )
    }
}

/** Navigate to the image viewer screen. */
fun NavController.navigateToViewer() {
    navigate(VIEWER_ROUTE) {
        launchSingleTop = true
    }
}
