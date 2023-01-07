package com.example.movieindex.fake.data_source

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.paging.PagingSource
import com.example.movieindex.core.data.local.CacheConstants
import com.example.movieindex.core.data.local.abstraction.CacheDataSource
import com.example.movieindex.core.data.local.model.MovieEntity
import com.example.movieindex.core.data.local.model.MovieKeyEntity
import com.example.movieindex.core.data.local.model.MoviePagingCategory
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
    private val movieKeyList = arrayListOf<MovieKeyEntity>()

    override suspend fun insertAllMovies(movies: List<MovieEntity>) {
        movieList.addAll(movies)
    }

    override fun getMovies(pagingCategory: MoviePagingCategory): PagingSource<Int, MovieEntity> {
        TODO("Not yet implemented")
    }

    override fun getMoviesWithReferenceToPagingCategory(pagingCategory: MoviePagingCategory): Flow<List<MovieEntity>> {
        return flow { emit(movieList.filter { it.pagingCategory == pagingCategory }) }
    }

    override fun getAllMovies(): Flow<List<MovieEntity>> {
        return flow { emit(movieList) }
    }

    override suspend fun clearMovies(pagingCategory: MoviePagingCategory) {
        movieList.removeIf { it.pagingCategory == pagingCategory }
    }

    override suspend fun insertAllMovieKeys(movieKeys: List<MovieKeyEntity>) {
        movieKeyList.addAll(movieKeys)
    }

    override fun getAllMovieKey(): Flow<List<MovieKeyEntity>> {
        return flow { emit(movieKeyList) }
    }

    override suspend fun movieKeyId(
        id: String,
        pagingCategory: MoviePagingCategory,
    ): MovieKeyEntity? {
        return movieKeyList.find { it.id == id && it.pagingCategory == pagingCategory }
    }

    override suspend fun clearMovieKeys(pagingCategory: MoviePagingCategory) {
        movieKeyList.removeIf { it.pagingCategory == pagingCategory }
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

}