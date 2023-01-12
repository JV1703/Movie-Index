package com.example.movieindex.core.repository.implementation

import androidx.paging.*
import com.example.movieindex.core.common.wrapEspressoIdlingResource
import com.example.movieindex.core.data.external.model.*
import com.example.movieindex.core.data.local.abstraction.CacheDataSource
import com.example.movieindex.core.data.local.model.*
import com.example.movieindex.core.data.remote.NetworkResource
import com.example.movieindex.core.data.remote.abstraction.NetworkDataSource
import com.example.movieindex.core.data.remote.model.account.toAccountEntity
import com.example.movieindex.core.data.remote.model.account.toMovieAccountState
import com.example.movieindex.core.data.remote.model.common.PostResponse
import com.example.movieindex.core.data.remote.model.favorite.body.FavoriteBody
import com.example.movieindex.core.data.remote.model.watchlist.body.WatchListBody
import com.example.movieindex.core.repository.abstraction.AccountRepository
import com.example.movieindex.core.repository.paging.MoviesPagingRemoteMediator
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class AccountRepositoryImpl @Inject constructor(
    private val network: NetworkDataSource,
    private val cache: CacheDataSource,
) : AccountRepository {

    override fun getAccountDetails(sessionId: String): Flow<Resource<AccountDetails?>> = flow {
        wrapEspressoIdlingResource {
            val networkResource = network.getAccountDetails(sessionId = sessionId)
            val cachedAccountDetails = cache.getAccountDetails().first()

            emit(Resource.Success(data = cachedAccountDetails?.toAccountDetails()))

            when (networkResource) {
                is NetworkResource.Success -> {
                    val networkAccountDetails = networkResource.data
                    val convertedNetworkAccountDetails = networkAccountDetails.toAccountEntity()

                    if (convertedNetworkAccountDetails != cachedAccountDetails) {
                        cache.insertAccountDetails(convertedNetworkAccountDetails)
                        emit(Resource.Success(convertedNetworkAccountDetails.toAccountDetails()))
                    }
                }
                is NetworkResource.Error -> {
                    emit(Resource.Error(errCode = networkResource.errCode,
                        errMsg = networkResource.errMsg))
                }
                is NetworkResource.Empty -> {
                    emit(Resource.Empty)
                }
            }
        }
    }

    override suspend fun addToFavorite(
        sessionId: String,
        accountId: Int,
        favorite: Boolean,
        mediaId: Int,
        mediaType: String,
    ): Resource<PostResponse> {
        wrapEspressoIdlingResource {
            val body = FavoriteBody(
                mediaId = mediaId, mediaType = mediaType, favorite = favorite
            )
            val networkResource =
                network.addToFavorite(accountId = accountId, sessionId = sessionId, body = body)

            return networkResourceHandler(networkResource, conversion = { it })
        }
    }

    override suspend fun addToWatchList(
        sessionId: String,
        accountId: Int,
        watchlist: Boolean,
        mediaId: Int,
        mediaType: String,
    ): Resource<PostResponse> {
        wrapEspressoIdlingResource {
            val body = WatchListBody(
                mediaId = mediaId, mediaType = mediaType, watchlist = watchlist
            )
            val networkResource =
                network.addToWatchList(accountId = accountId, sessionId = sessionId, body = body)
            return networkResourceHandler(networkResource, conversion = { it })
        }
    }

    override fun getFavoriteListRemoteMediator(
        accountId: Int,
        sessionId: String,
        loadSinglePage: Boolean,
        language: String?,
        sortBy: String?,
    ): Flow<PagingData<Result>> = wrapEspressoIdlingResource {
        Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            remoteMediator = MoviesPagingRemoteMediator(
                loadSinglePage = loadSinglePage,
                networkCall = { page ->
                    network.getFavoriteList(
                        accountId = accountId,
                        sessionId = sessionId,
                        page = page,
                        language = language,
                        sortBy = sortBy)
                },
                pagingCategory = MoviePagingCategory.FAVORITE,
                dbCallGetMovieKey = { id: String ->
                    cache.movieKeyId(id = id)
                },
                dbCallOnRefreshClearDb = { pagingCategory: MoviePagingCategory ->
                    cache.clearMovieKeys(pagingCategory = pagingCategory)
                    cache.clearMovies(pagingCategory = pagingCategory)
                },
                dbCallOnSuccess = { movieKeys: List<MovieEntityKey>, movies: List<MoviePagingEntity> ->
                    cache.insertAllMovieKeys(movieKeys)
                    cache.insertAllMovies(movies)
                }
            ),
            pagingSourceFactory = {
                cache.getMovies(pagingCategory = MoviePagingCategory.FAVORITE)
            }
        )
    }.flow.map { pagingData ->
        pagingData.map {
            it.toResult()
        }
    }.catch { t: Throwable ->
        Timber.e("getFavoriteListRemoteMediator: ${t.message}")
    }

    override fun getWatchListRemoteMediator(
        accountId: Int,
        sessionId: String,
        loadSinglePage: Boolean,
        language: String?,
        sortBy: String?,
    ): Flow<PagingData<Result>> = wrapEspressoIdlingResource {
        Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            remoteMediator = MoviesPagingRemoteMediator(
                loadSinglePage = loadSinglePage,
                networkCall = { page ->
                    network.getWatchList(
                        accountId = accountId,
                        sessionId = sessionId,
                        page = page,
                        language = language,
                        sortBy = sortBy)
                },
                pagingCategory = MoviePagingCategory.WATCHLIST,
                dbCallGetMovieKey = { id: String ->
                    cache.movieKeyId(id = id)
                },
                dbCallOnRefreshClearDb = { pagingCategory: MoviePagingCategory ->
                    cache.clearMovieKeys(pagingCategory = pagingCategory)
                    cache.clearMovies(pagingCategory = pagingCategory)
                },
                dbCallOnSuccess = { movieKeys: List<MovieEntityKey>, movies: List<MoviePagingEntity> ->
                    cache.insertAllMovieKeys(movieKeys)
                    cache.insertAllMovies(movies)
                }
            ),
            pagingSourceFactory = {
                cache.getMovies(pagingCategory = MoviePagingCategory.WATCHLIST)
            }
        )
    }.flow.map { pagingData ->
        pagingData.map {
            it.toResult()
        }
    }.catch { t: Throwable ->
        Timber.e("getWatchListRemoteMediator: ${t.message}")
    }

    override fun getAccountIdCache(): Flow<Int?> = wrapEspressoIdlingResource {
        cache.getAccountId()
    }

    override suspend fun deleteAccountDetailsCache() = wrapEspressoIdlingResource {
        cache.deleteAccountDetails()
    }

    override suspend fun getMovieAccountState(
        movieId: Int,
        sessionId: String,
    ): Resource<MovieAccountState> {
        wrapEspressoIdlingResource {
            val networkResource = network.getMovieAccountState(
                movieId = movieId,
                sessionId = sessionId)

            return networkResourceHandler(networkResource, conversion = { it.toMovieAccountState() })
        }
    }

}