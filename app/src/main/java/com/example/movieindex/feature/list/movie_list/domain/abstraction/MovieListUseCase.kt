package com.example.movieindex.feature.list.movie_list.domain.abstraction

import androidx.paging.PagingData
import com.example.movieindex.core.data.external.model.Result
import kotlinx.coroutines.flow.Flow

interface MovieListUseCase {

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

    fun getFavoriteListRemoteMediator(
        accountId: Int,
        sessionId: String,
        loadSinglePage: Boolean = false,
        language: String? = null,
        sortBy: String? = null,
    ): Flow<PagingData<Result>>

    fun getWatchListRemoteMediator(
        accountId: Int,
        sessionId: String,
        loadSinglePage: Boolean = false,
        language: String? = null,
        sortBy: String? = null,
    ): Flow<PagingData<Result>>

}