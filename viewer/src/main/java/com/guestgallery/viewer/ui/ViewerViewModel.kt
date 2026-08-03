package com.guestgallery.viewer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guestgallery.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class ViewerUiState(
    val imageUris: List<String> = emptyList(),
    val currentIndex: Int = 0,
    val showUi: Boolean = true,
)

/** Holds only the current page and visibility of the viewer controls. */
@HiltViewModel
class ViewerViewModel
    @Inject
    constructor(
        sessionRepository: SessionRepository,
    ) : ViewModel() {
        private val localState = MutableStateFlow(ViewerUiState())

        val uiState: StateFlow<ViewerUiState> =
            combine(sessionRepository.observeActiveSession(), localState) { session, local ->
                val images = session?.imageUris.orEmpty()
                local.copy(
                    imageUris = images,
                    currentIndex = local.currentIndex.coerceIn(0, (images.size - 1).coerceAtLeast(0)),
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ViewerUiState(),
            )

        fun toggleUiVisibility() {
            localState.update { it.copy(showUi = !it.showUi) }
        }

        fun setCurrentPage(index: Int) {
            localState.update { it.copy(currentIndex = index) }
        }
    }
