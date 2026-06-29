package com.guestgallery.viewer.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.rounded.Settings
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.guestgallery.core.theme.Dimens
import com.guestgallery.core.ui.components.FadeAnimatedVisibility
import com.guestgallery.core.ui.components.SlideUpAnimatedVisibility
import com.guestgallery.domain.model.ViewerBackground

/**
 * Main image viewer screen.
 *
 * Displays a horizontally-pageable gallery of images with overlay UI that
 * fades in/out on tap. Supports slideshow mode, metadata display, and
 * configurable viewer background.
 *
 * @param onSettingsClick Callback when the settings icon is tapped.
 * @param onExitClick     Callback when the back / exit button is tapped.
 * @param viewModel       Injected Hilt ViewModel.
 */
@Composable
fun ViewerScreen(
    onSettingsClick: () -> Unit,
    onExitClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ViewerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val backgroundColor =
        when (uiState.settings.viewerBackground) {
            ViewerBackground.BLACK -> Color.Black
            ViewerBackground.DARK_GRAY -> Color(0xFF1A1A1A)
            ViewerBackground.GRAY -> Color(0xFF2E2E2E)
            ViewerBackground.SYSTEM -> MaterialTheme.colorScheme.background
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
        if (uiState.totalCount > 0) {
            HorizontalPager(
                state = pagerState,
                beyondViewportPageCount = uiState.settings.preloadCount,
                modifier = Modifier.fillMaxSize(),
                key = { uiState.imageUris[it] },
            ) { pageIndex ->
                ImagePage(
                    imageUri = uiState.imageUris[pageIndex],
                    contentDescription = "Image ${pageIndex + 1} of ${uiState.totalCount}",
                    maxZoom = uiState.settings.maximumZoom,
                    enableZoom = uiState.settings.enableZoom,
                    enableDoubleTapZoom = uiState.settings.enableDoubleTapZoom,
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
                showCounter = uiState.settings.showImageCounter,
                isSlideshowActive = uiState.isSlideshowActive,
                enableSlideshow = uiState.settings.enableSlideshow,
                onExitClick = onExitClick,
                onSettingsClick = onSettingsClick,
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
            uiState.settings.showFileName ||
                uiState.settings.showResolution ||
                uiState.settings.showFileSize

        SlideUpAnimatedVisibility(
            visible = uiState.showUi && showMetadata,
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            MetadataBar(
                imageUri = uiState.imageUris.getOrNull(uiState.currentIndex),
                showFileName = uiState.settings.showFileName,
                showResolution = uiState.settings.showResolution,
                showFileSize = uiState.settings.showFileSize,
            )
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
    onSettingsClick: () -> Unit,
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

        IconButton(onClick = onSettingsClick) {
            Icon(
                imageVector = Icons.Rounded.Settings,
                contentDescription = "Open settings",
                tint = Color.White,
            )
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
            val fileName = imageUri.substringAfterLast('/')
            Text(
                text = fileName,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f),
                maxLines = 1,
            )
        }
        if (showResolution) {
            Text(
                text = "Resolution: —",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f),
            )
        }
        if (showFileSize) {
            Text(
                text = "Size: —",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f),
            )
        }
    }
}
