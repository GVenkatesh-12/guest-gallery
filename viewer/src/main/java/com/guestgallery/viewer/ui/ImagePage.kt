package com.guestgallery.viewer.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import me.saket.telephoto.zoomable.ZoomSpec
import me.saket.telephoto.zoomable.coil3.ZoomableAsyncImage
import me.saket.telephoto.zoomable.rememberZoomableImageState
import me.saket.telephoto.zoomable.rememberZoomableState

/**
 * Single image page displayed within the [ViewerScreen] pager.
 *
 * Supports pinch-to-zoom via Telephoto's [ZoomableAsyncImage].
 *
 * @param imageUri        Content URI string of the image to display.
 * @param contentDescription Accessibility description for the image.
 * @param maxZoom         Maximum zoom factor (from settings).
 * @param enableZoom      Whether zoom gestures are enabled.
 */
@Composable
fun ImagePage(
    imageUri: String,
    contentDescription: String,
    maxZoom: Float,
    enableZoom: Boolean,
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
    }
}
