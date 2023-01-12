package com.example.movieindex.feature.list.movie_list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.movieindex.feature.auth.domain.abstraction.AuthUseCase
import com.example.movieindex.feature.common.domain.abstraction.AccountUseCase
import com.example.movieindex.feature.list.movie_list.domain.abstraction.MovieListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val movieListUseCase: MovieListUseCase,
    private val authUseCase: AuthUseCase,
    private val accountUseCase: AccountUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        const val SAVED_LIST_TYPE = "listType"
        const val SAVED_RECOMMENDATION_MOVIE_ID = "recommendationMovieId"
    }

    private val listType = savedStateHandle.getStateFlow(SAVED_LIST_TYPE, ListType.NowPlaying)
    private val recommendationMovieId =
        savedStateHandle.getStateFlow(SAVED_RECOMMENDATION_MOVIE_ID, 0)
    private var accountId =
        accountUseCase.getAccountId()
    private var sessionId = authUseCase.getSessionId()

    private val ref = combine(listType,
        recommendationMovieId,
        accountId,
        sessionId) { listType, movieId, accountId, sessionId ->
        Timber.i("movieListViewModel - ref triggered")
        MovieListViewModelHelper(listType = listType,
            movieId = movieId,
            accountId = accountId,
            sessionId = sessionId)
    }

    val movieList =
        ref.flatMapLatest { ref: MovieListViewModelHelper ->
            Timber.i("movieList called")
            when (ref.listType) {
                ListType.NowPlaying -> {
                    movieListUseCase.getNowPlayingPagingSource()
                }
                ListType.Popular -> {
                    movieListUseCase.getPopularMoviesPagingSource()
                }
                ListType.Trending -> {
                    movieListUseCase.getTrendingMoviesPagingSource()
                }
                ListType.Recommendation -> {
                    movieListUseCase.getMovieRecommendationPagingSource(movieId = ref.movieId!!)
                }
                ListType.Favorite -> {
                    Timber.i("movieListViewModel - accountId: ${ref.accountId}")
                    movieListUseCase.getFavoriteListRemoteMediator(ref.accountId!!, ref.sessionId)
                }
                ListType.Watchlist -> {
                    movieListUseCase.getWatchListRemoteMediator(ref.accountId!!, ref.sessionId)
                }
            }
        }.catch { t -> Timber.e("movieList - errMsg: ${t.message}") }

    fun updateListType(listType: ListType) {
        savedStateHandle[SAVED_LIST_TYPE] = listType
    }

    fun saveMovieId(movieId: Int) {
        savedStateHandle[SAVED_RECOMMENDATION_MOVIE_ID] = movieId
    }

    data class MovieListViewModelHelper(
        val listType: ListType = ListType.NowPlaying,
        val movieId: Int? = null,
        val accountId: Int? = null,
        val sessionId: String = "",
    )

}

enum class ListType {
    NowPlaying,
    Popular,
    Trending,
    Recommendation,
    Favorite,
    Watchlist
}

enum class PagingLoadStateAdapterType {
    Header, Footer
}