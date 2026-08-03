package com.guestgallery.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

private const val MAX_CORNER_RADIUS = 28
private const val EXTRA_SMALL_RADIUS_DIVISOR = 4
private const val SMALL_RADIUS_DIVISOR = 2
private const val MEDIUM_RADIUS_RATIO = 0.75f

fun appShapes(
    cornerRadius: Int = 16,
    roundedButtons: Boolean = true,
): Shapes {
    val radius = if (roundedButtons) cornerRadius.coerceIn(0, MAX_CORNER_RADIUS).dp else 0.dp

    return Shapes(
        extraSmall = RoundedCornerShape(radius / EXTRA_SMALL_RADIUS_DIVISOR),
        small = RoundedCornerShape(radius / SMALL_RADIUS_DIVISOR),
        medium = RoundedCornerShape(radius * MEDIUM_RADIUS_RATIO),
        large = RoundedCornerShape(radius),
        extraLarge = RoundedCornerShape(if (roundedButtons) MAX_CORNER_RADIUS.dp else 0.dp),
    )
}

val AppShapes =
    Shapes(
        extraSmall = RoundedCornerShape(4.dp),
        small = RoundedCornerShape(8.dp),
        medium = RoundedCornerShape(12.dp),
        large = RoundedCornerShape(16.dp),
        extraLarge = RoundedCornerShape(28.dp),
    )
