package com.example.movieindex.fake.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.paging.PagingData
import com.example.movieindex.core.data.external.*
import com.example.movieindex.core.data.external.model.*
import com.example.movieindex.core.data.local.CacheConstants
import com.example.movieindex.core.data.remote.model.common.toResult
import com.example.movieindex.core.data.remote.model.details.toMovieDetails
import com.example.movieindex.core.data.remote.safeNetworkCall
import com.example.movieindex.core.repository.abstraction.MovieRepository
import com.example.movieindex.util.TestDataFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.TestDispatcher
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class FakeMovieRepository(
    private val testDispatcher: TestDispatcher,
    private val dataStore: DataStore<Preferences>,
    private val testDataFactory: TestDataFactory,
) : MovieRepository {

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

    override suspend fun getMovieRecommendations(
        movieId: Int,
        page: Int,
        language: String?,
    ): Resource<List<Result>> {
        val data = testDataFactory.generateRecommendationsTestData()
        val response =
            testDataFactory.generateResponse(isSuccess = isSuccess, isBodyEmpty = isBodyEmpty) {
                Response.success(data)
            }
        val networkResource = safeNetworkCall(dispatcher = testDispatcher,
            networkCall = { response },
            conversion = { moviesResponse ->
                moviesResponse.results.map { resultResponse ->
                    resultResponse.toResult()
                }
            })
        return networkResourceHandler(networkResource = networkResource,
            conversion = { it })
    }

    override fun searchMoviesPagingSource(
        loadSinglePage: Boolean,
        query: String,
        language: String?,
        includeAdult: Boolean?,
        region: String?,
        year: Int?,
        primaryReleaseYear: Int?,
    ): Flow<PagingData<Result>> {
        TODO("Not yet implemented")
    }

    override suspend fun getNowPlaying(
        page: Int,
        language: String?,
        region: String?,
    ): Resource<List<Result>> {
        val data = testDataFactory.generateNowPlayingMoviesTestData()
        val response =
            testDataFactory.generateResponse(isSuccess = isSuccess, isBodyEmpty = isBodyEmpty) {
                Response.success(data)
            }
        val networkResource = safeNetworkCall(dispatcher = testDispatcher,
            networkCall = { response },
            conversion = { moviesResponse ->
                moviesResponse.results.map { resultResponse ->
                    resultResponse.toResult()
                }
            })
        return networkResourceHandler(networkResource = networkResource,
            conversion = { it })
    }

    override suspend fun getPopularMovies(
        page: Int,
        language: String?,
        region: String?,
    ): Resource<List<Result>> {
        val data = testDataFactory.generatePopularMoviesTestData()
        val response =
            testDataFactory.generateResponse(isSuccess = isSuccess, isBodyEmpty = isBodyEmpty) {
                Response.success(data)
            }
        val networkResource = safeNetworkCall(dispatcher = testDispatcher,
            networkCall = { response },
            conversion = { moviesResponse ->
                moviesResponse.results.map { resultResponse ->
                    resultResponse.toResult()
                }
            })
        return networkResourceHandler(networkResource = networkResource,
            conversion = { it })
    }

    override suspend fun getTrendingMovies(
        page: Int,
        mediaType: String,
        timeWindow: String,
    ): Resource<List<Result>> {
        val data = testDataFactory.generateTrendingMoviesTestData()
        val response =
            testDataFactory.generateResponse(isSuccess = isSuccess, isBodyEmpty = isBodyEmpty) {
                Response.success(data)
            }
        val networkResource = safeNetworkCall(dispatcher = testDispatcher,
            networkCall = { response },
            conversion = { moviesResponse ->
                moviesResponse.results.map { resultResponse ->
                    resultResponse.toResult()
                }
            })
        return networkResourceHandler(networkResource = networkResource,
            conversion = { it })
    }

    override fun getNowPlayingPagingSource(
        loadSinglePage: Boolean,
        region: String?,
        language: String?,
    ): Flow<PagingData<Result>> {
        TODO("Not yet implemented")
    }

    override fun getPopularMoviesPagingSource(
        loadSinglePage: Boolean,
        region: String?,
        language: String?,
    ): Flow<PagingData<Result>> {
        TODO("Not yet implemented")
    }

    override fun getTrendingMoviesPagingSource(
        loadSinglePage: Boolean,
        mediaType: String,
        timeWindow: String,
    ): Flow<PagingData<Result>> {
        TODO("Not yet implemented")
    }

    override fun getMovieRecommendationPagingSource(
        movieId: Int,
        loadSinglePage: Boolean,
        language: String?,
    ): Flow<PagingData<Result>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveCasts(casts: List<Cast>) {
        val castsString = Gson().toJson(casts)
        dataStore.edit { preferences ->
            preferences[CacheConstants.CASTS] = castsString
        }
    }

    override fun getCasts(): Flow<List<Cast>> {
        return dataStore.data.catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            val data = preferences[CacheConstants.CASTS] ?: ""

            val listType = object : TypeToken<List<Cast>>() {}.type
            gson.fromJson<List<Cast>>(data, listType)
        }.flowOn(testDispatcher)
    }

    override suspend fun saveCrews(crews: List<Crew>) {
        val crewsString = gson.toJson(crews)
        dataStore.edit { preferences ->
            preferences[CacheConstants.CREWS] = crewsString
        }
    }

    override fun getCrews(): Flow<List<Crew>> {
        return dataStore.data.catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            val data = preferences[CacheConstants.CREWS] ?: ""

            val listType = object : TypeToken<List<Crew>>() {}.type
            gson.fromJson<List<Crew>>(data, listType)
        }.flowOn(testDispatcher)
    }
}