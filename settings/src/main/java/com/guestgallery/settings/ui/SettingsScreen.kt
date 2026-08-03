package com.guestgallery.settings.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PinDrop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.guestgallery.core.theme.Dimens
import com.guestgallery.core.ui.components.GuestGalleryTopBar
import com.guestgallery.settings.ui.components.SettingToggleItem

/**
 * Keeps the only user choice that changes the core guest-viewing workflow.
 */
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            GuestGalleryTopBar(
                title = "Screen pinning",
                onBackClick = onBackClick,
            )
        },
    ) { innerPadding ->
        val currentSettings = settings
        if (currentSettings == null) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(top = Dimens.SpacingMd),
            ) {
                Text(
                    text = "Protect a shared viewing session",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimens.PaddingScreen),
                )
                Text(
                    text = "Screen pinning is the reliable system control that keeps guests in Guest Gallery.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = Dimens.PaddingScreen,
                                vertical = Dimens.SpacingXs,
                            ),
                )
                SettingToggleItem(
                    title = "Show screen-pinning reminder",
                    subtitle = "Prompt before opening a shared photo session",
                    checked = currentSettings.enableScreenPinningReminder,
                    onCheckedChange = viewModel::setScreenPinningReminder,
                    icon = Icons.Rounded.PinDrop,
                )
            }
        }
    }
}
