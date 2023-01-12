package com.example.movieindex.feature.detail.movie.domain.abstraction

import com.example.movieindex.core.data.external.model.*
import com.example.movieindex.core.data.remote.model.common.PostResponse

interface MovieDetailsUseCase {

    suspend fun getMovieDetails(
        movieId: Int,
        language: String? = null,
        appendToResponse: String? = "videos,recommendations,credits,reviews,release_dates",
    ): Resource<MovieDetails>

    suspend fun saveCasts(casts: List<Cast>)
    suspend fun saveCrews(crews: List<Crew>)

    suspend fun addToFavorite(
        accountId: Int,
        sessionId: String,
        favorite: Boolean,
        mediaId: Int,
        mediaType: String = "movie",
    ): Resource<PostResponse>

    suspend fun addToWatchList(
        accountId: Int,
        sessionId: String,
        watchlist: Boolean,
        mediaId: Int,
        mediaType: String = "movie",
    ): Resource<PostResponse>

    suspend fun getMovieAccountState(movieId: Int, sessionId: String): Resource<MovieAccountState>

}