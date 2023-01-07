package com.example.movieindex.feature.main.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.movieindex.feature.common.domain.abstraction.MovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val movieUseCase: MovieUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        const val SAVED_SEARCH_QUERY = "search_query"
    }

    val searchQuery = savedStateHandle.getStateFlow(SAVED_SEARCH_QUERY, "")

    val searchResult =
        searchQuery.debounce(1000).distinctUntilChanged()
            .filter { query -> query.trim().isNotEmpty() }.flatMapLatest {
                movieUseCase.searchMoviesPagingSource(query = it)
            }.cachedIn(viewModelScope)

    fun updateSearchQuery(query: String) {
        savedStateHandle[SAVED_SEARCH_QUERY] = query
    }

}