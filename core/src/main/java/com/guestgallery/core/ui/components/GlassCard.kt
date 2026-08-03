package com.guestgallery.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.guestgallery.core.theme.Dimens
import com.guestgallery.core.theme.LocalBlurEffects
import com.guestgallery.core.theme.LocalGlassEffect

/**
 * A glassmorphism-styled card with blur effect and translucent background.
 * Creates a premium, modern look for overlaid content.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    blurRadius: Dp = 20.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
    borderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
    content: @Composable BoxScope.() -> Unit,
) {
    val useBlur = LocalBlurEffects.current && LocalGlassEffect.current

    Box(
        modifier =
            modifier
                .clip(MaterialTheme.shapes.large)
                .then(if (useBlur) Modifier.blur(blurRadius) else Modifier)
                .background(backgroundColor)
                .border(1.dp, borderColor, MaterialTheme.shapes.large)
                .padding(Dimens.PaddingCard),
        content = content,
    )
}

/**
 * A simpler glass surface without blur for places where blur
 * causes performance issues. Uses only translucent background.
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
    content: @Composable BoxScope.() -> Unit,
) {
    val useGlass = LocalGlassEffect.current
    val useBlur = LocalBlurEffects.current && useGlass
    val surfaceColor =
        when {
            !useGlass -> MaterialTheme.colorScheme.surface
            useBlur -> backgroundColor
            else -> backgroundColor.copy(alpha = 1f)
        }

    Box(
        modifier =
            modifier
                .clip(MaterialTheme.shapes.large)
                .background(surfaceColor)
                .then(
                    if (useGlass) {
                        Modifier.border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                            shape = MaterialTheme.shapes.large,
                        )
                    } else {
                        Modifier
                    },
                )
                .padding(Dimens.PaddingCard),
        content = content,
    )
}
