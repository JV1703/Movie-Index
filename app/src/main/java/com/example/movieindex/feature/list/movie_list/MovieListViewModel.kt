package com.example.movieindex.feature.list.movie_list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.movieindex.feature.common.domain.abstraction.MovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val movieUseCase: MovieUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        const val SAVED_LIST_TYPE = "listType"
        const val SAVED_RECOMMENDATION_MOVIE_ID = "recommendationMovieId"
    }

    private val listType = savedStateHandle.getStateFlow(SAVED_LIST_TYPE, ListType.NowPlaying)
    private val recommendationMovieId =
        savedStateHandle.getStateFlow(SAVED_RECOMMENDATION_MOVIE_ID, 0).filter { it != 0 }
    private val ref = combine(listType, recommendationMovieId) { listType, movieId ->
        MovieListViewModelHelper(listType = listType, movieId = movieId)
    }

    val movieList =
        ref.flatMapLatest { ref: MovieListViewModelHelper ->
            when (ref.listType) {
                ListType.NowPlaying -> {
                    movieUseCase.getNowPlayingPagingSource().cachedIn(viewModelScope)
                }
                ListType.Popular -> {
                    movieUseCase.getPopularMoviesPagingSource().cachedIn(viewModelScope)
                }
                ListType.Trending -> {
                    movieUseCase.getTrendingMoviesPagingSource().cachedIn(viewModelScope)
                }
                ListType.Recommendation -> {
                    Timber.i("shit")
                    movieUseCase.getMovieRecommendationPagingSource(movieId = ref.movieId!!)
                        .cachedIn(viewModelScope)
                }
            }
        }

    fun updateListType(listType: ListType) {
        savedStateHandle[SAVED_LIST_TYPE] = listType
    }

    fun saveMovieId(movieId: Int) {
        savedStateHandle[SAVED_RECOMMENDATION_MOVIE_ID] = movieId
    }

    data class MovieListViewModelHelper(val listType: ListType, val movieId: Int? = null)

}

enum class ListType {
    NowPlaying,
    Popular,
    Trending,
    Recommendation
}

enum class PagingLoadStateAdapterType {
    Header, Footer
}