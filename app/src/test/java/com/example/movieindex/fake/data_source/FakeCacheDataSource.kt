package com.example.movieindex.fake.data_source

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.paging.PagingSource
import com.example.movieindex.core.data.local.CacheConstants
import com.example.movieindex.core.data.local.abstraction.CacheDataSource
import com.example.movieindex.core.data.local.model.AccountEntity
import com.example.movieindex.core.data.local.model.MovieEntityKey
import com.example.movieindex.core.data.local.model.MoviePagingCategory
import com.example.movieindex.core.data.local.model.MoviePagingEntity
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

    private val movieList = arrayListOf<MoviePagingEntity>()
    private val movieKeyList = arrayListOf<MovieEntityKey>()
    private val accountDetailsList = arrayListOf<AccountEntity>()

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

    override suspend fun insertAllMovies(movies: List<MoviePagingEntity>) {
        movieList.addAll(movies)
    }

    override fun getMovies(pagingCategory: MoviePagingCategory): PagingSource<Int, MoviePagingEntity> {
        TODO("Not yet implemented")
    }

    override fun getMoviesWithReferenceToPagingCategory(pagingCategory: MoviePagingCategory): Flow<List<MoviePagingEntity>> =
        flow { emit(movieList.filter { it.pagingCategory == pagingCategory }) }

    override fun getAllMovies(): Flow<List<MoviePagingEntity>> = flow { emit(movieList) }

    override suspend fun clearMovies(pagingCategory: MoviePagingCategory) {
        movieList.clear()
    }

    override suspend fun insertAllMovieKeys(movieKeys: List<MovieEntityKey>) {
        movieKeyList.addAll(movieKeys)
    }

    override fun getAllMovieKey(): Flow<List<MovieEntityKey>> = flow { emit(movieKeyList) }

    override suspend fun movieKeyId(
        id: String,
    ): MovieEntityKey? {
        return movieKeyList.find { it.id == id }
    }

    override suspend fun clearMovieKeys(pagingCategory: MoviePagingCategory) {
        movieKeyList.clear()
    }

    override suspend fun insertAccountDetails(account: AccountEntity) {
        accountDetailsList.add(account)
    }

    override fun getAccountDetails(): Flow<AccountEntity?> =
        flow { emit(if (accountDetailsList.isEmpty()) null else accountDetailsList.first()) }

    override suspend fun deleteAccountDetails() {
        accountDetailsList.clear()
    }

    override fun getAccountId(): Flow<Int?> =
        flow { emit(if (accountDetailsList.isEmpty()) null else accountDetailsList.first().id) }
}