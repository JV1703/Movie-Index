package com.example.movieindex.feature.detail.movie.domain.implementation

import com.example.movieindex.core.data.external.model.Cast
import com.example.movieindex.core.data.external.model.Crew
import com.example.movieindex.core.data.external.model.MovieDetails
import com.example.movieindex.core.data.external.model.Resource
import com.example.movieindex.core.data.remote.model.common.PostResponse
import com.example.movieindex.core.repository.abstraction.AccountRepository
import com.example.movieindex.core.repository.abstraction.MovieRepository
import com.example.movieindex.feature.detail.movie.domain.abstraction.MovieDetailsUseCase
import javax.inject.Inject

class MovieDetailsUseCaseImpl @Inject constructor(private val movieRepository: MovieRepository, private val accountRepository: AccountRepository): MovieDetailsUseCase {

    override suspend fun getMovieDetails(
        movieId: Int,
        language: String?,
        appendToResponse: String?,
    ): Resource<MovieDetails> = movieRepository.getMovieDetails(movieId = movieId,
        language = language,
        appendToResponse = appendToResponse)

    override suspend fun saveCasts(casts: List<Cast>) {
        movieRepository.saveCasts(casts)
    }

    override suspend fun saveCrews(crews: List<Crew>) {
        movieRepository.saveCrews(crews)
    }

    override suspend fun addToFavorite(
        accountId: Int,
        sessionId: String,
        favorite: Boolean,
        mediaId: Int,
        mediaType: String,
    ): Resource<PostResponse> =
        accountRepository.addToFavorite(
            accountId = accountId,
            sessionId = sessionId,
            favorite = favorite,
            mediaId = mediaId,
            mediaType = mediaType)

    override suspend fun addToWatchList(
        accountId: Int,
        sessionId: String,
        watchlist: Boolean,
        mediaId: Int,
        mediaType: String,
    ): Resource<PostResponse> = accountRepository.addToWatchList(
        accountId = accountId,
        sessionId = sessionId,
        watchlist = watchlist,
        mediaId = mediaId,
        mediaType = mediaType)

    override suspend fun getMovieAccountState(movieId: Int, sessionId: String) =
        accountRepository.getMovieAccountState(
            movieId = movieId,
            sessionId = sessionId)

}