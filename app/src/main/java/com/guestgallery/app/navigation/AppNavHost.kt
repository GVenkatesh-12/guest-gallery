package com.guestgallery.app.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.guestgallery.app.AppState
import com.guestgallery.app.MainViewModel
import com.guestgallery.app.ui.AboutScreen
import com.guestgallery.app.ui.WelcomeScreen
import com.guestgallery.security.ui.ExitAuthScreen
import com.guestgallery.settings.navigation.navigateToSettings
import com.guestgallery.settings.navigation.settingsScreen
import com.guestgallery.viewer.navigation.viewerScreen

@Composable
fun AppNavHost(
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = if (mainViewModel.appState.value is AppState.Viewing) Routes.VIEWER else Routes.WELCOME,
) {
    val appState by mainViewModel.appState.collectAsStateWithLifecycle()

    LaunchedEffect(appState) {
        when (appState) {
            is AppState.Welcome -> {
                navController.navigate(Routes.WELCOME) {
                    popUpTo(0) { inclusive = true }
                }
            }
            is AppState.Viewing -> {
                navController.navigate(Routes.VIEWER) {
                    popUpTo(0) { inclusive = true }
                }
            }
            is AppState.ExitAuth -> {
                navController.navigate(Routes.EXIT_AUTH) {
                    launchSingleTop = true
                }
            }
            else -> {}
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() },
    ) {
        // Welcome route
        composable(route = Routes.WELCOME) {
            WelcomeScreen(
                onOpenSettings = { navController.navigateToSettings() },
                onAboutClick = { navController.navigate(Routes.ABOUT) },
            )
        }

        // Viewer route
        viewerScreen(
            onSettingsClick = { navController.navigateToSettings() },
            onExitClick = { mainViewModel.requestExit() },
        )

        // Settings route
        settingsScreen(
            onBackClick = { navController.popBackStack() },
        )

        // About route
        composable(route = Routes.ABOUT) {
            AboutScreen(
                onBackClick = { navController.popBackStack() },
            )
        }

        // Exit Auth route
        composable(route = Routes.EXIT_AUTH) {
            ExitAuthScreen(
                onAuthenticated = {
                    mainViewModel.onAuthSuccess()
                },
                onCancelled = {
                    mainViewModel.onAuthCancel()
                },
            )
        }
    }
}
