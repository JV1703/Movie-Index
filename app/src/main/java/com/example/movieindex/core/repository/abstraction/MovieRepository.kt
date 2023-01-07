package com.example.movieindex.core.repository.abstraction

import androidx.paging.PagingData
import com.example.movieindex.core.data.external.*
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    fun getMovieDetails(
        movieId: Int,
        language: String? = null,
        appendToResponse: String? = "videos,recommendations,credits,reviews,release_dates",
    ): Flow<Resource<MovieDetails>>

    suspend fun getMovieRecommendations(
        movieId: Int,
        page: Int = 1,
        language: String? = null,
    ): Flow<Resource<List<Result>>>

    fun searchMoviesPagingSource(
        loadSinglePage: Boolean,
        query: String,
        language: String? = null,
        includeAdult: Boolean? = null,
        region: String? = null,
        year: Int? = null,
        primaryReleaseYear: Int? = null,
    ): Flow<PagingData<Result>>

    fun getNowPlaying(
        page: Int = 1,
        language: String? = null,
        region: String? = null,
    ): Flow<Resource<List<Result>>>

    fun getPopularMovies(
        page: Int = 1,
        language: String? = null,
        region: String? = null,
    ): Flow<Resource<List<Result>>>

    fun getTrendingMovies(
        page: Int = 1,
        mediaType: String = "movie",
        timeWindow: String = "week",
    ): Flow<Resource<List<Result>>>

    fun getNowPlayingPagingSource(
        loadSinglePage: Boolean = false,
        region: String? = null,
        language: String? = null,
    ): Flow<PagingData<Result>>

    fun getPopularMoviesPagingSource(
        loadSinglePage: Boolean = false,
        region: String? = null,
        language: String? = null,
    ): Flow<PagingData<Result>>

    fun getTrendingMoviesPagingSource(
        loadSinglePage: Boolean = false,
        mediaType: String = "movie",
        timeWindow: String = "week",
    ): Flow<PagingData<Result>>

    fun getMovieRecommendationPagingSource(
        movieId: Int,
        loadSinglePage: Boolean = false,
        language: String? = null,
    ): Flow<PagingData<Result>>

    suspend fun saveCasts(casts: List<Cast>)
    fun getCasts(): Flow<List<Cast>>
    suspend fun saveCrews(crews: List<Crew>)
    fun getCrews(): Flow<List<Crew>>
}