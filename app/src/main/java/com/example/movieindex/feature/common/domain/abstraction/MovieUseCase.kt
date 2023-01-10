package com.example.movieindex.feature.common.domain.abstraction

import androidx.paging.PagingData
import com.example.movieindex.core.data.external.*
import kotlinx.coroutines.flow.Flow

interface MovieUseCase {
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

    fun getMovieDetails(
        movieId: Int,
        language: String? = null,
        appendToResponse: String? = "videos,recommendations,credits,reviews,release_dates",
    ): Flow<Resource<MovieDetails>>

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
        loadSinglePage: Boolean = false,
        movieId: Int,
        language: String? = null,
    ): Flow<PagingData<Result>>


    fun searchMoviesPagingSource(
        loadSinglePage: Boolean = false,
        query: String,
        language: String? = null,
        includeAdult: Boolean? = null,
        region: String? = null,
        year: Int? = null,
        primaryReleaseYear: Int? = null,
    ): Flow<PagingData<Result>>

    suspend fun saveCasts(casts: List<Cast>)
    fun getCasts(): Flow<List<Cast>>
    suspend fun saveCrews(crews: List<Crew>)
    fun getCrews(): Flow<List<Crew>>
    fun addToFavorite(
        favorite: Boolean,
        mediaId: Int,
        mediaType: String = "movie",
    )

    fun addToWatchList(
        watchlist: Boolean,
        mediaId: Int,
        mediaType: String = "movie",
    )

    fun getAccountId(): Flow<Int>
    suspend fun insertMovieToCache(
        movieDetails: MovieDetails,
        isFavorite: Boolean = false,
        isBookmarked: Boolean = false,
    )

    fun getCachedMovie(movieId: Int): Flow<SavedMovie?>
    suspend fun updateBookmarkCache(movieId: Int, isBookmarked: Boolean)
    suspend fun updateFavoriteCache(movieId: Int, isFavorite: Boolean)
    suspend fun deleteSavedMovieCache(movieId: Int)
}