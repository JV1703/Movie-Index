package com.example.movieindex.feature.detail.movie

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieindex.core.common.RvListHelper
import com.example.movieindex.core.data.external.model.*
import com.example.movieindex.feature.auth.domain.abstraction.AuthUseCase
import com.example.movieindex.feature.common.domain.abstraction.AccountUseCase
import com.example.movieindex.feature.detail.movie.domain.abstraction.MovieDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val movieDetailsUseCase: MovieDetailsUseCase,
    private val authUseCase: AuthUseCase,
    private val accountUseCase: AccountUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        const val SAVED_MOVIE_ID = "movieId"
    }

    private val movieId = savedStateHandle.getStateFlow(SAVED_MOVIE_ID, 0)

    private val _uiState: MutableStateFlow<MovieDetailUiState> =
        MutableStateFlow(MovieDetailUiState())
    val uiState = _uiState.asStateFlow()

    private var sessionId: String? = null
    private var accountId: Int? = null

    private val movieDetails =
        movieId.filter { it != 0 }.map { movieDetailsUseCase.getMovieDetails(it) }


    init {
        authUseCase.getSessionId().onEach { sessionId = it }.launchIn(viewModelScope)
        accountUseCase.getAccountId().onEach { accountId = it }.launchIn(viewModelScope)

        val movieAccountState =
            combine(movieId, authUseCase.getSessionId()) { movieId, sessionId ->
                movieDetailsUseCase.getMovieAccountState(movieId = movieId, sessionId = sessionId)
            }

        movieDetails.zip(movieAccountState) { movieDetails, accountState ->

            when (movieDetails) {
                is Resource.Success -> {
                    when (accountState) {
                        is Resource.Success -> {
                            val accountStateData = accountState.data

                            _uiState.update {
                                it.copy(
                                    movieDetails = movieDetails.data,
                                    isFavorite = accountStateData.favorite,
                                    isBookmarked = accountStateData.watchlist
                                )
                            }
                        }
                        is Resource.Error -> {
                            _uiState.update {
                                it.copy(
                                    userMsg = accountState.errMsg
                                )
                            }
                        }
                        is Resource.Empty -> {
                            _uiState.update {
                                it.copy(
                                    userMsg = "Empty Response"
                                )
                            }
                        }
                    }
                }
                is Resource.Error -> {

                    _uiState.update { it.copy(userMsg = movieDetails.errMsg) }

                }
                is Resource.Empty -> {

                    _uiState.update { it.copy(userMsg = "Empty Response") }

                }
            }
        }.launchIn(viewModelScope)
    }

    fun saveMovieId(movieId: Int) {
        savedStateHandle[SAVED_MOVIE_ID] = movieId
    }

    fun saveCasts(casts: List<Cast>) {
        viewModelScope.launch {
            movieDetailsUseCase.saveCasts(casts)
        }
    }

    fun saveCrews(crews: List<Crew>) {
        viewModelScope.launch {
            movieDetailsUseCase.saveCrews(crews)
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

    fun updateBookmark(movieId: Int, isBookmarked: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true) }
            val resource = movieDetailsUseCase.addToWatchList(
                sessionId = sessionId!!,
                accountId = accountId!!,
                watchlist = isBookmarked,
                mediaId = movieId)

            when (resource) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isUpdating = false, isBookmarked = isBookmarked) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isUpdating = false, userMsg = resource.errMsg) }
                }
                is Resource.Empty -> {
                    _uiState.update { it.copy(isUpdating = false, userMsg = "Empty Response") }
                }
            }
        }
    }

    fun updateFavorite(movieId: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true) }
            val resource = movieDetailsUseCase.addToFavorite(
                sessionId = sessionId!!,
                accountId = accountId!!,
                favorite = isFavorite,
                mediaId = movieId
            )

            when (resource) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isUpdating = false, isFavorite = isFavorite) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isUpdating = false, userMsg = resource.errMsg) }
                }
                is Resource.Empty -> {
                    _uiState.update { it.copy(isUpdating = false, userMsg = "Empty Response") }
                }
            }
        }
    }

    data class MovieDetailUiState(
        val isLoading: Boolean = true,
        val isUpdating: Boolean = false,
        val movieDetails: MovieDetails? = null,
        val isFavorite: Boolean = false,
        val isBookmarked: Boolean = false,
        val userMsg: String? = null,
    )
}