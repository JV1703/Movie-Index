package com.example.movieindex.core.data.local.abstraction

import androidx.paging.PagingSource
import com.example.movieindex.core.data.local.model.MovieEntity
import com.example.movieindex.core.data.local.model.MovieKeyEntity
import com.example.movieindex.core.data.local.model.MoviePagingCategory
import kotlinx.coroutines.flow.Flow

interface CacheDataSource {
    suspend fun insertAllMovies(movies: List<MovieEntity>)
    fun getMovies(pagingCategory: MoviePagingCategory): PagingSource<Int, MovieEntity>
    fun getMoviesWithReferenceToPagingCategory(pagingCategory: MoviePagingCategory): Flow<List<MovieEntity>>
    fun getAllMovies(): Flow<List<MovieEntity>>
    suspend fun clearMovies(pagingCategory: MoviePagingCategory)
    suspend fun insertAllMovieKeys(movieKeys: List<MovieKeyEntity>)
    fun getAllMovieKey(): Flow<List<MovieKeyEntity>>
    suspend fun movieKeyId(id: String, pagingCategory: MoviePagingCategory): MovieKeyEntity?
    suspend fun clearMovieKeys(pagingCategory: MoviePagingCategory)
}