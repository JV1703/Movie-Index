package com.example.movieindex.fake.apis

import androidx.paging.PagingSource
import com.example.movieindex.core.data.local.dao.MovieDao
import com.example.movieindex.core.data.local.model.MovieEntity
import com.example.movieindex.core.data.local.model.MoviePagingCategory
import kotlinx.coroutines.flow.Flow

class FakeMovieDao: MovieDao {

    val fakeMovieDatabase = arrayListOf<MovieEntity>()

    override suspend fun insertAllMovies(movies: List<MovieEntity>) {
        TODO("Not yet implemented")
    }

    override fun getMovies(pagingCategory: MoviePagingCategory): PagingSource<Int, MovieEntity> {
        TODO("Not yet implemented")
    }

    override fun getMoviesWithReferenceToPagingCategory(pagingCategory: MoviePagingCategory): Flow<List<MovieEntity>> {
        TODO("Not yet implemented")
    }

    override fun getAllMovies(): Flow<List<MovieEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun clearMovies(pagingCategory: MoviePagingCategory) {
        TODO("Not yet implemented")
    }

    override fun getMovies(): Flow<List<MovieEntity>> {
        TODO("Not yet implemented")
    }
}