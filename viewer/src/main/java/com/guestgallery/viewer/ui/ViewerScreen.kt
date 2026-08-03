package com.guestgallery.viewer.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.guestgallery.core.theme.Dimens
import com.guestgallery.core.ui.components.FadeAnimatedVisibility

/** Fullscreen, memory-only viewer for the images in the active shared session. */
@Composable
fun ViewerScreen(
    onExitClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ViewerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pageCount = uiState.imageUris.size
    val pagerState =
        rememberPagerState(
            initialPage = uiState.currentIndex.coerceIn(0, (pageCount - 1).coerceAtLeast(0)),
            pageCount = { pageCount },
        )

    BackHandler(onBack = onExitClick)

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect(viewModel::setCurrentPage)
    }

    LaunchedEffect(uiState.currentIndex) {
        if (pagerState.currentPage != uiState.currentIndex) {
            pagerState.animateScrollToPage(uiState.currentIndex)
        }
    }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = viewModel::toggleUiVisibility,
                ),
    ) {
        if (pageCount > 0) {
            HorizontalPager(
                state = pagerState,
                beyondViewportPageCount = 1,
                modifier = Modifier.fillMaxSize(),
                key = { uiState.imageUris[it] },
            ) { pageIndex ->
                ImagePage(
                    imageUri = uiState.imageUris[pageIndex],
                    contentDescription = "Image ${pageIndex + 1} of $pageCount",
                )
            }
        }

        FadeAnimatedVisibility(
            visible = uiState.showUi,
            modifier = Modifier.align(Alignment.TopCenter),
        ) {
            ViewerOverlay(
                currentIndex = uiState.currentIndex,
                totalCount = pageCount,
                onExitClick = onExitClick,
            )
        }
    }
}

@Composable
private fun ViewerOverlay(
    currentIndex: Int,
    totalCount: Int,
    onExitClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.55f))
                .statusBarsPadding()
                .padding(horizontal = Dimens.SpacingSm, vertical = Dimens.SpacingXs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onExitClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Exit viewer",
                tint = Color.White,
            )
        }
        Text(
            text = "${currentIndex + 1} / $totalCount",
            color = Color.White,
            fontWeight = FontWeight.Medium,
        )
    }
}
