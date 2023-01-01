package com.example.movieindex.core.repository.abstraction

import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.example.movieindex.core.data.external.MovieDetails
import com.example.movieindex.core.data.external.Resource
import com.example.movieindex.core.data.external.Result
import com.example.movieindex.core.data.local.model.MovieEntity
import com.example.movieindex.core.data.local.model.MovieKeyEntity
import com.example.movieindex.core.data.local.model.MoviePagingCategory
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    fun getNowPlayingPagingData(
        isInitialLoad: Boolean = true,
        loadSinglePage: Boolean = false,
        region: String? = null,
        language: String? = null,
    ): Flow<PagingData<Result>>

    fun getPopularMoviesPagingData(
        isInitialLoad: Boolean = true,
        loadSinglePage: Boolean = false,
        region: String? = null,
        language: String? = null,
    ): Flow<PagingData<Result>>

    fun getTrendingMoviesPagingData(
        isInitialLoad: Boolean = true,
        loadSinglePage: Boolean = false,
        mediaType: String = "movie",
        timeWindow: String = "week",
    ): Flow<PagingData<Result>>

    fun getMovieDetails(
        movieId: Int,
        language: String? = null,
        appendToResponse: String? = "videos,recommendations,credits,reviews,release_dates",
    ): Flow<Resource<MovieDetails>>

    fun searchMoviesPaging(
        loadSinglePage: Boolean,
        query: String,
        language: String? = null,
        includeAdult: Boolean? = null,
        region: String? = null,
        year: Int? = null,
        primaryReleaseYear: Int? = null,
    ): Flow<PagingData<Result>>

    fun searchMovies(
        query: String,
        page: Int,
        language: String?,
        includeAdult: Boolean?,
        region: String?,
        year: Int?,
        primaryReleaseYear: Int?,
    ): Flow<Resource<List<Result>>>

    fun getMoviesWithReferenceToPagingCategory(pagingCategory: MoviePagingCategory): Flow<List<Result>>

    suspend fun insertAllMovies(movies: List<MovieEntity>)

    fun getMovies(pagingCategory: MoviePagingCategory): PagingSource<Int, MovieEntity>

    suspend fun clearMovies(pagingCategory: MoviePagingCategory)

    suspend fun insertAllMovieKeys(movieKeys: List<MovieKeyEntity>)

    suspend fun movieKeyId(id: String, pagingCategory: MoviePagingCategory): MovieKeyEntity?

    suspend fun clearMovieKeys(pagingCategory: MoviePagingCategory)

    fun getNowPlaying(
        page: Int = 1,
        language: String? = null,
        region: String? = null,
    ): Flow<Resource<List<Result>>>

    fun getPopularMovies(
        page: Int = 1,
        region: String? = null,
        language: String? = null,
    ): Flow<Resource<List<Result>>>

    fun getTrendingMovies(
        page: Int = 1,
        mediaType: String = "movie",
        timeWindow: String = "week",
    ): Flow<Resource<List<Result>>>
}