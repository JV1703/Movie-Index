package com.example.movieindex.core.repository.abstraction

import androidx.paging.PagingData
import com.example.movieindex.core.data.external.model.AccountDetails
import com.example.movieindex.core.data.external.model.MovieAccountState
import com.example.movieindex.core.data.external.model.Resource
import com.example.movieindex.core.data.external.model.Result
import com.example.movieindex.core.data.remote.model.common.PostResponse
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAccountDetails(sessionId: String): Flow<Resource<AccountDetails?>>
    suspend fun addToFavorite(
        sessionId: String,
        accountId: Int,
        favorite: Boolean,
        mediaId: Int,
        mediaType: String = "movie",
    ): Resource<PostResponse>

    suspend fun addToWatchList(
        sessionId: String,
        accountId: Int,
        watchlist: Boolean,
        mediaId: Int,
        mediaType: String = "movie",
    ): Resource<PostResponse>

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

    suspend fun deleteAccountDetailsCache()
    suspend fun getMovieAccountState(movieId: Int, sessionId: String): Resource<MovieAccountState>
    fun getAccountIdCache(): Flow<Int?>
}