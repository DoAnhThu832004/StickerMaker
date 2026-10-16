package com.thupo.di

import com.thupo.data.repository.StickerRepositoryImpl
import com.thupo.data.repository.UserPreferencesRepositoryImpl
import com.thupo.domain.repository.StickerRepository
import com.thupo.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        userPreferencesRepositoryImpl: UserPreferencesRepositoryImpl
    ) : UserPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindStickerRepository(
        stickerRepositoryImpl: StickerRepositoryImpl
    ) : StickerRepository
}
