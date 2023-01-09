package com.example.movieindex.feature.splash.domain.implementaion

import com.example.movieindex.core.repository.abstraction.AuthRepository
import com.example.movieindex.core.repository.abstraction.MovieRepository
import com.example.movieindex.feature.splash.domain.abstraction.SyncUseCase
import javax.inject.Inject

class SyncUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val movieRepository: MovieRepository,
) : SyncUseCase {



}