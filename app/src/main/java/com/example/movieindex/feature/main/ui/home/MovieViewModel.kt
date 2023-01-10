package com.example.movieindex.feature.main.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieindex.core.data.external.model.Resource
import com.example.movieindex.core.data.external.model.Result
import com.example.movieindex.feature.common.domain.abstraction.MovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(private val movieUseCase: MovieUseCase) : ViewModel() {

    private val _nowPlayingUiState = MutableStateFlow(NowPlayingMoviesState())
    val nowPlayingUiState = _nowPlayingUiState.asStateFlow()

    private val _popularMoviesUiState = MutableStateFlow(PopularMoviesState())
    val popularMoviesUiState = _popularMoviesUiState.asStateFlow()

    private val _trendingMoviesUiState = MutableStateFlow(TrendingMoviesState())
    val trendingMoviesUisState = _trendingMoviesUiState.asStateFlow()

    private var nowPlayingJob: Job? = null
    private var popularMoviesJob: Job? = null
    private var trendingMoviesJob: Job? = null

    init {
        getNowPlaying()
        getPopularMovies()
        getTrendingMovies()
    }

    fun refreshMovies() {
        getNowPlaying()
        getPopularMovies()
        getTrendingMovies()
    }

    private fun getNowPlaying(
        page: Int = 1,
        language: String? = null,
        region: String? = null,
    ) {

        if (nowPlayingJob != null) return
        nowPlayingJob = movieUseCase.getNowPlaying(
            page = page,
            language = language,
            region = region).map { resource ->
            when (resource) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    _nowPlayingUiState.update { it.copy(movies = resource.data) }
                    nowPlayingJob = null
                }
                is Resource.Error -> {
                    _nowPlayingUiState.update { it.copy(userMsg = resource.errMsg) }
                    nowPlayingJob = null
                }
                is Resource.Empty -> {
                    _nowPlayingUiState.update { it.copy(userMsg = "No data available") }
                    nowPlayingJob = null
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getPopularMovies(
        page: Int = 1,
        language: String? = null,
        region: String? = null,
    ) {
        if (popularMoviesJob != null) return

        popularMoviesJob = movieUseCase.getPopularMovies(
            page = page,
            language = language,
            region = region).map { resource ->
            when (resource) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    _popularMoviesUiState.update { it.copy(movies = resource.data) }
                    popularMoviesJob = null
                }
                is Resource.Error -> {
                    _popularMoviesUiState.update { it.copy(userMsg = resource.errMsg) }
                    popularMoviesJob = null
                }
                is Resource.Empty -> {
                    _popularMoviesUiState.update { it.copy(userMsg = "No data available") }
                    popularMoviesJob = null
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getTrendingMovies(
        page: Int = 1, mediaType: String = "movie",
        timeWindow: String = "week",
    ) {

        if (trendingMoviesJob != null) return
        trendingMoviesJob = movieUseCase.getTrendingMovies(
            page = page,
            mediaType = mediaType,
            timeWindow = timeWindow).map { resource ->
            when (resource) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    _trendingMoviesUiState.update { it.copy(movies = resource.data) }
                    trendingMoviesJob = null
                }
                is Resource.Error -> {
                    _trendingMoviesUiState.update { it.copy(userMsg = resource.errMsg) }
                    trendingMoviesJob = null
                }
                is Resource.Empty -> {
                    _trendingMoviesUiState.update { it.copy(userMsg = "No data available") }
                    trendingMoviesJob = null
                }
            }
        }.launchIn(viewModelScope)
    }

    fun nowPlayingMoviesUserMsgShown() {
        _nowPlayingUiState.update { it.copy(userMsg = null) }
    }

    fun popularMoviesUserMsgShown() {
        _popularMoviesUiState.update { it.copy(userMsg = null) }
    }

    fun trendingMoviesUserMsgShown() {
        _trendingMoviesUiState.update { it.copy(userMsg = null) }
    }

    data class NowPlayingMoviesState(
        val isLoading: Boolean = true,
        val movies: List<Result> = emptyList(),
        val userMsg: String? = null,
    )

    data class PopularMoviesState(
        val isLoading: Boolean = true,
        val movies: List<Result> = emptyList(),
        val userMsg: String? = null,
    )

    data class TrendingMoviesState(
        val isLoading: Boolean = true,
        val movies: List<Result> = emptyList(),
        val userMsg: String? = null,
    )

}