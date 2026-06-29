package com.guestgallery.viewer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guestgallery.domain.model.AppSettings
import com.guestgallery.domain.repository.SessionRepository
import com.guestgallery.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Presentation state for the image viewer screen.
 *
 * @param imageUris         Ordered list of image URI strings.
 * @param currentIndex      Index of the currently visible page.
 * @param totalCount        Total number of images in the session.
 * @param settings          Current app settings snapshot.
 * @param showUi            Whether overlay UI (counter, metadata) is visible.
 * @param isSlideshowActive Whether auto-advance slideshow is running.
 */
data class ViewerUiState(
    val imageUris: List<String> = emptyList(),
    val currentIndex: Int = 0,
    val totalCount: Int = 0,
    val settings: AppSettings? = null,
    val showUi: Boolean = true,
    val isSlideshowActive: Boolean = false,
    val exitRequested: Boolean = false,
)

/**
 * ViewModel for the image viewer screen.
 *
 * Combines the active [ViewingSession] and [AppSettings] into a unified
 * [ViewerUiState]. Handles UI visibility toggling, slideshow auto-advance,
 * and page position tracking.
 */
@HiltViewModel
class ViewerViewModel
    @Inject
    constructor(
        private val sessionRepository: SessionRepository,
        private val settingsRepository: SettingsRepository,
    ) : ViewModel() {
        /** Internal mutable state that is NOT derived from repositories. */
        private val _localState = MutableStateFlow(LocalState())

        private var slideshowController: SlideshowController? = null

        /** Exposed UI state combining repository data with local view state. */
        val uiState: StateFlow<ViewerUiState> =
            combine(
                sessionRepository.observeActiveSession(),
                settingsRepository.observeSettings(),
                _localState,
            ) { session, settings, local ->
                ViewerUiState(
                    imageUris = session?.imageUris.orEmpty(),
                    currentIndex = local.currentIndex,
                    totalCount = session?.imageUris?.size ?: 0,
                    settings = settings,
                    showUi = local.showUi,
                    isSlideshowActive = local.isSlideshowActive,
                    exitRequested = local.exitRequested,
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ViewerUiState(),
            )

        // ── Public actions ───────────────────────────────────────────────────────

        /** Toggle overlay UI visibility (counter, metadata bar). */
        fun toggleUiVisibility() {
            _localState.update { it.copy(showUi = !it.showUi) }
        }

        /** Update the current page position (e.g. after a user swipe). */
        fun setCurrentPage(index: Int) {
            _localState.update { it.copy(currentIndex = index) }
            slideshowController?.updateCurrentIndex(index)
        }

        /** Start the slideshow auto-advance. */
        fun startSlideshow() {
            val state = uiState.value
            val settings = state.settings ?: return
            if (!settings.enableSlideshow || state.totalCount <= 1) return

            slideshowController?.stop()
            slideshowController =
                SlideshowController(
                    scope = viewModelScope,
                    delaySeconds = settings.slideshowSpeedSeconds,
                    loop = settings.loopSlideshow,
                    pageCount = state.totalCount,
                    onAdvance = { nextIndex ->
                        _localState.update {
                            it.copy(currentIndex = nextIndex, isSlideshowActive = true)
                        }
                    },
                    onFinished = {
                        _localState.update {
                            it.copy(
                                isSlideshowActive = false,
                                exitRequested = settings.autoCloseAfterLastImage,
                            )
                        }
                    },
                ).also { controller ->
                    controller.start(startIndex = state.currentIndex)
                }

            _localState.update { it.copy(isSlideshowActive = true, showUi = false) }
        }

        /** Stop the slideshow auto-advance. */
        fun stopSlideshow() {
            slideshowController?.stop()
            slideshowController = null
            _localState.update { it.copy(isSlideshowActive = false) }
        }

        /** Called after the Activity has handled a viewer-driven exit request. */
        fun onExitRequestConsumed() {
            _localState.update { it.copy(exitRequested = false) }
        }

        override fun onCleared() {
            super.onCleared()
            slideshowController?.stop()
        }

        // ── Internal ─────────────────────────────────────────────────────────────

        /** Local view state that doesn't come from repository flows. */
        private data class LocalState(
            val currentIndex: Int = 0,
            val showUi: Boolean = true,
            val isSlideshowActive: Boolean = false,
            val exitRequested: Boolean = false,
        )
    }
