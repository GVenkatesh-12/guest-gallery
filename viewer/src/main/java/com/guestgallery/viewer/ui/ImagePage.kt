package com.guestgallery.viewer.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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

private const val MAX_ZOOM_FACTOR = 5f

/** Renders one shared image without writing it to disk or the memory cache. */
@Composable
fun ImagePage(
    imageUri: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val zoomableState = rememberZoomableState(ZoomSpec(maxZoomFactor = MAX_ZOOM_FACTOR))
    val imageState = rememberZoomableImageState(zoomableState)
    val model =
        remember(imageUri) {
            ImageRequest.Builder(context)
                .data(imageUri)
                .crossfade(true)
                .diskCachePolicy(CachePolicy.DISABLED)
                .memoryCachePolicy(CachePolicy.DISABLED)
                .build()
        }

    ZoomableAsyncImage(
        model = model,
        contentDescription = contentDescription,
        state = imageState,
        contentScale = ContentScale.Fit,
        onDoubleClick = DoubleClickToZoomListener.cycle(),
        modifier = modifier.fillMaxSize(),
    )
}
