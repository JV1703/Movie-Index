package com.example.movieindex.core.data.local.implementation

import androidx.paging.PagingSource
import com.example.movieindex.core.data.local.abstraction.CacheDataSource
import com.example.movieindex.core.data.local.dao.MovieDao
import com.example.movieindex.core.data.local.dao.MovieKeyDao
import com.example.movieindex.core.data.local.model.MovieEntity
import com.example.movieindex.core.data.local.model.MovieKeyEntity
import com.example.movieindex.core.data.local.model.MoviePagingCategory
import com.example.movieindex.core.di.CoroutinesQualifiers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CacheDataSourceImpl @Inject constructor(
    private val movieDao: MovieDao,
    private val movieKeyDao: MovieKeyDao,
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

}