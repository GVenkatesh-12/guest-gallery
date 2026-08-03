package com.guestgallery.core.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import kotlin.math.roundToInt

private const val MIN_ANIMATION_SPEED = 0.25f
private const val MAX_ANIMATION_SPEED = 4f

/** User-controlled animation speed multiplier. Values above 1 are faster. */
val LocalAnimationSpeed = staticCompositionLocalOf { 1f }

/** Whether non-essential motion should be reduced or removed. */
val LocalReducedMotion = staticCompositionLocalOf { false }

/** Whether haptic feedback is enabled for interactive controls. */
val LocalHapticFeedbackEnabled = staticCompositionLocalOf { true }

/** Returns a duration adjusted by the current motion preferences. */
@Composable
fun motionDuration(baseDurationMs: Int): Int {
    if (LocalReducedMotion.current) return 1

    return (baseDurationMs / LocalAnimationSpeed.current.coerceIn(MIN_ANIMATION_SPEED, MAX_ANIMATION_SPEED))
        .roundToInt()
        .coerceAtLeast(1)
}
