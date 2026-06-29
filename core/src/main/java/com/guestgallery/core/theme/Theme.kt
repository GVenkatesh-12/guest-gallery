package com.guestgallery.core.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

// ── Composition Locals for extended theme data ───────────────────────────────
val LocalOledMode = staticCompositionLocalOf { false }
val LocalDynamicColor = staticCompositionLocalOf { true }

// ── Static Color Schemes ─────────────────────────────────────────────────────
private val LightColorScheme =
    lightColorScheme(
        primary = PrimaryLight,
        onPrimary = OnPrimaryLight,
        primaryContainer = PrimaryContainerLight,
        onPrimaryContainer = OnPrimaryContainerLight,
        secondary = SecondaryLight,
        onSecondary = OnSecondaryLight,
        secondaryContainer = SecondaryContainerLight,
        onSecondaryContainer = OnSecondaryContainerLight,
        tertiary = TertiaryLight,
        onTertiary = OnTertiaryLight,
        tertiaryContainer = TertiaryContainerLight,
        onTertiaryContainer = OnTertiaryContainerLight,
        error = ErrorLight,
        onError = OnErrorLight,
        errorContainer = ErrorContainerLight,
        onErrorContainer = OnErrorContainerLight,
        background = BackgroundLight,
        onBackground = OnBackgroundLight,
        surface = SurfaceLight,
        onSurface = OnSurfaceLight,
        surfaceVariant = SurfaceVariantLight,
        onSurfaceVariant = OnSurfaceVariantLight,
        outline = OutlineLight,
        outlineVariant = OutlineVariantLight,
        inverseSurface = InverseSurfaceLight,
        inverseOnSurface = InverseOnSurfaceLight,
        inversePrimary = InversePrimaryLight,
        scrim = Scrim,
    )

private val DarkColorScheme =
    darkColorScheme(
        primary = PrimaryDark,
        onPrimary = OnPrimaryDark,
        primaryContainer = PrimaryContainerDark,
        onPrimaryContainer = OnPrimaryContainerDark,
        secondary = SecondaryDark,
        onSecondary = OnSecondaryDark,
        secondaryContainer = SecondaryContainerDark,
        onSecondaryContainer = OnSecondaryContainerDark,
        tertiary = TertiaryDark,
        onTertiary = OnTertiaryDark,
        tertiaryContainer = TertiaryContainerDark,
        onTertiaryContainer = OnTertiaryContainerDark,
        error = ErrorDark,
        onError = OnErrorDark,
        errorContainer = ErrorContainerDark,
        onErrorContainer = OnErrorContainerDark,
        background = BackgroundDark,
        onBackground = OnBackgroundDark,
        surface = SurfaceDark,
        onSurface = OnSurfaceDark,
        surfaceVariant = SurfaceVariantDark,
        onSurfaceVariant = OnSurfaceVariantDark,
        outline = OutlineDark,
        outlineVariant = OutlineVariantDark,
        inverseSurface = InverseSurfaceDark,
        inverseOnSurface = InverseOnSurfaceDark,
        inversePrimary = InversePrimaryDark,
        scrim = Scrim,
    )

private val OledDarkColorScheme =
    DarkColorScheme.copy(
        background = OledBlack,
        surface = OledBlack,
        surfaceVariant = OledSurfaceVariant,
    )

@Composable
fun GuestGalleryTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = true,
    oledMode: Boolean = false,
    content: @Composable () -> Unit,
) {
    val isDark =
        when (themeMode) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
            ThemeMode.SYSTEM -> isSystemInDarkTheme()
        }

    val context = LocalContext.current

    val colorScheme =
        when {
            // Dynamic colors on Android 12+
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val dynamicScheme =
                    if (isDark) {
                        dynamicDarkColorScheme(context)
                    } else {
                        dynamicLightColorScheme(context)
                    }
                if (isDark && oledMode) {
                    dynamicScheme.copy(
                        background = OledBlack,
                        surface = OledBlack,
                        surfaceVariant = OledSurfaceVariant,
                    )
                } else {
                    dynamicScheme
                }
            }
            // Static OLED dark
            isDark && oledMode -> OledDarkColorScheme
            // Static dark
            isDark -> DarkColorScheme
            // Static light
            else -> LightColorScheme
        }

    CompositionLocalProvider(
        LocalOledMode provides oledMode,
        LocalDynamicColor provides dynamicColor,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content,
        )
    }
}
