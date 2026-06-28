package com.guestgallery.security.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.guestgallery.core.theme.Dimens
import com.guestgallery.core.ui.components.FadeAnimatedVisibility
import com.guestgallery.security.auth.AuthResult
import com.guestgallery.security.auth.AuthenticationManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.launch

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SecurityEntryPoint {
    fun authenticationManager(): AuthenticationManager
}

/** Lock icon size on the exit auth screen. */
private val LockIconSize = 72.dp

/** Fade-in duration for the screen entrance animation. */
private const val FADE_IN_DURATION_MS = 500

/**
 * Full-screen composable that blocks app exit until the device owner
 * authenticates via biometrics or device credential.
 *
 * @param authenticationManager  Injected [AuthenticationManager] instance.
 * @param onAuthenticated        Called once authentication succeeds, allowing the caller
 *                               to proceed with the exit flow.
 */
@Composable
fun ExitAuthScreen(
    onAuthenticated: () -> Unit,
    onCancelled: () -> Unit,
    modifier: Modifier = Modifier,
    authenticationManager: AuthenticationManager = EntryPointAccessors.fromApplication(
        LocalContext.current.applicationContext,
        SecurityEntryPoint::class.java
    ).authenticationManager(),
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Intercept back key to return to viewing mode
    BackHandler {
        onCancelled()
    }

    // Auto-trigger authentication prompt when screen is shown
    LaunchedEffect(Unit) {
        val activity = context as? FragmentActivity
        if (activity != null) {
            authenticationManager.authenticate(
                activity = activity,
                title = "Exit Guest Gallery",
                subtitle = "Verify your identity to exit",
                onResult = { result ->
                    when (result) {
                        is AuthResult.Success -> onAuthenticated()
                        else -> { /* Let user tap manual button if auto fails */ }
                    }
                }
            )
        }
    }

    // Controls the entrance fade-in animation.
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background,
        ) {
            FadeAnimatedVisibility(
                visible = visible,
                durationMs = FADE_IN_DURATION_MS,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Dimens.PaddingScreen),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    // Lock icon
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = "Locked",
                        modifier = Modifier.size(LockIconSize),
                        tint = MaterialTheme.colorScheme.primary,
                    )

                    Spacer(modifier = Modifier.height(Dimens.SpacingXl))

                    // Title
                    Text(
                        text = "Authenticate to Exit",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(Dimens.SpacingSm))

                    // Subtitle
                    Text(
                        text = "Authenticate to exit Guest Gallery",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(Dimens.SpacingXxl))

                    // Authenticate button
                    Button(
                        onClick = {
                            val activity = context as? FragmentActivity
                            if (activity != null) {
                                authenticationManager.authenticate(
                                    activity = activity,
                                    title = "Exit Guest Gallery",
                                    subtitle = "Verify your identity to exit",
                                    onResult = { result ->
                                        when (result) {
                                            is AuthResult.Success -> onAuthenticated()
                                            is AuthResult.Failed -> {
                                                scope.launch {
                                                    snackbarHostState.showSnackbar(
                                                        "Authentication failed. Try again.",
                                                    )
                                                }
                                            }
                                            is AuthResult.Error -> {
                                                scope.launch {
                                                    snackbarHostState.showSnackbar(
                                                        result.message,
                                                    )
                                                }
                                            }
                                        }
                                    },
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                        )
                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                        Text("Authenticate")
                    }

                    Spacer(modifier = Modifier.height(Dimens.SpacingSm))

                    // Cancel button
                    TextButton(
                        onClick = onCancelled,
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}
