package com.guestgallery.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.guestgallery.core.theme.motionDuration

/** Default animation duration in milliseconds. */
private const val DEFAULT_DURATION = 300

/** Fade in/out animation wrapper. */
@Composable
fun FadeAnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    durationMs: Int = DEFAULT_DURATION,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(animationSpec = tween(motionDuration(durationMs))),
        exit = fadeOut(animationSpec = tween(motionDuration(durationMs))),
    ) {
        content()
    }
}
