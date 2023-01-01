package com.example.movieindex.core.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.movieindex.core.data.remote.NetworkResource
import com.example.movieindex.core.data.remote.model.common.MoviesResponse
import com.example.movieindex.core.data.remote.model.common.ResultResponse

const val INITIAL_PAGE = 1

class MoviesPagingSource(
    private val loadSinglePage: Boolean,
    private val networkCall: suspend (page: Int) -> NetworkResource<MoviesResponse>,
) : PagingSource<Int, ResultResponse>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ResultResponse> {
        val page = params.key ?: INITIAL_PAGE

        val response = networkCall(page)
        val prevKey = if (page == INITIAL_PAGE) null else page - 1

        return when (response) {
            is NetworkResource.Empty -> {
                LoadResult.Error(NullPointerException("No Data Received"))
            }
            is NetworkResource.Success -> {
                val data = response.data.results
                LoadResult.Page(
                    data = data,
                    prevKey = prevKey,
                    nextKey = if (loadSinglePage || data.isEmpty() || response.data.total_pages == page || response.data.total_pages == 0) null else page + 1
                )
            }
            is NetworkResource.Error -> {
                LoadResult.Error(Exception("errCode: ${response.errCode}, errMsg: ${response.errMessage}"))
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ResultResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}