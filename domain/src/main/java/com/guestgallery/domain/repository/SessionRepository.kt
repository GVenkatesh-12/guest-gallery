package com.guestgallery.domain.repository

import com.guestgallery.domain.model.ViewingSession
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing temporary viewing sessions.
 * Sessions are ephemeral and should not persist across app restarts.
 */
interface SessionRepository {
    /** Observe the currently active session. Emits null when no session is active. */
    fun observeActiveSession(): Flow<ViewingSession?>

    /** Create a new viewing session from shared image URIs. */
    suspend fun createSession(imageUris: List<String>): ViewingSession

    /** Destroy the active session and release all resources. */
    suspend fun destroySession()

    /** Check if there is an active session. */
    suspend fun hasActiveSession(): Boolean
}
