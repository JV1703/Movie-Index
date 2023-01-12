package com.example.movieindex.core.di

import com.example.movieindex.core.repository.abstraction.AccountRepository
import com.example.movieindex.core.repository.abstraction.AuthRepository
import com.example.movieindex.core.repository.abstraction.MovieRepository
import com.example.movieindex.feature.auth.domain.abstraction.AuthUseCase
import com.example.movieindex.feature.auth.domain.implementation.AuthUseCaseImpl
import com.example.movieindex.feature.common.domain.abstraction.AccountUseCase
import com.example.movieindex.feature.common.domain.implementation.AccountUseCaseImpl
import com.example.movieindex.feature.detail.movie.domain.abstraction.MovieDetailsUseCase
import com.example.movieindex.feature.detail.movie.domain.implementation.MovieDetailsUseCaseImpl
import com.example.movieindex.feature.list.credit_list.domain.abstraction.CreditListUseCase
import com.example.movieindex.feature.list.credit_list.domain.implementation.CreditListUseCaseImpl
import com.example.movieindex.feature.list.movie_list.domain.abstraction.MovieListUseCase
import com.example.movieindex.feature.list.movie_list.domain.implementation.MovieListUseCaseImpl
import com.example.movieindex.feature.main.ui.home.domain.abstraction.HomeUseCase
import com.example.movieindex.feature.main.ui.home.domain.implementation.HomeUseCaseImpl
import com.example.movieindex.feature.main.ui.search.domain.abstraction.SearchUseCase
import com.example.movieindex.feature.main.ui.search.domain.implementation.SearchUseCaseImpl
import com.example.movieindex.feature.splash.domain.abstraction.SyncUseCase
import com.example.movieindex.feature.splash.domain.implementaion.SyncUseCaseImpl
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
    fun provideSyncUseCase(
        authRepository: AuthRepository,
        movieRepository: MovieRepository,
    ): SyncUseCase = SyncUseCaseImpl(authRepository = authRepository,
        movieRepository = movieRepository)

    @Provides
    @ViewModelScoped
    fun provideAccountUseCase(
        accountRepository: AccountRepository,
    ): AccountUseCase =
        AccountUseCaseImpl(accountRepository = accountRepository)

    @Provides
    @ViewModelScoped
    fun provideSearchUseCase(
        movieRepository: MovieRepository,
    ): SearchUseCase = SearchUseCaseImpl(movieRepository = movieRepository)

    @Provides
    @ViewModelScoped
    fun provideHomeUseCase(
        movieRepository: MovieRepository,
    ): HomeUseCase = HomeUseCaseImpl(movieRepository = movieRepository)

    @Provides
    @ViewModelScoped
    fun provideMovieListUseCase(
        movieRepository: MovieRepository,
        accountRepository: AccountRepository,
    ): MovieListUseCase = MovieListUseCaseImpl(movieRepository = movieRepository,
        accountRepository = accountRepository)

    @Provides
    @ViewModelScoped
    fun provideCreditListUseCase(
        movieRepository: MovieRepository,
    ): CreditListUseCase = CreditListUseCaseImpl(movieRepository = movieRepository)

    @Provides
    @ViewModelScoped
    fun provideMovieDetailsUseCase(
        movieRepository: MovieRepository,
        accountRepository: AccountRepository,
    ): MovieDetailsUseCase = MovieDetailsUseCaseImpl(movieRepository = movieRepository,
        accountRepository = accountRepository)

}