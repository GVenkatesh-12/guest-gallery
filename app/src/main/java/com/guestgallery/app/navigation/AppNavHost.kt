package com.guestgallery.app.navigation

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
import com.guestgallery.settings.navigation.navigateToSettings
import com.guestgallery.settings.navigation.settingsScreen
import com.guestgallery.viewer.navigation.viewerScreen

@Composable
fun AppNavHost(
    mainViewModel: MainViewModel,
    onExitClick: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = if (mainViewModel.appState.value is AppState.Viewing) Routes.VIEWER else Routes.WELCOME,
) {
    val appState by mainViewModel.appState.collectAsStateWithLifecycle()

    LaunchedEffect(appState) {
        when (appState) {
            AppState.Welcome -> {
                navController.navigate(Routes.WELCOME) {
                    popUpTo(0) { inclusive = true }
                }
            }
            is AppState.Viewing -> {
                navController.navigate(Routes.VIEWER) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(route = Routes.WELCOME) {
            WelcomeScreen(
                onOpenSettings = { navController.navigateToSettings() },
                onAboutClick = { navController.navigate(Routes.ABOUT) },
            )
        }

        viewerScreen(onExitClick = onExitClick)

        settingsScreen(onBackClick = { navController.popBackStack() })

        composable(route = Routes.ABOUT) {
            AboutScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
