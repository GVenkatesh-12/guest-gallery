package com.guestgallery.security.ui

import com.guestgallery.security.auth.AuthenticationManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SecurityEntryPoint {
    fun authenticationManager(): AuthenticationManager
}
