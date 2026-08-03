@file:Suppress("DEPRECATION") // Parcelable extras backward compat

package com.guestgallery.app

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.guestgallery.app.navigation.AppNavHost
import com.guestgallery.core.theme.GuestGalleryTheme
import com.guestgallery.core.theme.ThemeMode
import com.guestgallery.domain.model.AppSettings
import com.guestgallery.security.lockdown.ScreenPinningHelper
import com.guestgallery.security.ui.ScreenPinningGuideDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Single-activity host for Guest Gallery.
 *
 * Handles:
 * - Splash screen installation (before super.onCreate)
 * - Parsing incoming share intents (ACTION_SEND / ACTION_SEND_MULTIPLE)
 * - Applying security window flags based on user settings
 * - Composing the root UI with [GuestGalleryTheme] + [AppNavHost]
 */
@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var screenPinningHelper: ScreenPinningHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        // Must be called before super.onCreate per the SplashScreen API contract
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { viewModel.settings.value == null }

        enableEdgeToEdge()

        // Parse incoming images on first launch (not on config change)
        if (savedInstanceState == null) {
            handleIncomingIntent(intent)
        }

        // Reactively apply security-related window flags
        observeSecuritySettings()

        // React to finish events from ViewModel
        observeFinishEvent()

        setContent {
            val settings by viewModel.settings.collectAsStateWithLifecycle()
            val appState by viewModel.appState.collectAsStateWithLifecycle()
            val currentSettings = settings ?: return@setContent
            var showPinningGuide by remember { mutableStateOf(false) }
            var pinningGuideShown by remember { mutableStateOf(false) }

            LaunchedEffect(appState, currentSettings.enableScreenPinningReminder) {
                if (appState !is AppState.Viewing) {
                    pinningGuideShown = false
                    showPinningGuide = false
                    return@LaunchedEffect
                }

                if (
                    currentSettings.enableScreenPinningReminder &&
                    !pinningGuideShown &&
                    !screenPinningHelper.isScreenPinningActive(this@MainActivity)
                ) {
                    pinningGuideShown = true
                    showPinningGuide = true
                }
            }

            val themeMode =
                when (currentSettings.themeMode) {
                    "light" -> ThemeMode.LIGHT
                    "dark" -> ThemeMode.DARK
                    else -> ThemeMode.SYSTEM
                }

            GuestGalleryTheme(
                themeMode = themeMode,
                dynamicColor = currentSettings.dynamicColors,
                oledMode = currentSettings.oledBlackMode,
            ) {
                AppNavHost(
                    mainViewModel = viewModel,
                )

                if (showPinningGuide) {
                    ScreenPinningGuideDialog(
                        onDismiss = { showPinningGuide = false },
                        onPinApp = {
                            showPinningGuide = false
                            screenPinningHelper.requestScreenPinning(this@MainActivity)
                        },
                    )
                }
            }
        }
    }

    /**
     * Called when a new intent is delivered to this singleTask activity
     * (e.g., user shares more images while the app is already running).
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    // ── Intent Parsing ───────────────────────────────────────────────────────

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent == null) return

        val uris = mutableListOf<String>()

        when (intent.action) {
            Intent.ACTION_SEND -> {
                val uri: Uri? =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
                    } else {
                        intent.getParcelableExtra(Intent.EXTRA_STREAM)
                    }
                uri?.let { uris.add(it.toString()) }
            }

            Intent.ACTION_SEND_MULTIPLE -> {
                val uriList: List<Uri>? =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableArrayListExtra(
                            Intent.EXTRA_STREAM,
                            Uri::class.java,
                        )
                    } else {
                        intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM)
                    }
                uriList?.forEach { uris.add(it.toString()) }
            }
        }

        if (uris.isNotEmpty()) {
            viewModel.onImagesReceived(uris)
        }
    }

    // ── Security Window Flags ────────────────────────────────────────────────

    private fun observeSecuritySettings() {
        lifecycleScope.launch {
            combine(viewModel.settings, viewModel.appState) { settings, appState ->
                settings to appState
            }.collectLatest { (settings, appState) ->
                if (settings == null) return@collectLatest
                applySecurityFlags(settings, appState is AppState.Viewing)
            }
        }
    }

    private fun applySecurityFlags(
        settings: AppSettings,
        secureMode: Boolean,
    ) {
        if (!secureMode) {
            window.clearFlags(
                WindowManager.LayoutParams.FLAG_SECURE or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            )
            WindowCompat.getInsetsController(window, window.decorView)
                .show(WindowInsetsCompat.Type.systemBars())
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            return
        }

        val shouldSecureWindow =
            settings.secureWindowFlag ||
                settings.disableScreenshots ||
                settings.disableScreenRecording ||
                settings.hideRecentAppsPreview ||
                settings.blankScreenOnAppSwitch

        // FLAG_SECURE prevents screenshots, screen recording, and overview previews.
        if (shouldSecureWindow) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }

        // Keep screen awake while viewing
        if (settings.keepScreenAwake) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        // Immersive mode: hide status bar and/or navigation bar
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        when {
            settings.hideStatusBar && settings.hideNavigationBar -> {
                insetsController.hide(WindowInsetsCompat.Type.systemBars())
            }
            settings.hideStatusBar -> {
                insetsController.hide(WindowInsetsCompat.Type.statusBars())
                insetsController.show(WindowInsetsCompat.Type.navigationBars())
            }
            settings.hideNavigationBar -> {
                insetsController.show(WindowInsetsCompat.Type.statusBars())
                insetsController.hide(WindowInsetsCompat.Type.navigationBars())
            }
            else -> {
                insetsController.show(WindowInsetsCompat.Type.systemBars())
            }
        }

        requestedOrientation =
            if (settings.lockOrientation) {
                ActivityInfo.SCREEN_ORIENTATION_LOCKED
            } else {
                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
    }

    // ── Finish Event ─────────────────────────────────────────────────────────

    private fun observeFinishEvent() {
        lifecycleScope.launch {
            viewModel.finishEvent.collectLatest { shouldFinish ->
                if (shouldFinish) {
                    viewModel.onFinishConsumed()
                    finish()
                }
            }
        }
    }
}
