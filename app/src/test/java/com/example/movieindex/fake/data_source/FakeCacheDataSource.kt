package com.example.movieindex.fake.data_source

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.example.movieindex.core.data.local.CacheConstants
import com.example.movieindex.core.data.local.CacheConstants.ACCOUNT_ID
import com.example.movieindex.core.data.local.abstraction.CacheDataSource
import com.example.movieindex.core.data.local.model.MovieEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.withContext
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class FakeCacheDataSource(
    private val testDispatcher: TestDispatcher,
    private val dataStore: DataStore<Preferences>,
) : CacheDataSource {

    private val movieList = arrayListOf<MovieEntity>()

    override suspend fun insertMovie(movie: MovieEntity) {
        movieList.add(movie)
    }

    override fun getMovie(movieId: Int): Flow<MovieEntity?> {
        return flow { emit(movieList.find { it.movieId == movieId }) }
    }

    override fun getFavoriteMovies(): Flow<List<MovieEntity>> {
        return flow { emit(movieList.filter { it.isFavorite }) }
    }

    override fun getBookmarkedMovies(): Flow<List<MovieEntity>> {
        return flow { emit(movieList.filter { it.isBookmark }) }
    }

    override suspend fun updateBookmark(movieId: Int, isBookmark: Boolean) {
        val movie = movieList.find { it.movieId == movieId }
        val index = movieList.indexOf(movie)

        movie?.let {
            movieList[index] = it.copy(isBookmark = isBookmark)
        }
    }

    override suspend fun updateFavorite(movieId: Int, isFavorite: Boolean) {
        val movie = movieList.find { it.movieId == movieId }
        val index = movieList.indexOf(movie)

        movie?.let {
            movieList[index] = it.copy(isFavorite = isFavorite)
        }
    }

    override suspend fun deleteMovie(movieId: Int) {
        val movie = movieList.find { it.movieId == movieId }
        movie?.let {
            movieList.remove(movie)
        }
    }

    override suspend fun saveSessionId(sessionId: String) {
        withContext(testDispatcher) {
            dataStore.edit { preferences ->
                preferences[CacheConstants.SESSION_ID] = sessionId
            }
        }
    }

    override fun getSessionId(): Flow<String> {
        return dataStore.data.catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            preferences[CacheConstants.SESSION_ID] ?: ""
        }.flowOn(testDispatcher)
    }

    override suspend fun clearDataStore() {
        withContext(testDispatcher) {
            dataStore.edit {
                it.clear()
            }
        }
    }

    override suspend fun saveCasts(casts: String) {
        withContext(testDispatcher) {
            dataStore.edit { preferences ->
                preferences[CacheConstants.CASTS] = casts
            }
        }
    }

    override fun getCasts(): Flow<String> {
        return dataStore.data.catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            preferences[CacheConstants.CASTS] ?: ""
        }.flowOn(testDispatcher)
    }

    override suspend fun saveCrews(crews: String) {
        withContext(testDispatcher) {
            dataStore.edit { preferences ->
                preferences[CacheConstants.CREWS] = crews
            }
        }
    }

    override fun getCrews(): Flow<String> {
        return dataStore.data.catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            preferences[CacheConstants.CREWS] ?: ""
        }.flowOn(testDispatcher)
    }

    override suspend fun saveAccountId(accountId: Int) {
        withContext(testDispatcher) {
            dataStore.edit { preferences ->
                preferences[ACCOUNT_ID] = accountId
            }
        }
    }

    override fun getAccountId(): Flow<Int> {
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

}