package com.guestgallery.settings.ui.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AspectRatio
import androidx.compose.material.icons.rounded.BrightnessAuto
import androidx.compose.material.icons.rounded.CancelPresentation
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material.icons.rounded.Pin
import androidx.compose.material.icons.rounded.PinDrop
import androidx.compose.material.icons.rounded.ScreenLockPortrait
import androidx.compose.material.icons.rounded.ScreenRotation
import androidx.compose.material.icons.rounded.ScreenShare
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.guestgallery.domain.model.AppSettings
import com.guestgallery.settings.ui.components.SettingToggleItem

@Composable
fun SecuritySettingsSection(
    settings: AppSettings,
    onUpdate: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SettingToggleItem(
            title = "Screen Pinning Reminder",
            subtitle = "Remind to pin screen on entering viewer",
            checked = settings.enableScreenPinningReminder,
            onCheckedChange = { onUpdate("enableScreenPinningReminder", it) },
            icon = Icons.Rounded.PinDrop,
        )
        SettingToggleItem(
            title = "Auto Lock on Exit",
            subtitle = "Lock screen when leaving secure view",
            checked = settings.autoLockOnExit,
            onCheckedChange = { onUpdate("autoLockOnExit", it) },
            icon = Icons.Rounded.Lock,
        )
        SettingToggleItem(
            title = "Require Fingerprint to Exit",
            subtitle = "Owner authentication needed to close viewer",
            checked = settings.requireFingerprintToExit,
            onCheckedChange = { onUpdate("requireFingerprintToExit", it) },
            icon = Icons.Rounded.Fingerprint,
        )
        SettingToggleItem(
            title = "Require PIN to Exit",
            subtitle = "Fall back to device credentials",
            checked = settings.requirePinToExit,
            onCheckedChange = { onUpdate("requirePinToExit", it) },
            icon = Icons.Rounded.Pin,
        )
        SettingToggleItem(
            title = "Disable Screenshots",
            subtitle = "Block capturing screenshots",
            checked = settings.disableScreenshots,
            onCheckedChange = { onUpdate("disableScreenshots", it) },
            icon = Icons.Rounded.CancelPresentation,
        )
        SettingToggleItem(
            title = "Disable Screen Recording",
            subtitle = "Prevent screen video recording",
            checked = settings.disableScreenRecording,
            onCheckedChange = { onUpdate("disableScreenRecording", it) },
            icon = Icons.Rounded.ScreenShare,
        )
        SettingToggleItem(
            title = "Hide Notifications",
            subtitle = "Suppress incoming alerts in viewer",
            checked = settings.hideNotifications,
            onCheckedChange = { onUpdate("hideNotifications", it) },
            icon = Icons.Rounded.NotificationsOff,
        )
        SettingToggleItem(
            title = "Hide Status Bar",
            subtitle = "Enter full immersive fullscreen",
            checked = settings.hideStatusBar,
            onCheckedChange = { onUpdate("hideStatusBar", it) },
            icon = Icons.Rounded.VisibilityOff,
        )
        SettingToggleItem(
            title = "Hide Navigation Bar",
            subtitle = "Hide navigation buttons/gestures",
            checked = settings.hideNavigationBar,
            onCheckedChange = { onUpdate("hideNavigationBar", it) },
            icon = Icons.Rounded.AspectRatio,
        )
        SettingToggleItem(
            title = "Prevent Brightness Change",
            subtitle = "Lock system screen brightness",
            checked = settings.preventBrightnessChange,
            onCheckedChange = { onUpdate("preventBrightnessChange", it) },
            icon = Icons.Rounded.BrightnessAuto,
        )
        SettingToggleItem(
            title = "Auto Close after Last Image",
            subtitle = "Exits app after swiping past last photo",
            checked = settings.autoCloseAfterLastImage,
            onCheckedChange = { onUpdate("autoCloseAfterLastImage", it) },
            icon = Icons.Rounded.ScreenLockPortrait,
        )
        SettingToggleItem(
            title = "Lock Orientation",
            subtitle = "Prevent screen rotation in viewer",
            checked = settings.lockOrientation,
            onCheckedChange = { onUpdate("lockOrientation", it) },
            icon = Icons.Rounded.ScreenRotation,
        )
        SettingToggleItem(
            title = "Hide Recent Apps Preview",
            subtitle = "Blank screen in overview screen",
            checked = settings.hideRecentAppsPreview,
            onCheckedChange = { onUpdate("hideRecentAppsPreview", it) },
            icon = Icons.Rounded.Security,
        )
    }
}
