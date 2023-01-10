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
import com.example.movieindex.core.data.local.dao.AccountDao
import com.example.movieindex.core.data.local.dao.MovieDao
import com.example.movieindex.core.data.local.dao.MoviePagingDao
import com.example.movieindex.core.data.local.dao.MoviePagingKeyDao
import com.example.movieindex.core.data.local.model.*
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
    private val moviePagingDao: MoviePagingDao,
    private val moviePagingKeyDao: MoviePagingKeyDao,
    private val accountDao: AccountDao,
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

//    override suspend fun saveAccountId(accountId: Int) {
//        withContext(ioDispatcher) {
//            dataStore.edit { preferences ->
//                preferences[ACCOUNT_ID] = accountId
//            }
//        }
//    }

//    override fun getAccountId(): Flow<Int> = dataStore.data.catch {
//        if (it is IOException) {
//            it.printStackTrace()
//            emit(emptyPreferences())
//        } else {
//            throw it
//        }
//    }.map { preferences ->
//        preferences[ACCOUNT_ID] ?: 0
//    }.flowOn(ioDispatcher)

    override suspend fun insertAllMovies(movies: List<MoviePagingEntity>) =
        moviePagingDao.insertAllMovies(movies = movies)

    override fun getMovies(pagingCategory: MoviePagingCategory): PagingSource<Int, MoviePagingEntity> =
        moviePagingDao.getMovies(pagingCategory = pagingCategory)

    override fun getMoviesWithReferenceToPagingCategory(pagingCategory: MoviePagingCategory): Flow<List<MoviePagingEntity>> =
        moviePagingDao.getMoviesWithReferenceToPagingCategory(pagingCategory = pagingCategory)
            .flowOn(ioDispatcher)

    override fun getAllMovies(): Flow<List<MoviePagingEntity>> =
        moviePagingDao.getAllMovies().flowOn(ioDispatcher)

    override suspend fun clearMovies(pagingCategory: MoviePagingCategory) =
        moviePagingDao.clearMovies(pagingCategory = pagingCategory)

    override suspend fun insertAllMovieKeys(movieKeys: List<MovieEntityKey>) =
        moviePagingKeyDao.insertAllMovieKeys(movieKeys = movieKeys)

    override fun getAllMovieKey(): Flow<List<MovieEntityKey>> =
        moviePagingKeyDao.getAllMovieKey().flowOn(ioDispatcher)

    override suspend fun movieKeyId(
        id: String,
        pagingCategory: MoviePagingCategory,
    ): MovieEntityKey? = moviePagingKeyDao.movieKeyId(id = id, pagingCategory = pagingCategory)

    override suspend fun clearMovieKeys(pagingCategory: MoviePagingCategory) =
        moviePagingKeyDao.clearMovieKeys(pagingCategory = pagingCategory)

    override suspend fun insertAccountDetails(account: AccountEntity) =
        accountDao.insertAccountDetails(account)

    override fun getAccountDetails(): Flow<AccountEntity?> =
        accountDao.getAccountDetails()

    override suspend fun deleteAccountDetails() = accountDao.deleteAccountDetails()

    override fun getFavoriteCount(): Flow<Int> = movieDao.getFavoriteCount()

    override fun getWatchlistCount(): Flow<Int> = movieDao.getWatchlistCount()

}