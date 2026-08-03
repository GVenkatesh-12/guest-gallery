package com.guestgallery.settings.ui.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BlurOn
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.FormatSize
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.RoundedCorner
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.ViewCompact
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.guestgallery.domain.model.AppSettings
import com.guestgallery.settings.ui.SettingsViewModel
import com.guestgallery.settings.ui.components.SettingDropdownItem
import com.guestgallery.settings.ui.components.SettingSliderItem
import com.guestgallery.settings.ui.components.SettingToggleItem

@Composable
fun AppearanceSettingsSection(
    settings: AppSettings,
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SettingDropdownItem(
            title = "Theme Mode",
            selectedValue = settings.themeMode,
            options = listOf("light", "dark", "system"),
            onOptionSelected = { viewModel.updateString("themeMode", it) },
            icon = Icons.Rounded.DarkMode,
        )

        SettingToggleItem(
            title = "Dynamic Colors",
            subtitle = "Use Android 12+ wallpaper dynamic theme",
            checked = settings.dynamicColors,
            onCheckedChange = { viewModel.updateBoolean("dynamicColors", it) },
            icon = Icons.Rounded.Palette,
        )

        SettingToggleItem(
            title = "OLED Black Mode",
            subtitle = "True pitch black background for dark theme",
            checked = settings.oledBlackMode,
            onCheckedChange = { viewModel.updateBoolean("oledBlackMode", it) },
            icon = Icons.Rounded.ColorLens,
        )

        SettingSliderItem(
            title = "Corner Radius",
            subtitle = "UI cards rounded corners density",
            value = settings.cornerRadius.toFloat(),
            onValueChange = { viewModel.updateInt("cornerRadius", it.toInt()) },
            valueRange = 0f..28f,
            steps = 7,
            valueLabel = "${settings.cornerRadius}dp",
            icon = Icons.Rounded.RoundedCorner,
        )

        SettingSliderItem(
            title = "Animation Speed multiplier",
            subtitle = "Speed up or slow down UI motions",
            value = settings.animationSpeed,
            onValueChange = { viewModel.updateFloat("animationSpeed", it) },
            valueRange = 0.5f..2.0f,
            valueLabel = "%.1fx".format(settings.animationSpeed),
            icon = Icons.Rounded.Speed,
        )

        SettingDropdownItem(
            title = "Accent Color",
            selectedValue = accentLabel(settings.accentColor),
            options = ACCENT_PRESETS.keys.toList(),
            onOptionSelected = { viewModel.updateLong("accentColor", ACCENT_PRESETS.getValue(it)) },
            icon = Icons.Rounded.ColorLens,
        )

        SettingSliderItem(
            title = "Font Size",
            subtitle = "Scale app text independently of system settings",
            value = settings.fontSize,
            onValueChange = { viewModel.updateFloat("fontSize", it) },
            valueRange = 0.8f..1.5f,
            steps = 6,
            valueLabel = "%.1fx".format(settings.fontSize),
            icon = Icons.Rounded.FormatSize,
        )

        SettingToggleItem(
            title = "Minimal Viewer UI",
            subtitle = "Hide counters, slideshow controls, and metadata overlays",
            checked = settings.minimalUi,
            onCheckedChange = { viewModel.updateBoolean("minimalUi", it) },
            icon = Icons.Rounded.ViewCompact,
        )

        SettingToggleItem(
            title = "Glassmorphism Card Effect",
            subtitle = "Translucent blurred backdrop dialogs",
            checked = settings.glassEffect,
            onCheckedChange = { viewModel.updateBoolean("glassEffect", it) },
            icon = Icons.Rounded.BlurOn,
        )

        SettingToggleItem(
            title = "Blur Effects",
            subtitle = "Allow blurred surfaces where supported",
            checked = settings.blurEffects,
            onCheckedChange = { viewModel.updateBoolean("blurEffects", it) },
            icon = Icons.Rounded.BlurOn,
        )

        SettingToggleItem(
            title = "Rounded Buttons",
            subtitle = "Use rounded shapes throughout the app",
            checked = settings.roundedButtons,
            onCheckedChange = { viewModel.updateBoolean("roundedButtons", it) },
            icon = Icons.Rounded.RoundedCorner,
        )
    }
}

private val ACCENT_PRESETS =
    linkedMapOf(
        "Default" to 0L,
        "Teal" to 0xFF006B5EL,
        "Blue" to 0xFF1565C0L,
        "Purple" to 0xFF6A1B9AL,
        "Orange" to 0xFFE65100L,
    )

private fun accentLabel(value: Long): String =
    ACCENT_PRESETS.entries
        .firstOrNull { it.value == value }
        ?.key
        ?: "Default"
