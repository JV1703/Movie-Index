package com.example.movieindex.core.data.local.implementation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.example.movieindex.core.data.local.CacheConstants.ACCOUNT_ID
import com.example.movieindex.core.data.local.CacheConstants.CASTS
import com.example.movieindex.core.data.local.CacheConstants.CREWS
import com.example.movieindex.core.data.local.CacheConstants.SESSION_ID
import com.example.movieindex.core.data.local.abstraction.CacheDataSource
import com.example.movieindex.core.data.local.dao.MovieDao
import com.example.movieindex.core.data.local.model.MovieEntity
import com.example.movieindex.core.di.CoroutinesQualifiers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class CacheDataSourceImpl @Inject constructor(
    private val movieDao: MovieDao,
    private val dataStore: DataStore<Preferences>,
    @CoroutinesQualifiers.IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : CacheDataSource {

    override suspend fun insertMovie(movie: MovieEntity) {
        movieDao.insertMovie(movie)
    }

    override fun getMovie(movieId: Int): Flow<MovieEntity?> =
        movieDao.getMovie(movieId).catch { t -> Timber.e("getMovie: ${t.message}") }
            .flowOn(ioDispatcher)

    override fun getFavoriteMovies(): Flow<List<MovieEntity>> =
        movieDao.getFavoriteMovies().catch { t -> Timber.e("getFavoriteMovies: ${t.message}") }
            .flowOn(ioDispatcher)

    override fun getBookmarkedMovies(): Flow<List<MovieEntity>> =
        movieDao.getBookmarkedMovies().catch { t -> Timber.e("getBookmarkedMovies: ${t.message}") }
            .flowOn(ioDispatcher)

    override suspend fun updateBookmark(movieId: Int, isBookmark: Boolean) {
        movieDao.updateBookmark(movieId = movieId, isBookmark = isBookmark)
    }

    override suspend fun updateFavorite(movieId: Int, isFavorite: Boolean) {
        movieDao.updateFavorite(movieId = movieId, isFavorite = isFavorite)
    }

    override suspend fun deleteMovie(movieId: Int) {
        movieDao.deleteMovie(movieId = movieId)
    }

    // datastore - preferences

    override suspend fun saveSessionId(sessionId: String) {
        withContext(ioDispatcher) {
            dataStore.edit { preferences ->
                preferences[SESSION_ID] = sessionId
            }
        }
    }

    override fun getSessionId(): Flow<String> = dataStore.data.catch {
        if (it is IOException) {
            it.printStackTrace()
            emit(emptyPreferences())
        } else {
            throw it
        }
    }.map { preferences ->
        preferences[SESSION_ID] ?: ""
    }.flowOn(ioDispatcher)

    override suspend fun clearDataStore() {
        withContext(ioDispatcher) {
            dataStore.edit {
                it.clear()
            }
        }
    }

    override suspend fun saveCasts(casts: String) {
        withContext(ioDispatcher) {
            dataStore.edit { preferences ->
                preferences[CASTS] = casts
            }
        }
    }

    override fun getCasts(): Flow<String> = dataStore.data.catch {
        if (it is IOException) {
            it.printStackTrace()
            emit(emptyPreferences())
        } else {
            throw it
        }
    }.map { preferences ->
        preferences[CASTS] ?: ""
    }.flowOn(ioDispatcher)

    override suspend fun saveCrews(crews: String) {
        withContext(ioDispatcher) {
            dataStore.edit { preferences ->
                preferences[CREWS] = crews
            }
        }
    }

    override fun getCrews(): Flow<String> = dataStore.data.catch {
        if (it is IOException) {
            it.printStackTrace()
            emit(emptyPreferences())
        } else {
            throw it
        }
    }.map { preferences ->
        preferences[CREWS] ?: ""
    }.flowOn(ioDispatcher)

    override suspend fun saveAccountId(accountId: Int) {
        withContext(ioDispatcher) {
            dataStore.edit { preferences ->
                preferences[ACCOUNT_ID] = accountId
            }
        }
    }

    override fun getAccountId(): Flow<Int> = dataStore.data.catch {
        if (it is IOException) {
            it.printStackTrace()
            emit(emptyPreferences())
        } else {
            throw it
        }
    }.map { preferences ->
        preferences[ACCOUNT_ID] ?: 0
    }.flowOn(ioDispatcher)

}