package com.example.movieindex.feature.common.domain.implementation

import androidx.paging.PagingData
import com.example.movieindex.core.data.external.model.*
import com.example.movieindex.core.repository.abstraction.MovieRepository
import com.example.movieindex.feature.common.domain.abstraction.MovieUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import timber.log.Timber
import javax.inject.Inject

class MovieUseCaseImpl @Inject constructor(private val movieRepository: MovieRepository) :
    MovieUseCase {

    override fun getNowPlaying(
        page: Int,
        language: String?,
        region: String?,

        ): Flow<Resource<List<Result>>> =
        movieRepository.getNowPlaying(
            page = page,
            language = language,
            region = region,
        )

    override fun getPopularMovies(
        page: Int,
        language: String?,
        region: String?,

        ): Flow<Resource<List<Result>>> = movieRepository.getPopularMovies(page = page,
        language = language,
        region = region)

    override fun getTrendingMovies(
        page: Int,
        mediaType: String,
        timeWindow: String,

        ): Flow<Resource<List<Result>>> = movieRepository.getTrendingMovies(page = page,
        mediaType = mediaType,
        timeWindow = timeWindow)


    override fun getMovieDetails(
        movieId: Int,
        language: String?,
        appendToResponse: String?,
    ): Flow<Resource<MovieDetails>> = movieRepository.getMovieDetails(movieId = movieId,
        language = language,
        appendToResponse = appendToResponse)

    override fun getNowPlayingPagingSource(
        loadSinglePage: Boolean,
        region: String?,
        language: String?,
    ): Flow<PagingData<Result>> =
        movieRepository.getNowPlayingPagingSource(loadSinglePage = loadSinglePage,
            region = region,
            language = language)

    override fun getPopularMoviesPagingSource(
        loadSinglePage: Boolean,
        region: String?,
        language: String?,
    ): Flow<PagingData<Result>> =
        movieRepository.getPopularMoviesPagingSource(loadSinglePage = loadSinglePage,
            region = region,
            language = language)

    override fun getTrendingMoviesPagingSource(
        loadSinglePage: Boolean,
        mediaType: String,
        timeWindow: String,
    ): Flow<PagingData<Result>> =
        movieRepository.getTrendingMoviesPagingSource(loadSinglePage = loadSinglePage,
            mediaType = mediaType,
            timeWindow = timeWindow)

    override fun getMovieRecommendationPagingSource(
        loadSinglePage: Boolean,
        movieId: Int,
        language: String?,
    ): Flow<PagingData<Result>> =
        movieRepository.getMovieRecommendationPagingSource(loadSinglePage = loadSinglePage,
            movieId = movieId,
            language = language)

    override fun searchMoviesPagingSource(
        loadSinglePage: Boolean,
        query: String,
        language: String?,
        includeAdult: Boolean?,
        region: String?,
        year: Int?,
        primaryReleaseYear: Int?,
    ) = movieRepository.searchMoviesPagingSource(
        loadSinglePage = loadSinglePage,
        query = query,
        language = language,
        includeAdult = includeAdult,
        region = region,
        year = year)

    override fun getFavoriteListRemoteMediator(
        accountId: Int,
        sessionId: String,
        loadSinglePage: Boolean,
        language: String?,
        sortBy: String?,
    ): Flow<PagingData<Result>> = movieRepository.getFavoriteListRemoteMediator(
        accountId = accountId,
        sessionId = sessionId,
        loadSinglePage = loadSinglePage,
        language = language,
        sortBy = sortBy).catch { t -> Timber.e("getFavoriteListRemoteMediator - ${t.message}") }

    override fun getWatchListRemoteMediator(
        accountId: Int,
        sessionId: String,
        loadSinglePage: Boolean,
        language: String?,
        sortBy: String?,
    ): Flow<PagingData<Result>> = movieRepository.getWatchListRemoteMediator(
        accountId = accountId,
        sessionId = sessionId,
        loadSinglePage = loadSinglePage,
        language = language,
        sortBy = sortBy).catch { t -> Timber.e("getWatchListRemoteMediator - ${t.message}") }

    override fun addToFavorite(
        favorite: Boolean,
        mediaId: Int,
        mediaType: String,
    ) =
        movieRepository.addToFavorite(
            favorite = favorite,
            mediaId = mediaId,
            mediaType = mediaType)

    override fun addToWatchList(
        watchlist: Boolean,
        mediaId: Int,
        mediaType: String,
    ) = movieRepository.addToWatchList(
        watchlist = watchlist,
        mediaId = mediaId,
        mediaType = mediaType)

    override suspend fun saveCasts(casts: List<Cast>) {
        movieRepository.saveCasts(casts)
    }

    override fun getCasts(): Flow<List<Cast>> = movieRepository.getCasts()

    override suspend fun saveCrews(crews: List<Crew>) {
        movieRepository.saveCrews(crews)
    }

    override fun getCrews(): Flow<List<Crew>> = movieRepository.getCrews()

//    override fun getAccountId(): Flow<Int> = movieRepository.getAccountIdCache()

    override suspend fun insertMovieToCache(
        movieDetails: MovieDetails,
        isFavorite: Boolean,
        isBookmarked: Boolean,
    ) {
        movieRepository.insertMovieToCache(movie = movieDetails,
            isFavorite = isFavorite,
            isBookmark = isBookmarked)
    }

    override fun getCachedMovie(movieId: Int): Flow<SavedMovie?> =
        movieRepository.getCachedMovie(movieId = movieId)

    override suspend fun updateBookmarkCache(movieId: Int, isBookmarked: Boolean) {
        movieRepository.updateBookmarkCache(movieId = movieId, isBookmark = isBookmarked)
    }

    override suspend fun updateFavoriteCache(movieId: Int, isFavorite: Boolean) {
        movieRepository.updateFavoriteCache(movieId = movieId, isFavorite = isFavorite)
    }

    override suspend fun deleteSavedMovieCache(movieId: Int) {
        movieRepository.deleteMovieCache(movieId)
    }
}