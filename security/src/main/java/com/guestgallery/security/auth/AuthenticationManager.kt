package com.guestgallery.security.auth

import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Result of an authentication attempt.
 */
sealed class AuthResult {
    /** Authentication succeeded. */
    data object Success : AuthResult()

    /** Authentication failed (biometric was not recognised). */
    data object Failed : AuthResult()

    /** An unrecoverable error occurred during authentication. */
    data class Error(val message: String) : AuthResult()
}

/**
 * Manages biometric / device-credential authentication via [BiometricPrompt].
 *
 * Call [authenticate] from a [FragmentActivity] to show the system prompt.
 * The result is delivered through the [onResult] callback on the main thread.
 */
@Singleton
class AuthenticationManager
    @Inject
    constructor() {
        /**
         * Shows the biometric authentication prompt.
         *
         * @param activity   The host [FragmentActivity] (required by [BiometricPrompt]).
         * @param title      Title displayed in the prompt dialog.
         * @param subtitle   Subtitle displayed beneath the title.
         * @param onResult   Callback invoked with the [AuthResult] on completion.
         */
        fun authenticate(
            activity: FragmentActivity,
            title: String,
            subtitle: String,
            onResult: (AuthResult) -> Unit,
        ) {
            val executor = ContextCompat.getMainExecutor(activity)

            val callback =
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        onResult(AuthResult.Success)
                    }

                    override fun onAuthenticationFailed() {
                        onResult(AuthResult.Failed)
                    }

                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence,
                    ) {
                        onResult(AuthResult.Error(errString.toString()))
                    }
                }

            val prompt = BiometricPrompt(activity, executor, callback)

            val promptInfo =
                BiometricPrompt.PromptInfo.Builder()
                    .setTitle(title)
                    .setSubtitle(subtitle)
                    .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
                    .build()

            prompt.authenticate(promptInfo)
        }
    }
