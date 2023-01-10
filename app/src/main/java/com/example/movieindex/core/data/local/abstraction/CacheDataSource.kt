package com.example.movieindex.core.data.local.abstraction

import androidx.paging.PagingSource
import com.example.movieindex.core.data.local.model.*
import kotlinx.coroutines.flow.Flow

interface CacheDataSource {
    suspend fun saveSessionId(sessionId: String)
    fun getSessionId(): Flow<String>
    suspend fun clearDataStore()
    suspend fun saveCasts(casts: String)
    fun getCasts(): Flow<String>
    suspend fun saveCrews(crews: String)
    fun getCrews(): Flow<String>
//    suspend fun saveAccountId(accountId: Int)
//    fun getAccountId(): Flow<Int>
    suspend fun insertMovie(movie: MovieEntity)
    fun getMovie(movieId: Int): Flow<MovieEntity?>
    fun getFavoriteMovies(): Flow<List<MovieEntity>>
    fun getBookmarkedMovies(): Flow<List<MovieEntity>>
    suspend fun updateBookmark(movieId: Int, isBookmark: Boolean)
    suspend fun updateFavorite(movieId: Int, isFavorite: Boolean)
    suspend fun deleteMovie(movieId: Int)
    suspend fun insertAllMovies(movies: List<MoviePagingEntity>)
    fun getMovies(pagingCategory: MoviePagingCategory): PagingSource<Int, MoviePagingEntity>
    fun getMoviesWithReferenceToPagingCategory(pagingCategory: MoviePagingCategory): Flow<List<MoviePagingEntity>>
    fun getAllMovies(): Flow<List<MoviePagingEntity>>
    suspend fun clearMovies(pagingCategory: MoviePagingCategory)
    suspend fun insertAllMovieKeys(movieKeys: List<MovieEntityKey>)
    fun getAllMovieKey(): Flow<List<MovieEntityKey>>
    suspend fun movieKeyId(id: String, pagingCategory: MoviePagingCategory): MovieEntityKey?
    suspend fun clearMovieKeys(pagingCategory: MoviePagingCategory)
    suspend fun insertAccountDetails(account: AccountEntity)
    fun getAccountDetails(): Flow<AccountEntity?>
    suspend fun deleteAccountDetails()
    fun getFavoriteCount(): Flow<Int>
    fun getWatchlistCount(): Flow<Int>
}