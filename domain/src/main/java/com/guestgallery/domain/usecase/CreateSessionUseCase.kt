package com.guestgallery.domain.usecase

import com.guestgallery.domain.model.ViewingSession
import com.guestgallery.domain.repository.SessionRepository

/**
 * Creates a new temporary viewing session from a list of image URI strings.
 * Validates that at least one URI is provided.
 */
class CreateSessionUseCase(
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke(imageUris: List<String>): Result<ViewingSession> {
        if (imageUris.isEmpty()) {
            return Result.failure(IllegalArgumentException("No images provided"))
        }

        return try {
            val session = sessionRepository.createSession(imageUris)
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
