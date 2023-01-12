package com.example.movieindex.feature.splash.domain.implementaion

import com.example.movieindex.core.repository.abstraction.AuthRepository
import com.example.movieindex.core.repository.abstraction.MovieRepository
import com.example.movieindex.feature.splash.domain.abstraction.SyncUseCase
import javax.inject.Inject

class SyncUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val movieRepository: MovieRepository,
) : SyncUseCase {

//    fun getFavoriteList(
//        accountId: Int,
//        sessionId: String,
//        page: Int,
//        language: String?,
//        sortBy: String?,
//    ): Flow<Resource<List<Result>>> = movieRepository.getFavoriteList(
//        accountId = accountId,
//        sessionId = sessionId,
//        page = page,
//        language = language,
//        sortBy = sortBy)
//
//    fun getWatchList(
//        accountId: Int,
//        sessionId: String,
//        page: Int,
//        language: String?,
//        sortBy: String?,
//    ): Flow<Resource<List<Result>>> = movieRepository.getWatchList(
//        accountId = accountId,
//        sessionId = sessionId,
//        page = page,
//        language = language,
//        sortBy = sortBy)
//
//    fun getCachedFavoriteMovies(): Flow<List<SavedMovie>> =
//        movieRepository.getCachedFavoriteMovies()
//
//    fun getCachedWatchList(): Flow<List<SavedMovie>> = movieRepository.getCachedBookmarkedMovies()


}