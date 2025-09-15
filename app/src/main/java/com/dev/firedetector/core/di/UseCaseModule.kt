package com.dev.firedetector.core.di

import com.dev.firedetector.core.domain.usecase.FireInteractor
import com.dev.firedetector.core.domain.usecase.FireUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class UseCaseModule {

    @Binds
    @ViewModelScoped
    abstract fun bindFireUseCase(
        impl: FireInteractor
    ): FireUseCase
}