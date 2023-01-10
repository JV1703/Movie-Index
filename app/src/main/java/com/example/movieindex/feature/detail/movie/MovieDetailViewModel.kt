package com.example.movieindex.feature.detail.movie

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieindex.core.common.RvListHelper
import com.example.movieindex.core.data.external.*
import com.example.movieindex.core.data.external.model.*
import com.example.movieindex.feature.common.domain.abstraction.MovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val movieUseCase: MovieUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        const val SAVED_MOVIE_ID = "movieId"
    }

    private val movieId = savedStateHandle.getStateFlow(SAVED_MOVIE_ID, 0)


    private val _uiState: MutableStateFlow<MovieDetailUiState> =
        MutableStateFlow(MovieDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val cachedMovie =
            movieId.filter { it != 0 }.flatMapLatest { movieUseCase.getCachedMovie(it) }

        val movieDetails =
            movieId.filter { it != 0 }.flatMapLatest { movieUseCase.getMovieDetails(it) }

        combine(cachedMovie, movieDetails) { cache, resource ->
            when (resource) {
                is Resource.Loading -> {
                    _uiState.value = MovieDetailUiState(isLoading = true)
                }
                is Resource.Success -> {
                    _uiState.value = MovieDetailUiState(
                        isLoading = false,
                        movieDetails = resource.data,
                        cachedMovie = cache
                    )
                }
                is Resource.Empty -> {
                    _uiState.value =
                        MovieDetailUiState(isLoading = false, userMsg = "No Data Available")
                }
                is Resource.Error -> {
                    _uiState.value =
                        MovieDetailUiState(isLoading = false, userMsg = resource.errMsg)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun saveMovieId(movieId: Int) {
        savedStateHandle[SAVED_MOVIE_ID] = movieId
    }

    fun saveCasts(casts: List<Cast>) {
        viewModelScope.launch {
            movieUseCase.saveCasts(casts)
        }
    }

    fun saveCrews(crews: List<Crew>) {
        viewModelScope.launch {
            movieUseCase.saveCrews(crews)
        }
    }

    fun updatePosterLoadingStatus(isLoading: Boolean) {
        _uiState.update { uiState -> uiState.copy(isLoading = isLoading) }
    }

    suspend fun generateCastList(
        data: List<Cast>,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
    ): List<RvListHelper<Cast>> {

        return withContext(dispatcher) {
            val casts = arrayListOf<RvListHelper<Cast>>()

            if (data.size >= 10) {
                val convertedList = data.take(9).map { RvListHelper.DataWrapper(it) }
                casts.addAll(convertedList + RvListHelper.ViewMore)
            } else {
                val convertedList = data.map { RvListHelper.DataWrapper(it) }
                casts.addAll(convertedList)
            }
            casts
        }

    }

    suspend fun generateRecommendationList(
        data: List<Result>,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
    ): List<RvListHelper<Result>> {
        return withContext(dispatcher) {
            val recommendations = arrayListOf<RvListHelper<Result>>()

            if (data.size >= 10) {
                val convertedList =
                    data.take(9).map { RvListHelper.DataWrapper(it) }
                recommendations.addAll(convertedList + RvListHelper.ViewMore)
            } else {
                val convertedList = data.map { RvListHelper.DataWrapper(it) }
                recommendations.addAll(convertedList)
            }
            recommendations
        }

    }

    fun showMsg(msg: String) {
        _uiState.update { uiState -> uiState.copy(userMsg = msg) }
    }

    fun msgShown() {
        _uiState.update { uiState -> uiState.copy(userMsg = null) }
    }

    fun insertMovie(
        mediaId: Int,
        movieDetails: MovieDetails,
        mediaType: String = "movie",
        isFavorite: Boolean = false,
        isBookmarked: Boolean = false,
    ) {
        viewModelScope.launch {
            Timber.i("cachedMovie - insertMovie triggered")
            movieUseCase.insertMovieToCache(movieDetails = movieDetails,
                isFavorite = isFavorite,
                isBookmarked = isBookmarked)

            if (isFavorite) {
                movieUseCase.addToFavorite(favorite = true,
                    mediaId = mediaId,
                    mediaType = mediaType)
            }

            if (isBookmarked) {
                movieUseCase.addToWatchList(watchlist = true,
                    mediaId = mediaId,
                    mediaType = mediaType)
            }
        }
    }

    fun updateBookmark(movieId: Int, isBookmarked: Boolean, isFavorite: Boolean){
        viewModelScope.launch {
            if(!isBookmarked && !isFavorite){
               movieUseCase.deleteSavedMovieCache(movieId)
            }else{
                movieUseCase.updateBookmarkCache(movieId = movieId, isBookmarked = isBookmarked)
            }
            movieUseCase.addToWatchList(watchlist = isBookmarked, mediaId = movieId)
        }
    }

    fun updateFavorite(movieId: Int, isBookmarked: Boolean, isFavorite: Boolean){
        viewModelScope.launch {
            if(!isBookmarked && !isFavorite){
                movieUseCase.deleteSavedMovieCache(movieId)
            }else{
                movieUseCase.updateFavoriteCache(movieId = movieId, isFavorite = isFavorite)
            }

            movieUseCase.addToFavorite(favorite = isFavorite, mediaId = movieId)
        }
    }

    data class MovieDetailUiState(
        val isLoading: Boolean = true,
        val movieDetails: MovieDetails? = null,
        val cachedMovie: SavedMovie? = null,
        val userMsg: String? = null,
    )
}