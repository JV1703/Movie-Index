package com.example.movieindex.feature.list.movie_list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.bumptech.glide.Glide.init
import com.example.movieindex.feature.auth.domain.abstraction.AuthUseCase
import com.example.movieindex.feature.common.domain.abstraction.MovieUseCase
import com.example.movieindex.feature.main.ui.account.domain.abstraction.AccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val movieUseCase: MovieUseCase,
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
        accountUseCase.getAccountDetailsCache().map {
        Timber.i("movieListViewModel - accountDetails: ${it?.id}")
            it?.id
        }
    private var sessionId = authUseCase.getSessionId()

    val ref = combine(listType,
        recommendationMovieId,
        accountId,
        sessionId) { listType, movieId, accountId, sessionId ->
        Timber.i("movieListViewModel - ref triggered")
        MovieListViewModelHelper(listType = listType,
            movieId = movieId,
            accountId = accountId,
            sessionId = sessionId)
//        MovieListViewModelHelper(listType = listType,
//            movieId = movieId,
//            accountId = accountId,
//            sessionId = sessionId)
    }

//    init {
//        combine(listType,
//            recommendationMovieId,
//            accountId,
//            sessionId) { listType, movieId, accountId, sessionId ->
//            Timber.i("movieListViewModel - ref triggered")
//            _helperState.update { it.copy(
//                listType = listType,
//                movieId = movieId,
//                accountId = accountId,
//                sessionId = sessionId) }
////        MovieListViewModelHelper(listType = listType,
////            movieId = movieId,
////            accountId = accountId,
////            sessionId = sessionId)
//        }.launchIn(viewModelScope)
//    }

    private val _helperState = MutableStateFlow(MovieListViewModelHelper())
    val movieList =
        ref.flatMapLatest { ref: MovieListViewModelHelper ->
            when (ref.listType) {
                ListType.NowPlaying -> {
                    movieUseCase.getNowPlayingPagingSource()
                }
                ListType.Popular -> {
                    movieUseCase.getPopularMoviesPagingSource()
                }
                ListType.Trending -> {
                    movieUseCase.getTrendingMoviesPagingSource()
                }
                ListType.Recommendation -> {
                    movieUseCase.getMovieRecommendationPagingSource(movieId = ref.movieId!!)
                }
                ListType.Favorite -> {
                    Timber.i("movieListViewModel - accountId: ${ref.accountId}")
                    movieUseCase.getFavoriteListRemoteMediator(ref.accountId!!, ref.sessionId)
                }
                ListType.Watchlist -> {
                    movieUseCase.getWatchListRemoteMediator(ref.accountId!!, ref.sessionId)
                }
            }
        }.cachedIn(viewModelScope).catch { t -> Timber.e("movieList - errMsg: ${t.message}") }

    fun updateListType(listType: ListType) {
        savedStateHandle[SAVED_LIST_TYPE] = listType
    }

    fun saveMovieId(movieId: Int) {
        savedStateHandle[SAVED_RECOMMENDATION_MOVIE_ID] = movieId
    }

    data class MovieListViewModelHelper(
        val listType: ListType = ListType.NowPlaying,
        val movieId: Int? = null ,
        val accountId: Int? = null,
        val sessionId: String= "",
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