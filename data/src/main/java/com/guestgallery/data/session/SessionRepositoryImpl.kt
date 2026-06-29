package com.guestgallery.data.session

import com.guestgallery.domain.model.ViewingSession
import com.guestgallery.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory [SessionRepository] implementation.
 *
 * Sessions are ephemeral by design — they exist only while the app is alive
 * and are never persisted to disk. A [MutableStateFlow] holds the current
 * session, emitting `null` when no session is active.
 */
@Singleton
class SessionRepositoryImpl
    @Inject
    constructor() : SessionRepository {
        private val _activeSession = MutableStateFlow<ViewingSession?>(null)

        override fun observeActiveSession(): Flow<ViewingSession?> = _activeSession.asStateFlow()

        override suspend fun createSession(imageUris: List<String>): ViewingSession {
            val session =
                ViewingSession(
                    id = UUID.randomUUID().toString(),
                    imageUris = imageUris,
                    createdAt = System.currentTimeMillis(),
                    isActive = true,
                )
            _activeSession.value = session
            return session
        }

        override suspend fun destroySession() {
            _activeSession.value = null
        }

        override suspend fun hasActiveSession(): Boolean = _activeSession.value != null
    }
