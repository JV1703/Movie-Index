package com.example.movieindex.core.repository.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.movieindex.core.data.local.model.MovieEntity
import com.example.movieindex.core.data.local.model.MovieKeyEntity
import com.example.movieindex.core.data.local.model.MoviePagingCategory
import com.example.movieindex.core.data.remote.NetworkResource
import com.example.movieindex.core.data.remote.model.common.MoviesResponse
import com.example.movieindex.core.data.remote.model.common.toMovieEntity
import timber.log.Timber

@OptIn(ExperimentalPagingApi::class)
class MoviesRemoteMediator(
    private val isInitialLoad: Boolean = true,
    private val loadSinglePage: Boolean = false,
    private val networkCall: suspend (page: Int) -> NetworkResource<MoviesResponse>,
    private val pagingCategory: MoviePagingCategory,
    private val dbCallGetMovieKey: suspend (id: String, pagingCategory: MoviePagingCategory) -> MovieKeyEntity?,
    private val dbCallOnRefreshClearDb: suspend (pagingCategory: MoviePagingCategory) -> Unit,
    private val dbCallOnSuccess: suspend (movieKeys: List<MovieKeyEntity>, movies: List<MovieEntity>) -> Unit,
) : RemoteMediator<Int, MovieEntity>() {

    override suspend fun initialize(): InitializeAction {
        return if (isInitialLoad) {
            Timber.i("saved movie category - isInitialLoad: true")
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            Timber.i("saved movie category - isInitialLoad: false")
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, MovieEntity>,
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                val prevKey = remoteKeys?.prevKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with endOfPaginationReached = false because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its nextKey is null, that means we've reached
                // the end of pagination for append.
                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                nextKey
            }
        }

        when (val networkResource = networkCall(page)) {
            is NetworkResource.Success -> {
                val movies = networkResource.data.results
                val endOfPagination =
                    if (loadSinglePage) true else page == networkResource.data.total_pages
                if (loadType == LoadType.REFRESH) {
                    dbCallOnRefreshClearDb(pagingCategory)
                }

                val prevKey = if (page == INITIAL_PAGE) null else page - 1
                val nextKey = if (endOfPagination) null else page + 1

                val movieEntity = movies.map { it.toMovieEntity(pagingCategory = pagingCategory) }
                val keys = movieEntity.map {
                    MovieKeyEntity(
                        id = "${it.movieId}/${it.title}/${it.pagingCategory}",
                        movieId = it.movieId,
                        prevKey = prevKey,
                        nextKey = nextKey,
                        pagingCategory = pagingCategory
                    )
                }

                try {
                    dbCallOnSuccess(keys, movieEntity)
                } catch (e: Exception) {
                    Timber.e(e.message)
                }

                return MediatorResult.Success(endOfPaginationReached = endOfPagination)
            }
            is NetworkResource.Error -> {
                Timber.e("errCode: ${networkResource.errCode}, errMsg: ${networkResource.errMessage}")
                return MediatorResult.Error(throwable = Exception("errCode: ${networkResource.errCode}, errMsg: ${networkResource.errMessage}"))
            }
            is NetworkResource.Empty -> {
                Timber.e("Empty")
                return MediatorResult.Success(endOfPaginationReached = true)
            }
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, MovieEntity>): MovieKeyEntity? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        // Get the remote keys of the last item retrieved
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { movie ->
            val searchKey = "${movie.movieId}/${movie.title}/${movie.pagingCategory}"
            dbCallGetMovieKey(searchKey, pagingCategory)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, MovieEntity>): MovieKeyEntity? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { movie ->
            // Get the remote keys of the first items retrieved
            val searchKey = "${movie.movieId}/${movie.title}/${movie.pagingCategory}"
            dbCallGetMovieKey(searchKey, pagingCategory)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, MovieEntity>,
    ): MovieKeyEntity? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.let { movie ->
                val searchKey = "${movie.movieId}/${movie.title}/${movie.pagingCategory}"
                dbCallGetMovieKey(searchKey, pagingCategory)
            }
        }

    }

}