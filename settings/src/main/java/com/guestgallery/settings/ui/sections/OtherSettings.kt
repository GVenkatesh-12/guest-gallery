package com.guestgallery.settings.ui.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatterySaver
import androidx.compose.material.icons.rounded.Cached
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.HistoryToggleOff
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.SettingsAccessibility
import androidx.compose.material.icons.rounded.SpeakerPhone
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.guestgallery.domain.model.AppSettings
import com.guestgallery.settings.ui.SettingsViewModel
import com.guestgallery.settings.ui.components.SettingSliderItem
import com.guestgallery.settings.ui.components.SettingToggleItem

@Composable
fun PrivacySettingsSection(
    settings: AppSettings,
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SettingToggleItem(
            title = "Delete Session History",
            subtitle = "Wipe history of viewed files",
            checked = settings.deleteSessionHistory,
            onCheckedChange = { viewModel.updateBoolean("deleteSessionHistory", it) },
            icon = Icons.Rounded.History,
        )
        SettingToggleItem(
            title = "Never Store Shared Images",
            subtitle = "Ensure files stay in RAM only",
            checked = settings.neverStoreSharedImages,
            onCheckedChange = { viewModel.updateBoolean("neverStoreSharedImages", it) },
            icon = Icons.Rounded.Security,
        )
        SettingToggleItem(
            title = "Clear Cache on Exit",
            subtitle = "Purge cached thumbnails/temp files on exit",
            checked = settings.clearCacheOnExit,
            onCheckedChange = { viewModel.updateBoolean("clearCacheOnExit", it) },
            icon = Icons.Rounded.Cached,
        )
        SettingToggleItem(
            title = "Incognito Mode",
            subtitle = "Completely disables storing any settings updates",
            checked = settings.incognitoMode,
            onCheckedChange = { viewModel.updateBoolean("incognitoMode", it) },
            icon = Icons.Rounded.HistoryToggleOff,
        )
    }
}

@Composable
fun PerformanceSettingsSection(
    settings: AppSettings,
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SettingSliderItem(
            title = "Image Cache Size limit",
            subtitle = "Max disk/memory space allocation",
            value = settings.imageCacheSizeMb.toFloat(),
            onValueChange = { viewModel.updateInt("imageCacheSizeMb", it.toInt()) },
            valueRange = 64f..512f,
            steps = 7,
            valueLabel = "${settings.imageCacheSizeMb} MB",
            icon = Icons.Rounded.Memory,
        )
        SettingSliderItem(
            title = "Preload Count",
            subtitle = "Pages to cache in advance",
            value = settings.preloadCount.toFloat(),
            onValueChange = { viewModel.updateInt("preloadCount", it.toInt()) },
            valueRange = 1f..5f,
            steps = 4,
            valueLabel = "${settings.preloadCount} pages",
            icon = Icons.Rounded.Cached,
        )
        SettingToggleItem(
            title = "Battery Saver Integration",
            subtitle = "Disable anims automatically if battery low",
            checked = settings.batterySaver,
            onCheckedChange = { viewModel.updateBoolean("batterySaver", it) },
            icon = Icons.Rounded.BatterySaver,
        )
    }
}

@Composable
fun AccessibilitySettingsSection(
    settings: AppSettings,
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SettingToggleItem(
            title = "High Contrast Mode",
            subtitle = "Use bold high contrast color styling",
            checked = settings.highContrast,
            onCheckedChange = { viewModel.updateBoolean("highContrast", it) },
            icon = Icons.Rounded.SettingsAccessibility,
        )
        SettingToggleItem(
            title = "Enable Haptic Feedback",
            subtitle = "Vibrate on tapping toggles and zoom limits",
            checked = settings.hapticFeedback,
            onCheckedChange = { viewModel.updateBoolean("hapticFeedback", it) },
            icon = Icons.Rounded.VolumeUp,
        )
    }
}
