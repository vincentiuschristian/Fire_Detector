package com.dev.firedetector.core.di

import com.dev.firedetector.core.data.FireRepositoryImpl
import com.dev.firedetector.data.repository.IFireRepository
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
    abstract fun bindFireRepository(impl: FireRepositoryImpl): IFireRepository
}