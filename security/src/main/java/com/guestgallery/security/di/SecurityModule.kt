package com.guestgallery.security.di

import com.guestgallery.security.auth.AuthenticationManager
import com.guestgallery.security.lockdown.KioskModeManager
import com.guestgallery.security.lockdown.ScreenPinningHelper
import com.guestgallery.security.lockdown.SecureWindowManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides security-related singletons.
 *
 * [AuthenticationManager], [SecureWindowManager], [ScreenPinningHelper], and
 * [KioskModeManager] all use `@Inject constructor`, so Hilt can create them
 * automatically. This module exists to make the dependency graph explicit
 * and to allow future customisation (e.g. scoped or qualified bindings).
 */
@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {
    @Provides
    @Singleton
    fun provideAuthenticationManager(): AuthenticationManager = AuthenticationManager()

    @Provides
    @Singleton
    fun provideSecureWindowManager(): SecureWindowManager = SecureWindowManager()

    @Provides
    @Singleton
    fun provideScreenPinningHelper(): ScreenPinningHelper = ScreenPinningHelper()

    @Provides
    @Singleton
    fun provideKioskModeManager(secureWindowManager: SecureWindowManager): KioskModeManager {
        return KioskModeManager(secureWindowManager)
    }
}
