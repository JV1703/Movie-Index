package com.example.movieindex.fake.apis

import com.example.movieindex.core.data.local.dao.MovieDao
import com.example.movieindex.core.data.local.model.MovieEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeMovieDao : MovieDao {

    private val fakeMovieDatabase = arrayListOf<MovieEntity>()

    override suspend fun insertMovie(movie: MovieEntity) {
        fakeMovieDatabase.add(movie)
    }

    override fun getMovie(movieId: Int): Flow<MovieEntity?> {
        return flow { emit(fakeMovieDatabase.find { it.movieId == movieId }) }
    }

    override fun getFavoriteMovies(): Flow<List<MovieEntity>> {
        return flow { emit(fakeMovieDatabase.filter { it.isFavorite }) }
    }

    override fun getBookmarkedMovies(): Flow<List<MovieEntity>> {
        return flow { emit(fakeMovieDatabase.filter { it.isBookmark }) }
    }

    override suspend fun updateBookmark(movieId: Int, isBookmark: Boolean) {
        val movie = fakeMovieDatabase.find { it.movieId == movieId }
        val index = fakeMovieDatabase.indexOf(movie)

        movie?.let {
            fakeMovieDatabase[index] = it.copy(isBookmark = isBookmark)
        }
    }

    override suspend fun updateFavorite(movieId: Int, isFavorite: Boolean) {
        val movie = fakeMovieDatabase.find { it.movieId == movieId }
        val index = fakeMovieDatabase.indexOf(movie)

        movie?.let {
            fakeMovieDatabase[index] = it.copy(isFavorite = isFavorite)
        }
    }

    override suspend fun deleteMovie(movieId: Int) {
        val movie = fakeMovieDatabase.find { it.movieId == movieId }
        movie?.let {
            fakeMovieDatabase.remove(it)
        }
    }
}