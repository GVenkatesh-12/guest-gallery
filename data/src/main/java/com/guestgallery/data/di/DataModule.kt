package com.guestgallery.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.guestgallery.data.preferences.SettingsRepositoryImpl
import com.guestgallery.data.session.SessionRepositoryImpl
import com.guestgallery.domain.repository.SessionRepository
import com.guestgallery.domain.repository.SettingsRepository
import com.guestgallery.domain.usecase.CreateSessionUseCase
import com.guestgallery.domain.usecase.DestroySessionUseCase
import com.guestgallery.domain.usecase.GetSettingsUseCase
import com.guestgallery.domain.usecase.UpdateSettingUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Top-level DataStore delegate — creates a single file per process. */
private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "guest_gallery_settings",
)

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    // ── Repository bindings ──────────────────────────────────────────────────

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: SettingsRepositoryImpl,
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindSessionRepository(
        impl: SessionRepositoryImpl,
    ): SessionRepository

    companion object {

        // ── DataStore provider ───────────────────────────────────────────────

        @Provides
        @Singleton
        fun provideDataStore(
            @ApplicationContext context: Context,
        ): DataStore<Preferences> = context.settingsDataStore

        // ── Use case providers ───────────────────────────────────────────────

        @Provides
        fun provideCreateSessionUseCase(
            sessionRepository: SessionRepository,
        ): CreateSessionUseCase = CreateSessionUseCase(sessionRepository)

        @Provides
        fun provideDestroySessionUseCase(
            sessionRepository: SessionRepository,
        ): DestroySessionUseCase = DestroySessionUseCase(sessionRepository)

        @Provides
        fun provideGetSettingsUseCase(
            settingsRepository: SettingsRepository,
        ): GetSettingsUseCase = GetSettingsUseCase(settingsRepository)

        @Provides
        fun provideUpdateSettingUseCase(
            settingsRepository: SettingsRepository,
        ): UpdateSettingUseCase = UpdateSettingUseCase(settingsRepository)
    }
}
