package com.guestgallery.viewer.ui

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.ImageLoader
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.allowHardware
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
    contentDescription: String?,
    maxZoom: Float,
    enableZoom: Boolean,
    enableDoubleTapZoom: Boolean,
    enableRotation: Boolean,
    gestureSensitivity: Float,
    memoryOptimization: Boolean,
    neverStoreSharedImages: Boolean,
    imageLoader: ImageLoader,
    hardwareDecode: Boolean,
    softwareDecode: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var rotation by remember(imageUri) { mutableFloatStateOf(0f) }
    val effectiveMaxZoom = (maxZoom * gestureSensitivity).coerceIn(1f, 10f)

    val zoomableState =
        rememberZoomableState(
            zoomSpec =
                remember(effectiveMaxZoom) {
                    ZoomSpec(maxZoomFactor = effectiveMaxZoom)
                },
        )

    val imageState = rememberZoomableImageState(zoomableState)

    val model =
        remember(imageUri, memoryOptimization, neverStoreSharedImages, hardwareDecode, softwareDecode) {
            ImageRequest.Builder(context)
                .data(imageUri)
                .crossfade(true)
                // Shared images are never written to Coil's disk cache.
                .diskCachePolicy(CachePolicy.DISABLED)
                .allowHardware(hardwareDecode && !softwareDecode)
                .memoryCachePolicy(
                    if (memoryOptimization || neverStoreSharedImages) {
                        CachePolicy.DISABLED
                    } else {
                        CachePolicy.ENABLED
                    },
                )
                .build()
        }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .graphicsLayer { rotationZ = rotation }
                .rotationGesture(enabled = enableRotation) { rotation += it },
        contentAlignment = Alignment.Center,
    ) {
        if (enableZoom) {
            ZoomableAsyncImage(
                model = model,
                contentDescription = contentDescription,
                imageLoader = imageLoader,
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
                imageLoader = imageLoader,
                state = imageState,
                contentScale = ContentScale.Fit,
                gesturesEnabled = false,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

private fun Modifier.rotationGesture(
    enabled: Boolean,
    onRotation: (Float) -> Unit,
): Modifier =
    if (!enabled) {
        this
    } else {
        pointerInput(enabled) {
            awaitEachGesture {
                awaitFirstDown(pass = PointerEventPass.Initial)
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    val changes = event.changes.filter { it.pressed }
                    if (changes.isEmpty()) {
                        break
                    }
                    if (changes.size >= 2) {
                        val currentVector = changes[0].position - changes[1].position
                        val previousVector =
                            changes[0].previousPosition - changes[1].previousPosition
                        if (currentVector != Offset.Zero && previousVector != Offset.Zero) {
                            val currentAngle = kotlin.math.atan2(currentVector.y, currentVector.x)
                            val previousAngle = kotlin.math.atan2(previousVector.y, previousVector.x)
                            var delta =
                                (currentAngle - previousAngle) * DEGREES_PER_RADIAN
                            if (delta > HALF_TURN_DEGREES) delta -= FULL_TURN_DEGREES
                            if (delta < -HALF_TURN_DEGREES) delta += FULL_TURN_DEGREES
                            onRotation(delta)
                        }
                    }
                }
            }
        }
    }

private const val DEGREES_PER_RADIAN = 57.29578f
private const val HALF_TURN_DEGREES = 180f
private const val FULL_TURN_DEGREES = 360f
