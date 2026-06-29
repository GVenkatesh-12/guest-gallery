package com.guestgallery.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.guestgallery.app.R
import com.guestgallery.core.theme.Dimens
import com.guestgallery.core.ui.components.GuestGalleryTopBar

@Composable
fun AboutScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            GuestGalleryTopBar(
                title = stringResource(id = R.string.about_title),
                onBackClick = onBackClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = Dimens.PaddingScreen)
                    .verticalScroll(scrollState),
        ) {
            Spacer(modifier = Modifier.height(Dimens.SpacingLg))

            // App Name & Version
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = stringResource(id = R.string.version, "1.0.0"),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(Dimens.SpacingXl))

            // Privacy Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                    ),
            ) {
                Column(modifier = Modifier.padding(Dimens.PaddingCard)) {
                    Text(
                        text = "Privacy Promise",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = stringResource(id = R.string.privacy_promise),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = Dimens.SpacingXs),
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SpacingXl))

            // Details
            Text(
                text = "Secure Viewing System",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text =
                    "Guest Gallery starts a sandboxed temporary viewing session when photos are " +
                        "shared. The application enforces dynamic window flag configurations, prevents " +
                        "taking screenshots/recordings, hides status and navigation bars, and offers " +
                        "guided device-level screen pinning to protect other device data.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = Dimens.SpacingXs),
            )

            Spacer(modifier = Modifier.height(Dimens.SpacingXl))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(Dimens.SpacingXl))

            // Acknowledgements
            Text(
                text = "Open Source Acknowledgements",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(Dimens.SpacingMd))

            DependencyItem(name = "Jetpack Compose", license = "Apache License 2.0")
            DependencyItem(name = "Dagger Hilt", license = "Apache License 2.0")
            DependencyItem(name = "Coil Image Loader", license = "Apache License 2.0")
            DependencyItem(name = "Telephoto Zoom Library", license = "Apache License 2.0")
            DependencyItem(name = "AndroidX Biometric", license = "Apache License 2.0")

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun DependencyItem(
    name: String,
    license: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = Dimens.SpacingXs),
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = license,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
