package com.example.movieindex.fake.apis

import com.example.movieindex.core.data.remote.MovieApi
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
import com.example.movieindex.util.TestDataFactory
import retrofit2.Response

class FakeMovieApi(private val testDataFactory: TestDataFactory) : MovieApi {

    var isSuccess = true
    var isBodyEmpty = false

    override suspend fun getNowPlaying(
        page: Int,
        language: String?,
        region: String?,
    ): Response<MoviesResponse> {
        return testDataFactory.generateResponse(isSuccess = isSuccess, isBodyEmpty = isBodyEmpty) {
            Response.success(testDataFactory.generateNowPlayingMoviesTestData())
        }
    }

    override suspend fun getPopularMovies(
        page: Int,
        language: String?,
        region: String?,
    ): Response<MoviesResponse> {
        return testDataFactory.generateResponse(isSuccess = isSuccess, isBodyEmpty = isBodyEmpty) {
            Response.success(testDataFactory.generatePopularMoviesTestData())
        }
    }

    override suspend fun getTrendingMovies(
        mediaType: String,
        timeWindow: String,
        page: Int,
    ): Response<MoviesResponse> {
        return testDataFactory.generateResponse(isSuccess = isSuccess, isBodyEmpty = isBodyEmpty) {
            Response.success(testDataFactory.generateTrendingMoviesTestData())
        }
    }

    override suspend fun getMovieRecommendations(
        movieId: Int,
        page: Int,
        language: String?,
    ): Response<MoviesResponse> {
        return testDataFactory.generateResponse(isSuccess = isSuccess, isBodyEmpty = isBodyEmpty) {
            Response.success(testDataFactory.generateRecommendationsTestData())
        }
    }

    override suspend fun getMovieDetails(
        movieId: Int,
        language: String?,
        appendToResponse: String?,
    ): Response<MovieDetailsResponse> {
        return testDataFactory.generateResponse(isSuccess = isSuccess, isBodyEmpty = isBodyEmpty) {
            Response.success(testDataFactory.generateMovieDetailsTestData())
        }
    }

    override suspend fun searchMovies(
        query: String,
        language: String?,
        page: Int,
        includeAdult: Boolean?,
        region: String?,
        year: Int?,
        primaryReleaseYear: Int?,
    ): Response<MoviesResponse> {
        return testDataFactory.generateResponse(isSuccess = isSuccess, isBodyEmpty = isBodyEmpty) {
            Response.success(testDataFactory.generateSearchMoviesTestData())
        }
    }

    override suspend fun requestToken(): Response<RequestTokenResponse> {
        return testDataFactory.generateResponse(isSuccess = isSuccess, isBodyEmpty = isBodyEmpty) {
            Response.success(testDataFactory.generateRequestTokenTestData())
        }
    }

    override suspend fun login(loginBody: LoginBody): Response<LoginResponse> {
        return testDataFactory.generateResponse(isSuccess = isSuccess, isBodyEmpty = isBodyEmpty) {
            Response.success(testDataFactory.generateLoginTestData(loginBody))
        }
    }

    override suspend fun createSession(requestToken: String): Response<SessionIdResponse> {
        return testDataFactory.generateResponse(isSuccess = isSuccess, isBodyEmpty = isBodyEmpty) {
            Response.success(testDataFactory.generateSessionIdTestData())
        }
    }

    override suspend fun getAccountDetails(sessionId: String): Response<AccountDetailsResponse> {
        return testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(testDataFactory.generateAccountDetailsResponseTestData()) }
    }

    override suspend fun addToFavorite(
        accountId: Int,
        sessionId: String,
        body: FavoriteBody,
    ): Response<PostResponse> {
        return testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(testDataFactory.generatePostResponseTestData()) }
    }

    override suspend fun addToWatchList(
        accountId: Int,
        sessionId: String,
        body: WatchListBody,
    ): Response<PostResponse> {
        return testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(testDataFactory.generatePostResponseTestData()) }
    }

    override suspend fun getFavoriteList(
        accountId: Int,
        sessionId: String,
        page: Int,
        language: String?,
        sortBy: String?,
    ): Response<MoviesResponse> {
        return testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(testDataFactory.generateFavoriteMoviesListResponseTestData()) }
    }

    override suspend fun getWatchList(
        accountId: Int,
        sessionId: String,
        page: Int,
        language: String?,
        sortBy: String?,
    ): Response<MoviesResponse> {
        return testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(testDataFactory.generateWatchListResponseTestData()) }
    }

}