package com.example.movieindex.core.repository.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.movieindex.core.data.local.model.MovieEntityKey
import com.example.movieindex.core.data.local.model.MoviePagingCategory
import com.example.movieindex.core.data.local.model.MoviePagingEntity
import com.example.movieindex.core.data.remote.NetworkResource
import com.example.movieindex.core.data.remote.model.common.MoviesResponse
import com.example.movieindex.core.data.remote.model.common.toMoviePagingEntity
import timber.log.Timber

@OptIn(ExperimentalPagingApi::class)
class MoviesPagingRemoteMediator(
    private val loadSinglePage: Boolean,
    private val networkCall: suspend (page: Int) -> NetworkResource<MoviesResponse>,
    private val pagingCategory: MoviePagingCategory,
    private val dbCallGetMovieKey: suspend (id: String) -> MovieEntityKey?,
    private val dbCallOnRefreshClearDb: suspend (pagingCategory: MoviePagingCategory) -> Unit,
    private val dbCallOnSuccess: suspend (movieKeys: List<MovieEntityKey>, movies: List<MoviePagingEntity>) -> Unit,
) : RemoteMediator<Int, MoviePagingEntity>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, MoviePagingEntity>,
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
                    page == networkResource.data.total_pages || networkResource.data.total_pages == 0 || loadSinglePage
                if (loadType == LoadType.REFRESH) {
                    dbCallOnRefreshClearDb(pagingCategory)
                }

                val prevKey = if (page == INITIAL_PAGE) null else page - 1
                val nextKey = if (endOfPagination) null else page + 1

                val movieEntity = movies.map {
                    it.toMoviePagingEntity(pagingCategory)
                }
                val keys = movieEntity.map {
                    MovieEntityKey(
                        id = "${it.movieId}/${it.pagingCategory}",
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
                Timber.e("errCode: ${networkResource.errCode}, errMsg: ${networkResource.errMsg}")
                return MediatorResult.Error(throwable = Exception("errCode: ${networkResource.errCode}, errMsg: ${networkResource.errMsg}"))
            }
            is NetworkResource.Empty -> {
                Timber.e("Empty")
                return MediatorResult.Success(endOfPaginationReached = true)
            }
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, MoviePagingEntity>): MovieEntityKey? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        // Get the remote keys of the last item retrieved
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { movie ->
            val searchKey = movie.id
            dbCallGetMovieKey(searchKey)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, MoviePagingEntity>): MovieEntityKey? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { movie ->
            // Get the remote keys of the first items retrieved
            val searchKey = movie.id
            dbCallGetMovieKey(searchKey)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, MoviePagingEntity>,
    ): MovieEntityKey? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.let { movie ->
                val searchKey = /*"${movie.movieId}/${movie.pagingCategory}"*/ movie.id
                dbCallGetMovieKey(searchKey)
            }
        }

    }

}