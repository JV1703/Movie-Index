package com.example.movieindex.feature.list.movie_list.domain.implementation

import androidx.paging.PagingData
import com.example.movieindex.core.data.external.model.Result
import com.example.movieindex.core.repository.abstraction.AccountRepository
import com.example.movieindex.core.repository.abstraction.MovieRepository
import com.example.movieindex.feature.list.movie_list.domain.abstraction.MovieListUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import timber.log.Timber
import javax.inject.Inject

class MovieListUseCaseImpl @Inject constructor(
    private val movieRepository: MovieRepository,
    private val accountRepository: AccountRepository,
) : MovieListUseCase {

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

    override fun getFavoriteListRemoteMediator(
        accountId: Int,
        sessionId: String,
        loadSinglePage: Boolean,
        language: String?,
        sortBy: String?,
    ): Flow<PagingData<Result>> = accountRepository.getFavoriteListRemoteMediator(
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
    ): Flow<PagingData<Result>> = accountRepository.getWatchListRemoteMediator(
        accountId = accountId,
        sessionId = sessionId,
        loadSinglePage = loadSinglePage,
        language = language,
        sortBy = sortBy).catch { t -> Timber.e("getWatchListRemoteMediator - ${t.message}") }

}