package com.example.movieindex.core.data.remote.abstraction

import com.example.movieindex.core.data.remote.NetworkResource
import com.example.movieindex.core.data.remote.model.account.AccountDetailsResponse
import com.example.movieindex.core.data.remote.model.account.MovieAccountStateResponse
import com.example.movieindex.core.data.remote.model.auth.body.DeleteSessionBody
import com.example.movieindex.core.data.remote.model.auth.body.LoginBody
import com.example.movieindex.core.data.remote.model.auth.response.DeleteSessionResponse
import com.example.movieindex.core.data.remote.model.auth.response.LoginResponse
import com.example.movieindex.core.data.remote.model.auth.response.RequestTokenResponse
import com.example.movieindex.core.data.remote.model.auth.response.SessionIdResponse
import com.example.movieindex.core.data.remote.model.common.MoviesResponse
import com.example.movieindex.core.data.remote.model.common.PostResponse
import com.example.movieindex.core.data.remote.model.details.MovieDetailsResponse
import com.example.movieindex.core.data.remote.model.favorite.body.FavoriteBody
import com.example.movieindex.core.data.remote.model.watchlist.body.WatchListBody

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

    suspend fun getAccountDetails(sessionId: String): NetworkResource<AccountDetailsResponse>
    suspend fun addToFavorite(
        accountId: Int,
        sessionId: String,
        body: FavoriteBody,
    ): NetworkResource<PostResponse>

    suspend fun addToWatchList(
        accountId: Int,
        sessionId: String,
        body: WatchListBody,
    ): NetworkResource<PostResponse>

    suspend fun getFavoriteList(
        accountId: Int,
        sessionId: String,
        page: Int = 1,
        language: String? = null,
        sortBy: String? = null,
    ): NetworkResource<MoviesResponse>

    suspend fun getWatchList(
        accountId: Int,
        sessionId: String,
        page: Int = 1,
        language: String? = null,
        sortBy: String? = null,
    ): NetworkResource<MoviesResponse>

    suspend fun deleteSession(body: DeleteSessionBody): NetworkResource<DeleteSessionResponse>
    suspend fun getMovieAccountState(
        movieId: Int,
        sessionId: String
    ): NetworkResource<MovieAccountStateResponse>
}