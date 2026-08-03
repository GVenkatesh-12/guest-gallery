package com.guestgallery.viewer.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.guestgallery.core.theme.Dimens
import com.guestgallery.core.ui.components.FadeAnimatedVisibility
import com.guestgallery.core.ui.components.SlideUpAnimatedVisibility
import com.guestgallery.domain.model.TransitionStyle
import com.guestgallery.domain.model.ViewerBackground
import kotlin.math.absoluteValue

private const val MILLIS_PER_MINUTE = 60_000L
private const val DEPTH_ALPHA_FACTOR = 0.5f
private const val DEPTH_SCALE_FACTOR = 0.1f
private const val BYTES_PER_KILOBYTE = 1_024L
private const val BYTES_PER_MEGABYTE = BYTES_PER_KILOBYTE * BYTES_PER_KILOBYTE
private const val KILOBYTES_AS_FLOAT = 1_024f
private const val MEGABYTES_AS_FLOAT = KILOBYTES_AS_FLOAT * KILOBYTES_AS_FLOAT

/**
 * Main image viewer screen.
 *
 * Displays a horizontally-pageable gallery of images with overlay UI that
 * fades in/out on tap. Supports slideshow mode, metadata display, and
 * configurable viewer background.
 *
 * @param onExitClick     Callback when the back / exit button is tapped.
 * @param viewModel       Injected Hilt ViewModel.
 */
@Composable
fun ViewerScreen(
    onExitClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ViewerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val settings = uiState.settings

    val backgroundColor =
        when (settings?.viewerBackground) {
            ViewerBackground.BLACK -> Color.Black
            ViewerBackground.DARK_GRAY -> Color(0xFF1A1A1A)
            ViewerBackground.GRAY -> Color(0xFF2E2E2E)
            ViewerBackground.SYSTEM -> MaterialTheme.colorScheme.background
            null -> Color.Black
        }

    val pagerState =
        rememberPagerState(
            initialPage = uiState.currentIndex,
            pageCount = { uiState.totalCount },
        )

    // Sync pager state → ViewModel when user swipes
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            viewModel.setCurrentPage(page)
        }
    }

    // Sync ViewModel → pager when slideshow advances
    LaunchedEffect(uiState.currentIndex) {
        if (pagerState.currentPage != uiState.currentIndex) {
            pagerState.animateScrollToPage(uiState.currentIndex)
        }
    }

    LaunchedEffect(uiState.exitRequested) {
        if (uiState.exitRequested) {
            viewModel.onExitRequestConsumed()
            onExitClick()
        }
    }

    LaunchedEffect(settings?.autoCloseAfterTimeoutMinutes, uiState.totalCount) {
        val timeoutMinutes = settings?.autoCloseAfterTimeoutMinutes ?: 0
        if (timeoutMinutes > 0 && uiState.totalCount > 0) {
            kotlinx.coroutines.delay(timeoutMinutes * MILLIS_PER_MINUTE)
            viewModel.requestTimedExit()
        }
    }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(backgroundColor)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) {
                    viewModel.toggleUiVisibility()
                },
    ) {
        // ── Image pager ──────────────────────────────────────────────────
        if (uiState.totalCount > 0 && settings != null) {
            HorizontalPager(
                state = pagerState,
                beyondViewportPageCount = settings.preloadCount,
                contentPadding = PaddingValues(horizontal = settings.edgePadding.dp),
                modifier = Modifier.fillMaxSize(),
                key = { uiState.imageUris[it] },
            ) { pageIndex ->
                ImagePage(
                    imageUri = uiState.imageUris[pageIndex],
                    contentDescription = "Image ${pageIndex + 1} of ${uiState.totalCount}",
                    maxZoom = settings.maximumZoom,
                    enableZoom = settings.enableZoom,
                    enableDoubleTapZoom = settings.enableDoubleTapZoom,
                    modifier = Modifier.pageTransition(settings.transitionStyle, pageIndex, pagerState),
                )
            }
        }

        // ── Top overlay: counter + controls ──────────────────────────────
        FadeAnimatedVisibility(
            visible = uiState.showUi,
            modifier = Modifier.align(Alignment.TopCenter),
        ) {
            TopOverlay(
                currentIndex = uiState.currentIndex,
                totalCount = uiState.totalCount,
                showCounter = settings?.showImageCounter == true,
                isSlideshowActive = uiState.isSlideshowActive,
                enableSlideshow = settings?.enableSlideshow == true,
                onExitClick = onExitClick,
                onSlideshowToggle = {
                    if (uiState.isSlideshowActive) {
                        viewModel.stopSlideshow()
                    } else {
                        viewModel.startSlideshow()
                    }
                },
            )
        }

        // ── Bottom overlay: metadata bar ─────────────────────────────────
        val showMetadata =
            settings?.showFileName == true ||
                settings?.showResolution == true ||
                settings?.showFileSize == true

        SlideUpAnimatedVisibility(
            visible = uiState.showUi && showMetadata,
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            MetadataBar(
                imageUri = uiState.imageUris.getOrNull(uiState.currentIndex),
                showFileName = settings?.showFileName == true,
                showResolution = settings?.showResolution == true,
                showFileSize = settings?.showFileSize == true,
            )
        }
    }
}

