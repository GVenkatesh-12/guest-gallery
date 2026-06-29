@file:Suppress("DEPRECATION") // Parcelable extras backward compat

package com.guestgallery.app

import android.content.pm.ActivityInfo
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.guestgallery.app.navigation.AppNavHost
import com.guestgallery.core.theme.GuestGalleryTheme
import com.guestgallery.core.theme.ThemeMode
import com.guestgallery.domain.model.AppSettings
import dagger.hilt.android.AndroidEntryPoint
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
            val currentSettings = settings ?: return@setContent

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
            viewModel.settings.collectLatest { settings ->
                if (settings == null) return@collectLatest
                applySecurityFlags(settings)
            }
        }
    }

    private fun applySecurityFlags(settings: AppSettings) {
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
