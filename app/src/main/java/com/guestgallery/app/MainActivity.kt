@file:Suppress("DEPRECATION")

package com.guestgallery.app

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.guestgallery.app.navigation.AppNavHost
import com.guestgallery.core.theme.GuestGalleryTheme
import com.guestgallery.security.lockdown.ScreenPinningHelper
import com.guestgallery.security.ui.ScreenPinningGuideDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Hosts the focused guest-viewing flow and applies its fixed safeguards. */
@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private val viewModel: MainViewModel by viewModels()

    private var isScreenPinned by mutableStateOf(false)

    @Inject
    lateinit var screenPinningHelper: ScreenPinningHelper

    @Suppress("LongMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { viewModel.settings.value == null }
        enableEdgeToEdge()
        refreshScreenPinningState()

        if (savedInstanceState == null) {
            handleIncomingIntent(intent)
        }

        observeViewerSecurity()
        observeFinishEvent()
        observeScreenPinningState()

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
                    delay(PINNING_GUIDE_DELAY_MS)
                    showPinningGuide = true
                }
            }

            GuestGalleryTheme {
                AppNavHost(
                    mainViewModel = viewModel,
                    onExitClick = ::requestExit,
                    isScreenPinned = isScreenPinned,
                )

                if (showPinningGuide) {
                    ScreenPinningGuideDialog(
                        onDismiss = { showPinningGuide = false },
                        onPinApp = {
                            showPinningGuide = false
                            screenPinningHelper.requestScreenPinning(this@MainActivity)
                            refreshScreenPinningState()
                        },
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        refreshScreenPinningState()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) refreshScreenPinningState()
    }

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

        if (uris.isNotEmpty()) viewModel.onImagesReceived(uris)
    }

    private fun observeViewerSecurity() {
        lifecycleScope.launch {
            viewModel.appState.collectLatest { appState ->
                applyViewerSecurity(appState is AppState.Viewing)
            }
        }
    }

    private fun applyViewerSecurity(viewing: Boolean) {
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        if (viewing) {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SECURE or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            )
            insetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            insetsController.hide(WindowInsetsCompat.Type.systemBars())
        } else {
            window.clearFlags(
                WindowManager.LayoutParams.FLAG_SECURE or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            )
            insetsController.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    private fun observeFinishEvent() {
        lifecycleScope.launch {
            viewModel.finishEvent.collectLatest { shouldFinish ->
                if (shouldFinish) {
                    viewModel.onFinishConsumed()
                    finishAndRemoveTask()
                }
            }
        }
    }

    /**
     * Screen pinning can end through a system gesture without an activity lifecycle event.
     * Keeping this state current lets Compose stop intercepting Back while pinned and restore
     * normal exit behavior immediately after Android has unpinned the task.
     */
    private fun observeScreenPinningState() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                while (isActive) {
                    refreshScreenPinningState()
                    delay(SCREEN_PINNING_STATE_REFRESH_MS)
                }
            }
        }
    }

    private fun refreshScreenPinningState() {
        isScreenPinned = screenPinningHelper.isScreenPinningActive(this)
    }

    private fun requestExit() {
        if (screenPinningHelper.isScreenPinningActive(this)) {
            Toast.makeText(this, R.string.unpin_to_exit, Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.requestExit()
    }
}

private const val PINNING_GUIDE_DELAY_MS = 260L
private const val SCREEN_PINNING_STATE_REFRESH_MS = 250L
