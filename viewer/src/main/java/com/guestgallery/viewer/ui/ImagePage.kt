package com.guestgallery.viewer.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import me.saket.telephoto.zoomable.DoubleClickToZoomListener
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
                // Shared images are never written to Coil's disk cache.
                .diskCachePolicy(CachePolicy.DISABLED)
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
                gesturesEnabled = enableZoom,
                onDoubleClick =
                    if (enableDoubleTapZoom) {
                        DoubleClickToZoomListener.cycle()
                    } else {
                        DoubleClickToZoomListener { _, _ -> }
                    },
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            ZoomableAsyncImage(
                model = model,
                contentDescription = contentDescription,
                state = imageState,
                contentScale = ContentScale.Fit,
                gesturesEnabled = false,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
