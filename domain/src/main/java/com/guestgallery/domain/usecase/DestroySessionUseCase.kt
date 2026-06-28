package com.guestgallery.domain.usecase

import com.guestgallery.domain.repository.SessionRepository

/**
 * Destroys the active viewing session and releases all resources.
 * Safe to call even if no session is active.
 */
class DestroySessionUseCase(
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            sessionRepository.destroySession()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
