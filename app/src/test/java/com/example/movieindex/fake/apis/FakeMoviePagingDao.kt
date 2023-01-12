package com.example.movieindex.fake.apis

import androidx.paging.PagingSource
import com.example.movieindex.core.data.local.dao.MoviePagingDao
import com.example.movieindex.core.data.local.model.MoviePagingCategory
import com.example.movieindex.core.data.local.model.MoviePagingEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeMoviePagingDao : MoviePagingDao {

    private val movieList = arrayListOf<MoviePagingEntity>()

    override suspend fun insertAllMovies(movies: List<MoviePagingEntity>) {
        movieList.addAll(movies)
    }

    override fun getMovies(pagingCategory: MoviePagingCategory): PagingSource<Int, MoviePagingEntity> {
        TODO("Not yet implemented")
    }

    override fun getMoviesWithReferenceToPagingCategory(pagingCategory: MoviePagingCategory): Flow<List<MoviePagingEntity>> =
        flow {
            val data = movieList.filter { it.pagingCategory == pagingCategory }
            emit(data)
        }

    override fun getAllMovies(): Flow<List<MoviePagingEntity>> = flow { emit(movieList) }

    override suspend fun clearMovies(pagingCategory: MoviePagingCategory) {
        movieList.removeIf { it.pagingCategory == pagingCategory }
    }

}