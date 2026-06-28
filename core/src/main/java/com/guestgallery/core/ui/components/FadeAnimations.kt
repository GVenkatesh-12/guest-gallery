package com.guestgallery.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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
        enter = fadeIn(animationSpec = tween(durationMs)),
        exit = fadeOut(animationSpec = tween(durationMs)),
    ) {
        content()
    }
}

/** Scale + fade animation for dialogs and overlays. */
@Composable
fun ScaleFadeAnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    durationMs: Int = DEFAULT_DURATION,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = scaleIn(
            initialScale = 0.85f,
            animationSpec = tween(durationMs),
        ) + fadeIn(animationSpec = tween(durationMs)),
        exit = scaleOut(
            targetScale = 0.85f,
            animationSpec = tween(durationMs),
        ) + fadeOut(animationSpec = tween(durationMs)),
    ) {
        content()
    }
}

/** Slide up + fade for bottom sheets and bottom bars. */
@Composable
fun SlideUpAnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    durationMs: Int = DEFAULT_DURATION,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = slideInVertically(
            initialOffsetY = { it / 3 },
            animationSpec = tween(durationMs),
        ) + fadeIn(animationSpec = tween(durationMs)),
        exit = slideOutVertically(
            targetOffsetY = { it / 3 },
            animationSpec = tween(durationMs),
        ) + fadeOut(animationSpec = tween(durationMs)),
    ) {
        content()
    }
}