private fun Modifier.pageTransition(
    transitionStyle: TransitionStyle,
    pageIndex: Int,
    pagerState: androidx.compose.foundation.pager.PagerState,
): Modifier =
    if (transitionStyle == TransitionStyle.NONE) {
        this
    } else {
        graphicsLayer {
            val pageOffset =
                ((pageIndex - pagerState.currentPage) + pagerState.currentPageOffsetFraction)
                    .absoluteValue
                    .coerceIn(0f, 1f)
            when (transitionStyle) {
                TransitionStyle.CROSSFADE -> alpha = 1f - pageOffset
                TransitionStyle.SLIDE -> translationX = pageOffset * size.width.toFloat()
                TransitionStyle.DEPTH -> {
                    alpha = 1f - (pageOffset * DEPTH_ALPHA_FACTOR)
                    scaleX = 1f - (pageOffset * DEPTH_SCALE_FACTOR)
                    scaleY = 1f - (pageOffset * DEPTH_SCALE_FACTOR)
                }
                TransitionStyle.NONE -> Unit
            }
        }
    }

// ═══════════════════════════════════════════════════════════════════════════════
// Private helper composables
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun TopOverlay(
    currentIndex: Int,
    totalCount: Int,
    showCounter: Boolean,
    isSlideshowActive: Boolean,
    enableSlideshow: Boolean,
    onExitClick: () -> Unit,
    onSlideshowToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.4f))
                .statusBarsPadding()
                .padding(horizontal = Dimens.SpacingXs, vertical = Dimens.SpacingXs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onExitClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Exit viewer",
                tint = Color.White,
            )
        }

        if (showCounter && totalCount > 0) {
            Text(
                text = "${currentIndex + 1} / $totalCount",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier =
                    Modifier
                        .weight(1f)
                        .animateContentSize(),
            )
        } else {
            Box(modifier = Modifier.weight(1f))
        }

        if (enableSlideshow) {
            IconButton(onClick = onSlideshowToggle) {
                Icon(
                    imageVector =
                        if (isSlideshowActive) {
                            Icons.Rounded.Pause
                        } else {
                            Icons.Rounded.PlayArrow
                        },
                    contentDescription =
                        if (isSlideshowActive) {
                            "Stop slideshow"
                        } else {
                            "Start slideshow"
                        },
                    tint = Color.White,
                )
            }
        }
    }
}

@Composable
private fun MetadataBar(
    imageUri: String?,
    showFileName: Boolean,
    showResolution: Boolean,
    showFileSize: Boolean,
    modifier: Modifier = Modifier,
) {
    if (imageUri == null) return

    val context = androidx.compose.ui.platform.LocalContext.current
    val metadata by androidx.compose.runtime.produceState<ImageMetadata?>(
        initialValue = null,
        key1 = imageUri,
    ) {
        value =
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                loadImageMetadata(context, imageUri)
            }
    }

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.5f))
                .navigationBarsPadding()
                .padding(horizontal = Dimens.PaddingScreen, vertical = Dimens.SpacingMd),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        if (showFileName) {
            Text(
                text = metadata?.fileName ?: imageUri.substringAfterLast('/'),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f),
                maxLines = 1,
            )
        }
        if (showResolution) {
            Text(
                text = metadata?.resolution?.let { "Resolution: $it" } ?: "Resolution: unavailable",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f),
            )
        }
        if (showFileSize) {
            Text(
                text =
                    metadata?.sizeBytes?.let { "Size: ${formatFileSize(it)}" }
                        ?: "Size: unavailable",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f),
            )
        }
    }
}

private data class ImageMetadata(
    val fileName: String?,
    val resolution: String?,
    val sizeBytes: Long?,
)

private fun loadImageMetadata(
    context: android.content.Context,
    imageUri: String,
): ImageMetadata {
    val uri = android.net.Uri.parse(imageUri)
    var fileName: String? = uri.lastPathSegment?.substringAfterLast('/')
    var sizeBytes: Long? = null

    runCatching {
        context.contentResolver.query(
            uri,
            arrayOf(
                android.provider.OpenableColumns.DISPLAY_NAME,
                android.provider.OpenableColumns.SIZE,
            ),
            null,
            null,
            null,
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
                if (nameIndex >= 0) fileName = cursor.getString(nameIndex)
                if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) sizeBytes = cursor.getLong(sizeIndex)
            }
        }
    }

    val bounds = android.graphics.BitmapFactory.Options().apply { inJustDecodeBounds = true }
    runCatching {
        context.contentResolver.openInputStream(uri)?.use {
            android.graphics.BitmapFactory.decodeStream(it, null, bounds)
        }
    }

    val resolution =
        bounds.outWidth.takeIf { it > 0 }?.let { width ->
            bounds.outHeight.takeIf { it > 0 }?.let { height -> "$width × $height" }
        }

    return ImageMetadata(fileName, resolution, sizeBytes)
}

private fun formatFileSize(bytes: Long): String {
    if (bytes < BYTES_PER_KILOBYTE) return "$bytes B"
    if (bytes < BYTES_PER_MEGABYTE) return "%.1f KB".format(bytes / KILOBYTES_AS_FLOAT)
    return "%.1f MB".format(bytes / MEGABYTES_AS_FLOAT)
}
