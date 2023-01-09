package com.example.movieindex.feature.detail.movie

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieindex.core.common.RvListHelper
import com.example.movieindex.core.data.external.*
import com.example.movieindex.feature.auth.domain.abstraction.AuthUseCase
import com.example.movieindex.feature.common.domain.abstraction.MovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MovieDetailViewModelAlt @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val movieUseCase: MovieUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        const val SAVED_MOVIE_ID = "movieId"
    }

    private val movieId = savedStateHandle.getStateFlow(SAVED_MOVIE_ID, 0)
    private var sessionId: String? = null
    private var accountId: Int? = null

    init {
        viewModelScope.launch {
            sessionId = authUseCase.getSessionId().first()
            accountId = movieUseCase.getAccountId().first()
        }
    }

    private val cachedMovie =
        movieId.filter { it != 0 }.flatMapLatest { movieUseCase.getCachedMovie(it) }

    private val movieDetails =
        movieId.filter { it != 0 }.flatMapLatest { movieUseCase.getMovieDetails(it) }

    private val _uiState: MutableStateFlow<MovieDetailUiState> =
        MutableStateFlow(MovieDetailUiState())
    val uiState =
        combine(
            movieDetails,
            _uiState) { resource: Resource<MovieDetails>, uiState: MovieDetailUiState ->

            when (resource) {
                is Resource.Loading -> {
                    uiState
                }
                is Resource.Success -> {
                    _fabState.update { fabState ->
                        fabState.copy(isFavorite = resource.data.isFavorite,
                            isBookmarked = resource.data.isBookmark)
                    }
                    uiState.copy(movieDetails = resource.data)
                }
                is Resource.Empty -> {
                    uiState.copy(userMsg = "No Data Available")
                }
                is Resource.Error -> {
                    uiState.copy(userMsg = resource.errMsg)
                }
            }

        }.stateIn(scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MovieDetailUiState())

    private val _fabState = MutableStateFlow(MovieDetailsFabState())
    val fabState = combine(cachedMovie, _fabState) { movie, fabState ->
        fabState.copy(isFavorite = movie?.isFavorite ?: false,
            isBookmarked = movie?.isBookmark ?: false)
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
        _uiState.update { uiState -> uiState.copy(isLoadingGeneral = isLoading) }
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

    fun addToFavorite(
        favorite: Boolean,
        mediaId: Int,
        movieDetails: MovieDetails,
        mediaType: String = "movie",
    ) {
        viewModelScope.launch {

            movieUseCase.insertMovie(movieDetails = movieDetails)

            movieUseCase.addToFavorite(favorite = favorite,
                mediaId = mediaId,
                mediaType = mediaType)

        }
    }

    fun addToWatchList(
        watchlist: Boolean,
        mediaId: Int,
        movieDetails: MovieDetails,
        mediaType: String = "movie",
    ) {
        viewModelScope.launch {

            movieUseCase.insertMovie(movieDetails = movieDetails)

            movieUseCase.addToWatchList(watchlist = watchlist,
                mediaId = mediaId,
                mediaType = mediaType)

        }
    }

    data class MovieDetailUiState(
        val isLoadingGeneral: Boolean = true,
        val movieDetails: MovieDetails? = null,
        val userMsg: String? = null,
    )

    data class MovieDetailsFabState(
        val isOpen: Boolean = false,
        val isBookmarked: Boolean = false,
        val isFavorite: Boolean = false,
        val userMsg: String? = null,
    )

}