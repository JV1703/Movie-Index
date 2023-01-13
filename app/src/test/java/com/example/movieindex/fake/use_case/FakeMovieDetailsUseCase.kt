package com.example.movieindex.fake.use_case

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.movieindex.core.data.external.model.*
import com.example.movieindex.core.data.local.CacheConstants
import com.example.movieindex.core.data.remote.model.account.toMovieAccountState
import com.example.movieindex.core.data.remote.model.common.PostResponse
import com.example.movieindex.core.data.remote.model.details.toMovieDetails
import com.example.movieindex.core.data.remote.safeNetworkCall
import com.example.movieindex.feature.detail.movie.domain.abstraction.MovieDetailsUseCase
import com.example.movieindex.util.TestDataFactory
import com.google.gson.Gson
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class FakeMovieDetailsUseCase(
    private val testDispatcher: TestDispatcher,
    private val dataStore: DataStore<Preferences>,
    private val testDataFactory: TestDataFactory,
) : MovieDetailsUseCase {

    var isSuccess = true
    var isBodyEmpty = false
    private val gson = Gson()

    override suspend fun getMovieDetails(
        movieId: Int,
        language: String?,
        appendToResponse: String?,
    ): Resource<MovieDetails> {
        val data = testDataFactory.generateMovieDetailsTestData()
        val response =
            testDataFactory.generateResponse(isSuccess = isSuccess, isBodyEmpty = isBodyEmpty) {
                Response.success(data)
            }
        val networkResource = safeNetworkCall(dispatcher = testDispatcher,
            networkCall = { response },
            conversion = { it })
        return networkResourceHandler(networkResource = networkResource,
            conversion = { it.toMovieDetails() })
    }

    override suspend fun saveCasts(casts: List<Cast>) {
        val castsString = Gson().toJson(casts)
        dataStore.edit { preferences ->
            preferences[CacheConstants.CASTS] = castsString
        }
    }

    override suspend fun saveCrews(crews: List<Crew>) {
        val crewsString = gson.toJson(crews)
        dataStore.edit { preferences ->
            preferences[CacheConstants.CREWS] = crewsString
        }
    }

    override suspend fun addToFavorite(
        accountId: Int,
        sessionId: String,
        favorite: Boolean,
        mediaId: Int,
        mediaType: String,
    ): Resource<PostResponse> {
        val data = testDataFactory.generatePostResponseTestData()
        val response = testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(data) }
        val networkResource =
            safeNetworkCall(dispatcher = testDispatcher,
                networkCall = { response },
                conversion = { it })
        return networkResourceHandler(networkResource = networkResource, conversion = { it })
    }

    override suspend fun addToWatchList(
        accountId: Int,
        sessionId: String,
        watchlist: Boolean,
        mediaId: Int,
        mediaType: String,
    ): Resource<PostResponse> {
        val data = testDataFactory.generatePostResponseTestData()
        val response = testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(data) }
        val networkResource =
            safeNetworkCall(dispatcher = testDispatcher,
                networkCall = { response },
                conversion = { it })
        return networkResourceHandler(networkResource = networkResource, conversion = { it })
    }

    override suspend fun getMovieAccountState(
        movieId: Int,
        sessionId: String,
    ): Resource<MovieAccountState> {
        val data = testDataFactory.generateMovieAccountStateResponseTestData()
        val response = testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(data) }
        val networkResource = safeNetworkCall(dispatcher = testDispatcher,
            networkCall = { response },
            conversion = { it })
        return networkResourceHandler(networkResource = networkResource,
            conversion = { it.toMovieAccountState() })
    }
}