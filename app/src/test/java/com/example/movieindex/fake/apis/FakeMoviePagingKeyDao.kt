package com.example.movieindex.fake.apis

import com.example.movieindex.core.data.local.dao.MoviePagingKeyDao
import com.example.movieindex.core.data.local.model.MovieEntityKey
import com.example.movieindex.core.data.local.model.MoviePagingCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeMoviePagingKeyDao : MoviePagingKeyDao {

    private val movieKeyList = arrayListOf<MovieEntityKey>()

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

}