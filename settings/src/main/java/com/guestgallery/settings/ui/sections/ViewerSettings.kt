package com.guestgallery.settings.ui.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Animation
import androidx.compose.material.icons.rounded.AspectRatio
import androidx.compose.material.icons.rounded.FormatListNumbered
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Loop
import androidx.compose.material.icons.rounded.PhotoSizeSelectActual
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.RotateRight
import androidx.compose.material.icons.rounded.ScreenLockPortrait
import androidx.compose.material.icons.rounded.Slideshow
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.guestgallery.domain.model.AppSettings
import com.guestgallery.domain.model.TransitionStyle
import com.guestgallery.domain.model.ViewerBackground
import com.guestgallery.settings.ui.SettingsViewModel
import com.guestgallery.settings.ui.components.SettingDropdownItem
import com.guestgallery.settings.ui.components.SettingSliderItem
import com.guestgallery.settings.ui.components.SettingToggleItem

@Composable
fun ViewerSettingsSection(
    settings: AppSettings,
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SettingToggleItem(
            title = "Show Image Counter",
            subtitle = "Display index over photos (e.g. 3 / 10)",
            checked = settings.showImageCounter,
            onCheckedChange = { viewModel.updateBoolean("showImageCounter", it) },
            icon = Icons.Rounded.FormatListNumbered,
        )
        SettingToggleItem(
            title = "Show File Name",
            subtitle = "Display photo file names at bottom",
            checked = settings.showFileName,
            onCheckedChange = { viewModel.updateBoolean("showFileName", it) },
            icon = Icons.Rounded.Info,
        )
        SettingToggleItem(
            title = "Show Resolution",
            subtitle = "Display image pixels resolution",
            checked = settings.showResolution,
            onCheckedChange = { viewModel.updateBoolean("showResolution", it) },
            icon = Icons.Rounded.AspectRatio,
        )
        SettingToggleItem(
            title = "Show File Size",
            subtitle = "Display file size in KB/MB",
            checked = settings.showFileSize,
            onCheckedChange = { viewModel.updateBoolean("showFileSize", it) },
            icon = Icons.Rounded.PhotoSizeSelectActual,
        )
        SettingToggleItem(
            title = "Enable Zoom",
            subtitle = "Allow pinch gesture zooming",
            checked = settings.enableZoom,
            onCheckedChange = { viewModel.updateBoolean("enableZoom", it) },
            icon = Icons.Rounded.ZoomIn,
        )
        SettingToggleItem(
            title = "Enable Rotation",
            subtitle = "Allow rotating images with gestures",
            checked = settings.enableRotation,
            onCheckedChange = { viewModel.updateBoolean("enableRotation", it) },
            icon = Icons.Rounded.RotateRight,
        )
        SettingToggleItem(
            title = "Enable Slideshow",
            subtitle = "Allow slideshow playback controls",
            checked = settings.enableSlideshow,
            onCheckedChange = { viewModel.updateBoolean("enableSlideshow", it) },
            icon = Icons.Rounded.Slideshow,
        )
        if (settings.enableSlideshow) {
            SettingToggleItem(
                title = "Loop Slideshow",
                subtitle = "Restart slideshow after last photo",
                checked = settings.loopSlideshow,
                onCheckedChange = { viewModel.updateBoolean("loopSlideshow", it) },
                icon = Icons.Rounded.Loop,
            )
            SettingSliderItem(
                title = "Slideshow Speed",
                subtitle = "Seconds per image",
                value = settings.slideshowSpeedSeconds.toFloat(),
                onValueChange = { viewModel.updateInt("slideshowSpeedSeconds", it.toInt()) },
                valueRange = 1f..15f,
                steps = 14,
                valueLabel = "${settings.slideshowSpeedSeconds}s",
                icon = Icons.Rounded.Speed,
            )
        }
        SettingSliderItem(
            title = "Maximum Zoom",
            subtitle = "Max scale limit for images",
            value = settings.maximumZoom,
            onValueChange = { viewModel.updateFloat("maximumZoom", it) },
            valueRange = 1.5f..10f,
            valueLabel = "%.1fx".format(settings.maximumZoom),
            icon = Icons.Rounded.ZoomIn,
        )
        SettingToggleItem(
            title = "Keep Screen Awake",
            subtitle = "Prevent display timeout in viewer",
            checked = settings.keepScreenAwake,
            onCheckedChange = { viewModel.updateBoolean("keepScreenAwake", it) },
            icon = Icons.Rounded.ScreenLockPortrait,
        )

        SettingDropdownItem(
            title = "Viewer Background",
            selectedValue = settings.viewerBackground.name,
            options = ViewerBackground.values().map { it.name },
            onOptionSelected = { viewModel.updateString("viewerBackground", it) },
            icon = Icons.Rounded.Wallpaper,
        )

        SettingDropdownItem(
            title = "Transition Style",
            selectedValue = settings.transitionStyle.name,
            options = TransitionStyle.values().map { it.name },
            onOptionSelected = { viewModel.updateString("transitionStyle", it) },
            icon = Icons.Rounded.Animation,
        )
    }
}
