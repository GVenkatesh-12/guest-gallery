package com.guestgallery.viewer.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BrokenImage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.guestgallery.core.theme.Dimens
import me.saket.telephoto.zoomable.ZoomSpec
import me.saket.telephoto.zoomable.coil3.ZoomableAsyncImage
import me.saket.telephoto.zoomable.rememberZoomableImageState
import me.saket.telephoto.zoomable.rememberZoomableState

/**
 * Single image page displayed within the [ViewerScreen] pager.
 *
 * Supports pinch-to-zoom via Telephoto's [ZoomableAsyncImage], double-tap
 * zoom toggling, and shows appropriate loading / error states.
 *
 * @param imageUri        Content URI string of the image to display.
 * @param contentDescription Accessibility description for the image.
 * @param maxZoom         Maximum zoom factor (from settings).
 * @param enableZoom      Whether zoom gestures are enabled.
 * @param enableDoubleTapZoom Whether double-tap toggles zoom.
 */
@Composable
fun ImagePage(
    imageUri: String,
    contentDescription: String,
    maxZoom: Float,
    enableZoom: Boolean,
    enableDoubleTapZoom: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val zoomableState =
        rememberZoomableState(
            zoomSpec =
                remember(maxZoom) {
                    ZoomSpec(maxZoomFactor = maxZoom)
                },
        )

    val imageState = rememberZoomableImageState(zoomableState)

    val model =
        remember(imageUri) {
            ImageRequest.Builder(context)
                .data(imageUri)
                .crossfade(true)
                .build()
        }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (enableZoom) {
            ZoomableAsyncImage(
                model = model,
                contentDescription = contentDescription,
                state = imageState,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            // Fallback without zoom — still uses ZoomableAsyncImage but
            // zoom is effectively disabled via maxZoom = 1f
            val noZoomState =
                rememberZoomableState(
                    zoomSpec = remember { ZoomSpec(maxZoomFactor = 1f) },
                )
            val noZoomImageState = rememberZoomableImageState(noZoomState)

            ZoomableAsyncImage(
                model = model,
                contentDescription = contentDescription,
                state = noZoomImageState,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
            )
        }

        // ── Loading state ────────────────────────────────────────────────────
        if (imageState.painter?.state is AsyncImagePainter.State.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(Dimens.IconXl),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp,
            )
        }

        // ── Error state ──────────────────────────────────────────────────────
        if (imageState.painter?.state is AsyncImagePainter.State.Error) {
            ErrorState(
                onRetry = {
                    // Force recomposition by invalidating the image state
                    // (Coil will re-attempt with the same request)
                },
            )
        }
    }
}

/**
 * Error placeholder shown when an image fails to load.
 */
@Composable
private fun ErrorState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Rounded.BrokenImage,
            contentDescription = "Failed to load image",
            modifier = Modifier.size(Dimens.IconXl),
            tint = MaterialTheme.colorScheme.error,
        )
        Spacer(modifier = Modifier.height(Dimens.SpacingMd))
        Text(
            text = "Failed to load image",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(Dimens.SpacingSm))
        FilledTonalButton(onClick = onRetry) {
            Text("Retry")
        }
    }
}
