package com.guestgallery.domain.model

/**
 * Represents a temporary viewing session created from shared images.
 * A session is ephemeral — it exists only while the user is actively viewing.
 */
data class ViewingSession(
    val id: String,
    val imageUris: List<String>,
    val createdAt: Long,
    val isActive: Boolean = true,
)
