package com.example.movieindex.fake.data_source

import com.example.movieindex.core.data.remote.NetworkResource
import com.example.movieindex.core.data.remote.abstraction.NetworkDataSource
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
import com.example.movieindex.core.data.remote.safeNetworkCall
import com.example.movieindex.util.TestDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class FakeNetworkDataSource(
    private val testDataFactory: TestDataFactory,
    private val testDispatcher: TestDispatcher,
) : NetworkDataSource {

    var isSuccess = true
    var isBodyEmpty = false

    override suspend fun getNowPlaying(
        page: Int,
        language: String?,
        region: String?,
    ): NetworkResource<MoviesResponse> {
        val data = testDataFactory.generateNowPlayingMoviesTestData()
        val response = testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(data) }
        return safeNetworkCall(testDispatcher, networkCall = { response }, conversion = { it })
    }

    override suspend fun getPopularMovies(
        page: Int,
        language: String?,
        region: String?,
    ): NetworkResource<MoviesResponse> {
        val data = testDataFactory.generatePopularMoviesTestData()
        val response = testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(data) }
        return safeNetworkCall(testDispatcher, networkCall = { response }, conversion = { it })
    }

    override suspend fun getTrendingMovies(
        page: Int,
        mediaType: String,
        timeWindow: String,
    ): NetworkResource<MoviesResponse> {
        val data = testDataFactory.generateTrendingMoviesTestData()
        val response = testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(data) }
        return safeNetworkCall(testDispatcher, networkCall = { response }, conversion = { it })
    }

    override suspend fun getMovieRecommendations(
        movieId: Int,
        page: Int,
        language: String?,
    ): NetworkResource<MoviesResponse> {
        val data = testDataFactory.generateRecommendationsTestData()
        val response = testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(data) }
        return safeNetworkCall(testDispatcher, networkCall = { response }, conversion = { it })
    }

    override suspend fun getMovieDetails(
        movieId: Int,
        language: String?,
        appendToResponse: String?,
    ): NetworkResource<MovieDetailsResponse> {
        val data = testDataFactory.generateMovieDetailsTestData()
        val response = testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(data) }
        return safeNetworkCall(testDispatcher, networkCall = { response }, conversion = { it })
    }

    override suspend fun searchMovies(
        query: String,
        language: String?,
        page: Int,
        includeAdult: Boolean?,
        region: String?,
        year: Int?,
        primaryReleaseYear: Int?,
    ): NetworkResource<MoviesResponse> {
        val data = testDataFactory.generateSearchMoviesTestData()
        val response = testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(data) }
        return safeNetworkCall(testDispatcher, networkCall = { response }, conversion = { it })
    }

    override suspend fun requestToken(): NetworkResource<RequestTokenResponse> {
        val data = testDataFactory.generateRequestTokenTestData()
        val response = testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(data) }
        return safeNetworkCall(testDispatcher, networkCall = { response }, conversion = { it })
    }

    override suspend fun createSession(requestToken: String): NetworkResource<SessionIdResponse> {
        val data = testDataFactory.generateSessionIdTestData()
        val response = testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(data) }
        return safeNetworkCall(testDispatcher, networkCall = { response }, conversion = { it })
    }

    override suspend fun login(loginBody: LoginBody): NetworkResource<LoginResponse> {
        val data = testDataFactory.generateLoginTestData(loginBody)
        val response = testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(data) }
        return safeNetworkCall(testDispatcher, networkCall = { response }, conversion = { it })
    }

    override suspend fun getAccountDetails(sessionId: String): NetworkResource<AccountDetailsResponse> {
        val data = testDataFactory.generateAccountDetailsResponseTestData()
        val response = testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(data) }
        return safeNetworkCall(testDispatcher, networkCall = { response }, conversion = { it })
    }

    override suspend fun addToFavorite(
        accountId: Int,
        sessionId: String,
        body: FavoriteBody,
    ): NetworkResource<PostResponse> {
        val data = testDataFactory.generatePostResponseTestData()
        val response = testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(data) }
        return safeNetworkCall(testDispatcher, networkCall = { response }, conversion = { it })
    }

    override suspend fun addToWatchList(
        accountId: Int,
        sessionId: String,
        body: WatchListBody,
    ): NetworkResource<PostResponse> {
        val data = testDataFactory.generatePostResponseTestData()
        val response = testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(data) }
        return safeNetworkCall(testDispatcher, networkCall = { response }, conversion = { it })
    }

    override suspend fun getFavoriteList(
        accountId: Int,
        sessionId: String,
        page: Int,
        language: String?,
        sortBy: String?,
    ): NetworkResource<MoviesResponse> {
        val data = testDataFactory.generateFavoriteMoviesListResponseTestData()
        val response = testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(data) }
        return safeNetworkCall(testDispatcher, networkCall = { response }, conversion = { it })
    }

    override suspend fun getWatchList(
        accountId: Int,
        sessionId: String,
        page: Int,
        language: String?,
        sortBy: String?,
    ): NetworkResource<MoviesResponse> {
        val data = testDataFactory.generateWatchListResponseTestData()
        val response = testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(data) }
        return safeNetworkCall(testDispatcher, networkCall = { response }, conversion = { it })
    }

    override suspend fun deleteSession(body: DeleteSessionBody): NetworkResource<DeleteSessionResponse> {
        val data = testDataFactory.generateDeleteSessionResponseTestData()
        val response = testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(data) }
        return safeNetworkCall(dispatcher = testDispatcher,
            networkCall = { response },
            conversion = { it })
    }

    override suspend fun getMovieAccountState(
        movieId: Int,
        sessionId: String,
    ): NetworkResource<MovieAccountStateResponse> {
        val data = testDataFactory.generateMovieAccountStateResponseTestData()
        val response = testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(data) }
        return safeNetworkCall(dispatcher = testDispatcher,
            networkCall = { response },
            conversion = { it })
    }
}