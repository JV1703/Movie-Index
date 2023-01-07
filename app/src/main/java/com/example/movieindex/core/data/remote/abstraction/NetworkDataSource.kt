package com.example.movieindex.core.data.remote.abstraction

import com.example.movieindex.core.data.remote.model.auth.response.LoginResponse
import com.example.movieindex.core.data.remote.NetworkResource
import com.example.movieindex.core.data.remote.model.auth.body.LoginBody
import com.example.movieindex.core.data.remote.model.auth.response.RequestTokenResponse
import com.example.movieindex.core.data.remote.model.auth.response.SessionIdResponse
import com.example.movieindex.core.data.remote.model.common.MoviesResponse
import com.example.movieindex.core.data.remote.model.details.MovieDetailsResponse

interface NetworkDataSource {

    suspend fun getNowPlaying(
        page: Int = 1, language: String? = null, region: String? = null,
    ): NetworkResource<MoviesResponse>

    suspend fun getPopularMovies(
        page: Int = 1, language: String? = null, region: String? = null,
    ): NetworkResource<MoviesResponse>

    suspend fun getTrendingMovies(
        page: Int = 1,
        mediaType: String = "movie",
        timeWindow: String = "week",
    ): NetworkResource<MoviesResponse>

    suspend fun getMovieRecommendations(
        movieId: Int,
        page: Int = 1,
        language: String? = null,
    ): NetworkResource<MoviesResponse>

    suspend fun getMovieDetails(
        movieId: Int,
        language: String? = null,
        appendToResponse: String? = "videos,recommendations,credits,reviews,release_dates",
    ): NetworkResource<MovieDetailsResponse>

    suspend fun searchMovies(
        query: String,
        language: String? = null,
        page: Int = 1,
        includeAdult: Boolean? = null,
        region: String? = null,
        year: Int? = null,
        primaryReleaseYear: Int? = null,
    ): NetworkResource<MoviesResponse>

    suspend fun requestToken(): NetworkResource<RequestTokenResponse>
    suspend fun createSession(requestToken: String): NetworkResource<SessionIdResponse>
    suspend fun login(loginBody: LoginBody): NetworkResource<LoginResponse>

}