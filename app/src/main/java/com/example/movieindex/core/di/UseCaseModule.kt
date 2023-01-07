package com.example.movieindex.core.di

import com.example.movieindex.core.repository.abstraction.AuthRepository
import com.example.movieindex.core.repository.abstraction.MovieRepository
import com.example.movieindex.feature.auth.domain.abstraction.AuthUseCase
import com.example.movieindex.feature.auth.domain.implementation.AuthUseCaseImpl
import com.example.movieindex.feature.common.domain.abstraction.MovieUseCase
import com.example.movieindex.feature.common.domain.implementation.MovieUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    @ViewModelScoped
    fun provideAuthUseCase(
        repository: AuthRepository,
    ): AuthUseCase = AuthUseCaseImpl(repository = repository)

    @Provides
    @ViewModelScoped
    fun provideMoviesUseCase(
        repository: MovieRepository,
    ): MovieUseCase = MovieUseCaseImpl(repository = repository)

}