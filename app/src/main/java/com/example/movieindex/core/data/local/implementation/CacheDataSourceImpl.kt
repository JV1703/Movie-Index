package com.example.movieindex.core.data.local.implementation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.paging.PagingSource
import com.example.movieindex.core.data.local.CacheConstants.CASTS
import com.example.movieindex.core.data.local.CacheConstants.CREWS
import com.example.movieindex.core.data.local.CacheConstants.SESSION_ID
import com.example.movieindex.core.data.local.abstraction.CacheDataSource
import com.example.movieindex.core.data.local.dao.MovieDao
import com.example.movieindex.core.data.local.dao.MovieKeyDao
import com.example.movieindex.core.data.local.model.MovieEntity
import com.example.movieindex.core.data.local.model.MovieKeyEntity
import com.example.movieindex.core.data.local.model.MoviePagingCategory
import com.example.movieindex.core.di.CoroutinesQualifiers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class CacheDataSourceImpl @Inject constructor(
    private val movieDao: MovieDao,
    private val movieKeyDao: MovieKeyDao,
    private val dataStore: DataStore<Preferences>,
    @CoroutinesQualifiers.IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : CacheDataSource {

    override suspend fun insertAllMovies(movies: List<MovieEntity>) =
        movieDao.insertAllMovies(movies = movies)

    override fun getMovies(pagingCategory: MoviePagingCategory): PagingSource<Int, MovieEntity> =
        movieDao.getMovies(pagingCategory = pagingCategory)

    override fun getMoviesWithReferenceToPagingCategory(pagingCategory: MoviePagingCategory): Flow<List<MovieEntity>> =
        movieDao.getMoviesWithReferenceToPagingCategory(pagingCategory = pagingCategory)
            .flowOn(ioDispatcher)

    override fun getAllMovies(): Flow<List<MovieEntity>> =
        movieDao.getAllMovies().flowOn(ioDispatcher)

    override suspend fun clearMovies(pagingCategory: MoviePagingCategory) =
        movieDao.clearMovies(pagingCategory = pagingCategory)

    override suspend fun insertAllMovieKeys(movieKeys: List<MovieKeyEntity>) =
        movieKeyDao.insertAllMovieKeys(movieKeys = movieKeys)

    override fun getAllMovieKey(): Flow<List<MovieKeyEntity>> =
        movieKeyDao.getAllMovieKey().flowOn(ioDispatcher)

    override suspend fun movieKeyId(
        id: String,
        pagingCategory: MoviePagingCategory,
    ): MovieKeyEntity? = movieKeyDao.movieKeyId(id = id, pagingCategory = pagingCategory)

    override suspend fun clearMovieKeys(pagingCategory: MoviePagingCategory) =
        movieKeyDao.clearMovieKeys(pagingCategory = pagingCategory)

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

}