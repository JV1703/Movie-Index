package com.example.movieindex.core.data.local.abstraction

import com.example.movieindex.core.data.local.model.MovieEntity
import kotlinx.coroutines.flow.Flow

interface CacheDataSource {
    suspend fun saveSessionId(sessionId: String)
    fun getSessionId(): Flow<String>
    suspend fun clearDataStore()
    suspend fun saveCasts(casts: String)
    fun getCasts(): Flow<String>
    suspend fun saveCrews(crews: String)
    fun getCrews(): Flow<String>
    suspend fun saveAccountId(accountId: Int)
    fun getAccountId(): Flow<Int>
    suspend fun insertMovie(movie: MovieEntity)
    fun getMovie(movieId: Int): Flow<MovieEntity?>
    fun getFavoriteMovies(): Flow<List<MovieEntity>>
    fun getBookmarkedMovies(): Flow<List<MovieEntity>>
    suspend fun updateBookmark(isBookmark: Boolean)
    suspend fun updateFavorite(isFavorite: Boolean)
    suspend fun deleteMovie(movieId: Int)
}