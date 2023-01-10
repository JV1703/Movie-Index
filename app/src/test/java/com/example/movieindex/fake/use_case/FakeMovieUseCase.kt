package com.example.movieindex.fake.use_case

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.paging.PagingData
import com.example.movieindex.core.data.external.*
import com.example.movieindex.core.data.external.model.*
import com.example.movieindex.core.data.local.CacheConstants
import com.example.movieindex.core.data.remote.model.common.toMovies
import com.example.movieindex.core.data.remote.model.details.toMovieDetails
import com.example.movieindex.core.data.remote.safeNetworkCall
import com.example.movieindex.core.repository.implementation.networkResourceHandler
import com.example.movieindex.feature.common.domain.abstraction.MovieUseCase
import com.example.movieindex.util.TestDataFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class FakeMovieUseCase(
    private val testDispatcher: TestDispatcher,
    private val dataStore: DataStore<Preferences>,
    private val testDataFactory: TestDataFactory,
) : MovieUseCase {

    var isSuccess = true
    var isBodyEmpty = false
    private val gson = Gson()

    override fun getNowPlaying(
        page: Int,
        language: String?,
        region: String?,
    ): Flow<Resource<List<Result>>> = flow {
        emit(Resource.Loading)

        val data = testDataFactory.generateNowPlayingMoviesTestData()
        val response =
            testDataFactory.generateResponse(isSuccess = isSuccess,
                isBodyEmpty = isBodyEmpty) { Response.success(data) }
        val networkResource =
            safeNetworkCall(testDispatcher, networkCall = { response }, conversion = { it })
        emit(networkResourceHandler(networkResource) { it.toMovies().results })
    }

    override fun getPopularMovies(
        page: Int,
        language: String?,
        region: String?,
    ): Flow<Resource<List<Result>>> = flow {
        emit(Resource.Loading)

        val data = testDataFactory.generatePopularMoviesTestData()
        val response =
            testDataFactory.generateResponse(isSuccess = isSuccess,
                isBodyEmpty = isBodyEmpty) { Response.success(data) }
        val networkResource =
            safeNetworkCall(testDispatcher, networkCall = { response }, conversion = { it })
        emit(networkResourceHandler(networkResource) { it.toMovies().results })
    }

    override fun getTrendingMovies(
        page: Int,
        mediaType: String,
        timeWindow: String,
    ): Flow<Resource<List<Result>>> = flow {
        emit(Resource.Loading)

        val data = testDataFactory.generateTrendingMoviesTestData()
        val response =
            testDataFactory.generateResponse(isSuccess = isSuccess,
                isBodyEmpty = isBodyEmpty) { Response.success(data) }
        val networkResource =
            safeNetworkCall(testDispatcher, networkCall = { response }, conversion = { it })
        emit(networkResourceHandler(networkResource) { it.toMovies().results })
    }

    override fun getMovieDetails(
        movieId: Int,
        language: String?,
        appendToResponse: String?,
    ): Flow<Resource<MovieDetails>> = flow {
        emit(Resource.Loading)

        val data = testDataFactory.generateMovieDetailsTestData()
        val response =
            testDataFactory.generateResponse(isSuccess = isSuccess,
                isBodyEmpty = isBodyEmpty) { Response.success(data) }
        val networkResource =
            safeNetworkCall(testDispatcher, networkCall = { response }, conversion = { it })
        emit(networkResourceHandler(networkResource) { it.toMovieDetails() })
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
        loadSinglePage: Boolean,
        movieId: Int,
        language: String?,
    ): Flow<PagingData<Result>> {
        TODO("Not yet implemented")
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

    override suspend fun saveCasts(casts: List<Cast>) {
        val castString = Gson().toJson(casts)
        withContext(testDispatcher) {
            dataStore.edit { preferences ->
                preferences[CacheConstants.CASTS] = castString
            }
        }
    }

    override fun getCasts(): Flow<List<Cast>> = dataStore.data.catch {
        if (it is IOException) {
            it.printStackTrace()
            emit(emptyPreferences())
        } else {
            throw it
        }
    }.map { preferences ->
        val castString = preferences[CacheConstants.CASTS] ?: ""
        val listType = object : TypeToken<List<Cast>>() {}.type
        gson.fromJson<List<Cast>>(castString, listType)
    }.flowOn(testDispatcher)

    override suspend fun saveCrews(crews: List<Crew>) {
        val castString = Gson().toJson(crews)
        withContext(testDispatcher) {
            dataStore.edit { preferences ->
                preferences[CacheConstants.CREWS] = castString
            }
        }
    }

    override fun getCrews(): Flow<List<Crew>>  = dataStore.data.catch {
        if (it is IOException) {
            it.printStackTrace()
            emit(emptyPreferences())
        } else {
            throw it
        }
    }.map { preferences ->
        val castString = preferences[CacheConstants.CREWS] ?: ""
        val listType = object : TypeToken<List<Crew>>() {}.type
        gson.fromJson<List<Crew>>(castString, listType)
    }.flowOn(testDispatcher)

    override fun addToFavorite(favorite: Boolean, mediaId: Int, mediaType: String) {
        TODO("Not yet implemented")
    }

    override fun addToWatchList(watchlist: Boolean, mediaId: Int, mediaType: String) {
        TODO("Not yet implemented")
    }

    override fun getAccountId(): Flow<Int> {
        TODO("Not yet implemented")
    }

    override suspend fun insertMovieToCache(
        movieDetails: MovieDetails,
        isFavorite: Boolean,
        isBookmarked: Boolean,
    ) {
        TODO("Not yet implemented")
    }

    override fun getCachedMovie(movieId: Int): Flow<SavedMovie?> {
        TODO("Not yet implemented")
    }

    override suspend fun updateBookmarkCache(movieId: Int, isBookmarked: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun updateFavoriteCache(movieId: Int, isFavorite: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSavedMovieCache(movieId: Int) {
        TODO("Not yet implemented")
    }
}