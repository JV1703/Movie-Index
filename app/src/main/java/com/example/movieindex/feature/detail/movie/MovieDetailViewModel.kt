//package com.example.movieindex.feature.detail.movie
//
//import androidx.lifecycle.SavedStateHandle
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.movieindex.core.common.RvListHelper
//import com.example.movieindex.core.data.external.*
//import com.example.movieindex.feature.common.domain.abstraction.MovieUseCase
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.*
//import kotlinx.coroutines.flow.*
//import javax.inject.Inject
//
//@OptIn(ExperimentalCoroutinesApi::class)
//@HiltViewModel
//class MovieDetailViewModel @Inject constructor(
//    private val movieUseCase: MovieUseCase,
//    private val savedStateHandle: SavedStateHandle,
//) : ViewModel() {
//
//    companion object {
//        const val SAVED_MOVIE_ID = "movieId"
//    }
//
//    private val _notificationEvents = MutableSharedFlow<MovieDetailEvents>()
//    val notificationEvents = _notificationEvents.asSharedFlow()
//
//    private val isPosterLoading = MutableStateFlow(true)
//
//    private val movieId = savedStateHandle.getStateFlow(SAVED_MOVIE_ID, 0)
//    private val movieDetails =
//        movieId.filter { it != 0 }.flatMapLatest { movieUseCase.getMovieDetails(it) }
//
//    val uiState: StateFlow<MovieDetailUiState> =
//        combine(this.isPosterLoading, movieDetails) { isLoading, movieDetails ->
//            when (movieDetails) {
//                is Resource.Loading -> {
//                    MovieDetailUiState.Loading
//                }
//                is Resource.Success -> {
//                    MovieDetailUiState.Success(isLoading = isLoading,
//                        movieDetails = movieDetails.data)
//                }
//                is Resource.Empty -> {
//                    _notificationEvents.emit(MovieDetailEvents.EmptyData(msg = "No data available"))
//                    MovieDetailUiState.Error
//                }
//                is Resource.Error -> {
//                    _notificationEvents.emit(MovieDetailEvents.LoadError(msg = "errCode: ${movieDetails.errCode}, errMsg: ${movieDetails.errMsg}"))
//                    MovieDetailUiState.Error
//                }
//            }
//        }.stateIn(scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(5000),
//            initialValue = MovieDetailUiState.Loading)
//
//    fun saveMovieId(movieId: Int) {
//        savedStateHandle[SAVED_MOVIE_ID] = movieId
//    }
//
//    fun saveCasts(casts: List<Cast>) {
//        viewModelScope.launch {
//            movieUseCase.saveCasts(casts)
//        }
//    }
//
//    fun saveCrews(crews: List<Crew>) {
//        viewModelScope.launch {
//            movieUseCase.saveCrews(crews)
//        }
//    }
//
//    fun updatePosterLoadingStatus(isLoading: Boolean) {
//        this.isPosterLoading.value = isLoading
//    }
//
//    suspend fun generateCastList(
//        data: List<Cast>,
//        dispatcher: CoroutineDispatcher = Dispatchers.Default,
//    ): List<RvListHelper<Cast>> {
//
//        return withContext(dispatcher) {
//            val casts = arrayListOf<RvListHelper<Cast>>()
//
//            if (data.size >= 10) {
//                val convertedList = data.take(9).map { RvListHelper.DataWrapper(it) }
//                casts.addAll(convertedList + RvListHelper.ViewMore)
//            } else {
//                val convertedList = data.map { RvListHelper.DataWrapper(it) }
//                casts.addAll(convertedList)
//            }
//            casts
//        }
//
//    }
//
//    suspend fun generateRecommendationList(
//        data: List<Result>,
//        dispatcher: CoroutineDispatcher = Dispatchers.Default,
//    ): List<RvListHelper<Result>> {
//        return withContext(dispatcher) {
//            val recommendations = arrayListOf<RvListHelper<Result>>()
//
//            if (data.size >= 10) {
//                val convertedList =
//                    data.take(9).map { RvListHelper.DataWrapper(it) }
//                recommendations.addAll(convertedList + RvListHelper.ViewMore)
//            } else {
//                val convertedList = data.map { RvListHelper.DataWrapper(it) }
//                recommendations.addAll(convertedList)
//            }
//            recommendations
//        }
//
//    }
//
//    sealed interface MovieDetailUiState {
//        data class Success(
//            val isLoading: Boolean = true,
//            val movieDetails: MovieDetails,
//            val isBookmarked: Boolean = false,
//            val isFabOpen: Boolean = false,
//            val isFavorite: Boolean = false,
//            val isAddedToPlaylist: Boolean = false,
//        ) : MovieDetailUiState
//
//        object Error : MovieDetailUiState
//        object Loading : MovieDetailUiState
//    }
//
//    sealed interface MovieDetailEvents {
//        data class EmptyData(val msg: String) : MovieDetailEvents
//        data class LoadError(val msg: String) : MovieDetailEvents
//    }
//
//}