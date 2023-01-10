package com.example.movieindex.fake.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.movieindex.core.data.external.*
import com.example.movieindex.core.data.external.model.*
import com.example.movieindex.core.data.local.CacheConstants.ACCOUNT_ID
import com.example.movieindex.core.data.local.CacheConstants.CASTS
import com.example.movieindex.core.data.local.CacheConstants.CREWS
import com.example.movieindex.core.data.local.model.MovieEntity
import com.example.movieindex.core.data.local.model.toSavedMovie
import com.example.movieindex.core.data.remote.model.account.toAccountDetails
import com.example.movieindex.core.data.remote.model.common.toMovies
import com.example.movieindex.core.data.remote.model.common.toResult
import com.example.movieindex.core.data.remote.model.details.toMovieDetails
import com.example.movieindex.core.data.remote.safeNetworkCall
import com.example.movieindex.core.repository.abstraction.MovieRepository
import com.example.movieindex.core.repository.implementation.networkResourceHandler
import com.example.movieindex.core.repository.paging.MoviesPagingSource
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
class FakeMovieRepository(
    private val testDispatcher: TestDispatcher,
    private val dataStore: DataStore<Preferences>,
    private val testDataFactory: TestDataFactory,
) : MovieRepository {

    var isSuccess = true
    var isBodyEmpty = false
    private val gson = Gson()

    private val movieList = arrayListOf<MovieEntity>()

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

    override suspend fun getMovieRecommendations(
        movieId: Int,
        page: Int,
        language: String?,
    ): Flow<Resource<List<Result>>> = flow {
        emit(Resource.Loading)

        val data = testDataFactory.generateRecommendationsTestData()
        val response =
            testDataFactory.generateResponse(isSuccess = isSuccess,
                isBodyEmpty = isBodyEmpty) { Response.success(data) }
        val networkResource =
            safeNetworkCall(testDispatcher, networkCall = { response }, conversion = { it })
        emit(networkResourceHandler(networkResource) { it.toMovies().results })
    }

    override fun searchMoviesPagingSource(
        loadSinglePage: Boolean,
        query: String,
        language: String?,
        includeAdult: Boolean?,
        region: String?,
        year: Int?,
        primaryReleaseYear: Int?,
    ): Flow<PagingData<Result>> = Pager(config = PagingConfig(
        pageSize = 20, enablePlaceholders = false
    ), pagingSourceFactory = {
        MoviesPagingSource(loadSinglePage = loadSinglePage, networkCall = { page ->
            val data = testDataFactory.generateSearchMoviesTestData()
            val response = testDataFactory.generateResponse(isSuccess = isSuccess,
                isBodyEmpty = isBodyEmpty) { Response.success(data) }
            safeNetworkCall(testDispatcher, networkCall = { response }, conversion = { it })
        })
    }).flow.map { pagingData ->
        pagingData.map {
            it.toResult()
        }
    }

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

    override fun getNowPlayingPagingSource(
        loadSinglePage: Boolean,
        region: String?,
        language: String?,
    ): Flow<PagingData<Result>> = Pager(config = PagingConfig(
        pageSize = 20, enablePlaceholders = false
    ), pagingSourceFactory = {
        MoviesPagingSource(loadSinglePage = loadSinglePage, networkCall = { page ->
            val data = testDataFactory.generateNowPlayingMoviesTestData()
            val response = testDataFactory.generateResponse(isSuccess = isSuccess,
                isBodyEmpty = isBodyEmpty) { Response.success(data) }
            safeNetworkCall(testDispatcher, networkCall = { response }, conversion = { it })
        })
    }).flow.map { pagingData ->
        pagingData.map {
            it.toResult()
        }
    }

    override fun getPopularMoviesPagingSource(
        loadSinglePage: Boolean,
        region: String?,
        language: String?,
    ): Flow<PagingData<Result>> = Pager(config = PagingConfig(
        pageSize = 20, enablePlaceholders = false
    ), pagingSourceFactory = {
        MoviesPagingSource(loadSinglePage = loadSinglePage, networkCall = { page ->
            val data = testDataFactory.generatePopularMoviesTestData()
            val response = testDataFactory.generateResponse(isSuccess = isSuccess,
                isBodyEmpty = isBodyEmpty) { Response.success(data) }
            safeNetworkCall(testDispatcher, networkCall = { response }, conversion = { it })
        })
    }).flow.map { pagingData ->
        pagingData.map {
            it.toResult()
        }
    }

    override fun getTrendingMoviesPagingSource(
        loadSinglePage: Boolean,
        mediaType: String,
        timeWindow: String,
    ): Flow<PagingData<Result>> = Pager(config = PagingConfig(
        pageSize = 20, enablePlaceholders = false
    ), pagingSourceFactory = {
        MoviesPagingSource(loadSinglePage = loadSinglePage, networkCall = { page ->
            val data = testDataFactory.generateTrendingMoviesTestData()
            val response = testDataFactory.generateResponse(isSuccess = isSuccess,
                isBodyEmpty = isBodyEmpty) { Response.success(data) }
            safeNetworkCall(testDispatcher, networkCall = { response }, conversion = { it })
        })
    }).flow.map { pagingData ->
        pagingData.map {
            it.toResult()
        }
    }

    override fun getMovieRecommendationPagingSource(
        movieId: Int,
        loadSinglePage: Boolean,
        language: String?,
    ): Flow<PagingData<Result>> = Pager(config = PagingConfig(
        pageSize = 20, enablePlaceholders = false
    ), pagingSourceFactory = {
        MoviesPagingSource(loadSinglePage = loadSinglePage, networkCall = { page ->
            val data = testDataFactory.generateRecommendationsTestData()
            val response = testDataFactory.generateResponse(isSuccess = isSuccess,
                isBodyEmpty = isBodyEmpty) { Response.success(data) }
            safeNetworkCall(testDispatcher, networkCall = { response }, conversion = { it })
        })
    }).flow.map { pagingData ->
        pagingData.map {
            it.toResult()
        }
    }

    override suspend fun saveCasts(casts: List<Cast>) {
        val castString = Gson().toJson(casts)
        withContext(testDispatcher) {
            dataStore.edit { preferences ->
                preferences[CASTS] = castString
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
        val castString = preferences[CASTS] ?: ""
        val listType = object : TypeToken<List<Cast>>() {}.type
        gson.fromJson<List<Cast>>(castString, listType)
    }.flowOn(testDispatcher)


    override suspend fun saveCrews(crews: List<Crew>) {
        val castString = Gson().toJson(crews)
        withContext(testDispatcher) {
            dataStore.edit { preferences ->
                preferences[CREWS] = castString
            }
        }
    }

    override fun getCrews(): Flow<List<Crew>> = dataStore.data.catch {
        if (it is IOException) {
            it.printStackTrace()
            emit(emptyPreferences())
        } else {
            throw it
        }
    }.map { preferences ->
        val castString = preferences[CREWS] ?: ""
        val listType = object : TypeToken<List<Crew>>() {}.type
        gson.fromJson<List<Crew>>(castString, listType)
    }.flowOn(testDispatcher)

    override suspend fun saveAccountIdCache(accountId: Int) {
        withContext(testDispatcher) {
            dataStore.edit { preferences ->
                preferences[ACCOUNT_ID] = accountId
            }
        }
    }

    override fun getAccountIdCache(): Flow<Int> {
        return dataStore.data.catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            preferences[ACCOUNT_ID] ?: 0
        }.flowOn(testDispatcher)
    }

    override fun getAccountDetails(sessionId: String): Flow<Resource<AccountDetails>> = flow {
        emit(Resource.Loading)

        val data = testDataFactory.generateAccountDetailsResponseTestData()
        val response =
            testDataFactory.generateResponse(isSuccess = isSuccess,
                isBodyEmpty = isBodyEmpty) { Response.success(data) }
        val networkResource =
            safeNetworkCall(testDispatcher, networkCall = { response }, conversion = { it })
        emit(networkResourceHandler(networkResource) { it.toAccountDetails() })
    }

    override fun addToFavorite(favorite: Boolean, mediaId: Int, mediaType: String) {
        TODO("Not yet implemented")
    }

    override fun addToWatchList(watchlist: Boolean, mediaId: Int, mediaType: String) {
        TODO("Not yet implemented")
    }

    override fun getFavoriteListPagingSource(
        accountId: Int,
        sessionId: String,
        loadSinglePage: Boolean,
        language: String?,
        sortBy: String?,
    ): Flow<PagingData<Result>> = Pager(config = PagingConfig(
        pageSize = 20, enablePlaceholders = false
    ), pagingSourceFactory = {
        MoviesPagingSource(loadSinglePage = loadSinglePage, networkCall = { page ->
            val data = testDataFactory.generateFavoriteMoviesListResponseTestData()
            val response = testDataFactory.generateResponse(isSuccess = isSuccess,
                isBodyEmpty = isBodyEmpty) { Response.success(data) }
            safeNetworkCall(testDispatcher, networkCall = { response }, conversion = { it })
        })
    }).flow.map { pagingData ->
        pagingData.map {
            it.toResult()
        }
    }

    override fun getWatchListPagingSource(
        accountId: Int,
        sessionId: String,
        loadSinglePage: Boolean,
        language: String?,
        sortBy: String?,
    ): Flow<PagingData<Result>> = Pager(config = PagingConfig(
        pageSize = 20, enablePlaceholders = false
    ), pagingSourceFactory = {
        MoviesPagingSource(loadSinglePage = loadSinglePage, networkCall = { page ->
            val data = testDataFactory.generateWatchListResponseTestData()
            val response = testDataFactory.generateResponse(isSuccess = isSuccess,
                isBodyEmpty = isBodyEmpty) { Response.success(data) }
            safeNetworkCall(testDispatcher, networkCall = { response }, conversion = { it })
        })
    }).flow.map { pagingData ->
        pagingData.map {
            it.toResult()
        }
    }

    override suspend fun insertMovieToCache(
        movie: MovieDetails,
        isFavorite: Boolean,
        isBookmark: Boolean,
    ) {
        movieList.add(movie.toMovieEntity(isFavorite, isBookmark))
    }

    override fun getCachedMovie(movieId: Int): Flow<SavedMovie?> =
        flow { emit(movieList.find { it.movieId == movieId }?.toSavedMovie()) }

    override fun getCachedFavoriteMovies(): Flow<List<SavedMovie>> =
        flow { emit(movieList.filter { it.isFavorite }.map { it.toSavedMovie() }) }

    override fun getCachedBookmarkedMovies(): Flow<List<SavedMovie>> =
        flow { emit(movieList.filter { it.isBookmark }.map { it.toSavedMovie() }) }

    override suspend fun updateBookmarkCache(movieId: Int, isBookmark: Boolean) {
        val movie = movieList.find { it.movieId == movieId }
        val index = movieList.indexOf(movie)

        movie?.let {
            movieList[index] = it.copy(isBookmark = isBookmark)
        }
    }

    override suspend fun updateFavoriteCache(movieId: Int, isFavorite: Boolean) {
        val movie = movieList.find { it.movieId == movieId }
        val index = movieList.indexOf(movie)

        movie?.let {
            movieList[index] = it.copy(isFavorite = isFavorite)
        }
    }

    override suspend fun deleteMovieCache(movieId: Int) {
        val movie = movieList.find { it.movieId == movieId }
        movieList.remove(movie)
    }
}