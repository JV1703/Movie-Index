package com.example.movieindex.core.data.remote.implementation

import com.example.movieindex.core.data.remote.MovieApi
import com.example.movieindex.core.data.remote.NetworkResource
import com.example.movieindex.core.data.remote.abstraction.NetworkDataSource
import com.example.movieindex.core.data.remote.model.account.AccountDetailsResponse
import com.example.movieindex.core.data.remote.model.auth.body.LoginBody
import com.example.movieindex.core.data.remote.model.auth.response.LoginResponse
import com.example.movieindex.core.data.remote.model.auth.response.RequestTokenResponse
import com.example.movieindex.core.data.remote.model.auth.response.SessionIdResponse
import com.example.movieindex.core.data.remote.model.common.MoviesResponse
import com.example.movieindex.core.data.remote.model.common.PostResponse
import com.example.movieindex.core.data.remote.model.details.MovieDetailsResponse
import com.example.movieindex.core.data.remote.model.favorite.body.FavoriteBody
import com.example.movieindex.core.data.remote.model.watchlist.body.WatchListBody
import com.example.movieindex.core.data.remote.safeNetworkCall
import com.example.movieindex.core.di.CoroutinesQualifiers
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class NetworkDataSourceImpl @Inject constructor(
    private val movieApi: MovieApi,
    @CoroutinesQualifiers.IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : NetworkDataSource {

    override suspend fun getNowPlaying(
        page: Int,
        language: String?,
        region: String?,
    ): NetworkResource<MoviesResponse> =
        safeNetworkCall(dispatcher = ioDispatcher,
            networkCall = {
                movieApi.getNowPlaying(page = page,
                    language = language,
                    region = region)
            },
            conversion = { data: MoviesResponse -> data })

    override suspend fun getPopularMovies(
        page: Int,
        language: String?,
        region: String?,
    ): NetworkResource<MoviesResponse> =
        safeNetworkCall(dispatcher = ioDispatcher, networkCall = {
            movieApi.getPopularMovies(page = page, language = language, region = region)
        }, conversion = { data: MoviesResponse -> data })

    override suspend fun getMovieRecommendations(
        movieId: Int,
        page: Int,
        language: String?,
    ): NetworkResource<MoviesResponse> =
        safeNetworkCall(dispatcher = ioDispatcher, networkCall = {
            movieApi.getMovieRecommendations(movieId = movieId, page = page, language = language)
        }, conversion = { data: MoviesResponse -> data })

    override suspend fun getTrendingMovies(
        page: Int,
        mediaType: String,
        timeWindow: String,
    ): NetworkResource<MoviesResponse> =
        safeNetworkCall(dispatcher = ioDispatcher, networkCall = {
            movieApi.getTrendingMovies(page = page, mediaType = mediaType, timeWindow = timeWindow)
        }, conversion = { data: MoviesResponse -> data })

    override suspend fun getMovieDetails(
        movieId: Int,
        language: String?,
        appendToResponse: String?,
    ): NetworkResource<MovieDetailsResponse> =
        safeNetworkCall(dispatcher = ioDispatcher, networkCall = {
            movieApi.getMovieDetails(movieId, language, appendToResponse)
        }, conversion = { data: MovieDetailsResponse -> data })

    override suspend fun searchMovies(
        query: String,
        language: String?,
        page: Int,
        includeAdult: Boolean?,
        region: String?,
        year: Int?,
        primaryReleaseYear: Int?,
    ): NetworkResource<MoviesResponse> = safeNetworkCall(dispatcher = ioDispatcher,
        networkCall = {
            movieApi.searchMovies(query = query,
                language = language,
                page = page,
                includeAdult = includeAdult,
                region = region,
                year = year,
                primaryReleaseYear = primaryReleaseYear)
        },
        conversion = { it })

    override suspend fun requestToken(): NetworkResource<RequestTokenResponse> =
        safeNetworkCall(dispatcher = ioDispatcher,
            networkCall = { movieApi.requestToken() },
            conversion = { it })

    override suspend fun login(
        loginBody: LoginBody,
    ): NetworkResource<LoginResponse> =
        safeNetworkCall(dispatcher = ioDispatcher, networkCall = {
            movieApi.login(
                loginBody
            )
        }, conversion = { it })

    override suspend fun createSession(requestToken: String): NetworkResource<SessionIdResponse> =
        safeNetworkCall(
            dispatcher = ioDispatcher,
            networkCall = { movieApi.createSession(requestToken) },
            conversion = { it })

    override suspend fun getAccountDetails(sessionId: String): NetworkResource<AccountDetailsResponse> =
        safeNetworkCall(
            dispatcher = ioDispatcher,
            networkCall = { movieApi.getAccountDetails(sessionId = sessionId) },
            conversion = { it })

    override suspend fun addToFavorite(
        accountId: Int,
        sessionId: String,
        body: FavoriteBody,
    ): NetworkResource<PostResponse> =
        safeNetworkCall(dispatcher = ioDispatcher,
            networkCall = {
                movieApi.addToFavorite(accountId = accountId,
                    sessionId = sessionId,
                    body = body)
            },
            conversion = { it })

    override suspend fun addToWatchList(
        accountId: Int,
        sessionId: String,
        body: WatchListBody
    ): NetworkResource<PostResponse> =
        safeNetworkCall(dispatcher = ioDispatcher,
            networkCall = { movieApi.addToWatchList(accountId = accountId, sessionId = sessionId, body = body) },
            conversion = { it })

    override suspend fun getFavoriteList(
        accountId: Int,
        sessionId: String,
        page: Int,
        language: String?,
        sortBy: String?,
    ): NetworkResource<MoviesResponse> = safeNetworkCall(dispatcher = ioDispatcher,
        networkCall = {
            movieApi.getFavoriteList(accountId = accountId,
                sessionId = sessionId,
                page = page,
                language = language,
                sortBy = sortBy)
        },
        conversion = { it })

    override suspend fun getWatchList(
        accountId: Int,
        sessionId: String,
        page: Int,
        language: String?,
        sortBy: String?,
    ): NetworkResource<MoviesResponse> = safeNetworkCall(dispatcher = ioDispatcher,
        networkCall = {
            movieApi.getWatchList(accountId = accountId,
                sessionId = sessionId,
                page = page,
                language = language,
                sortBy = sortBy)
        },
        conversion = { it })
}