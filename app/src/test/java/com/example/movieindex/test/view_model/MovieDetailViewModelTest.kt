package com.example.movieindex.test.view_model

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import com.example.movieindex.core.common.RvListHelper
import com.example.movieindex.core.data.remote.model.details.toMovieDetails
import com.example.movieindex.fake.datastore.FakePreferenceDataStore
import com.example.movieindex.fake.use_case.FakeAccountUseCase
import com.example.movieindex.fake.use_case.FakeAuthUseCase
import com.example.movieindex.fake.use_case.FakeCreditListUseCase
import com.example.movieindex.fake.use_case.FakeMovieDetailsUseCase
import com.example.movieindex.feature.detail.movie.MovieDetailViewModel
import com.example.movieindex.util.MainCoroutineRule
import com.example.movieindex.util.TestDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.random.Random.Default.nextBoolean

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class MovieDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainCoroutineRule()

    private lateinit var testDataFactory: TestDataFactory
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var testScope: TestScope
    private lateinit var movieDetailsUseCase: FakeMovieDetailsUseCase
    private lateinit var authUseCase: FakeAuthUseCase
    private lateinit var accountUseCase: FakeAccountUseCase
    private lateinit var creditListUseCase: FakeCreditListUseCase
    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var viewModel: MovieDetailViewModel

    @Before
    fun setup() {
        testScope = TestScope()
        testDataFactory = TestDataFactory()
        dataStore =
            FakePreferenceDataStore(testContext = ApplicationProvider.getApplicationContext(),
                testCoroutineScope = testScope).testDataStore
        savedStateHandle = SavedStateHandle()
        movieDetailsUseCase =
            FakeMovieDetailsUseCase(mainDispatcherRule.testDispatcher, dataStore, testDataFactory)
        authUseCase = FakeAuthUseCase(mainDispatcherRule.testDispatcher, dataStore, testDataFactory)
        accountUseCase =
            FakeAccountUseCase(mainDispatcherRule.testDispatcher, dataStore, testDataFactory)
        creditListUseCase =
            FakeCreditListUseCase(mainDispatcherRule.testDispatcher, dataStore, testDataFactory)
        viewModel =
            MovieDetailViewModel(
                movieDetailsUseCase = movieDetailsUseCase,
                authUseCase = authUseCase,
                accountUseCase = accountUseCase,
                savedStateHandle = savedStateHandle)

    }

    @Test
    fun saveCasts() = runTest {
        val casts = testDataFactory.generateMovieDetailsTestData().toMovieDetails().casts
        viewModel.saveCasts(casts)
        advanceUntilIdle()
        val cachedData = creditListUseCase.getCasts().first()
        assertEquals(casts, cachedData)
    }

    @Test
    fun saveCrews() = runTest {
        val crews = testDataFactory.generateMovieDetailsTestData().toMovieDetails().crews
        viewModel.saveCrews(crews)
        advanceUntilIdle()
        val cachedData = creditListUseCase.getCrews().first()
        assertEquals(crews, cachedData)
    }

    @Test
    fun generateCastList_castSizeIsLargerThan10() = runTest {
        val casts = testDataFactory.generateMovieDetailsTestData().toMovieDetails().casts
        val generatedCastList =
            viewModel.generateCastList(data = casts, dispatcher = mainDispatcherRule.testDispatcher)

        assertEquals(10, generatedCastList.size)

        val normalCastList = generatedCastList.take(9)
        normalCastList.all { it is RvListHelper.DataWrapper }

        val viewMore = generatedCastList.last()
        assertTrue(viewMore is RvListHelper.ViewMore)
    }

    @Test
    fun generateCastList_castSizeIsLessThan10() = runTest {
        val casts = testDataFactory.generateMovieDetailsTestData().toMovieDetails().casts.take(8)
        val generatedCastList =
            viewModel.generateCastList(data = casts, dispatcher = mainDispatcherRule.testDispatcher)
        val castsSize = casts.size

        assertEquals(castsSize, generatedCastList.size)

        generatedCastList.all { it is RvListHelper.DataWrapper }
    }

    @Test
    fun generateRecommendationList_castSizeIsLargerThan10() = runTest {
        val recommendations =
            testDataFactory.generateMovieDetailsTestData().toMovieDetails().recommendations!!
        val generatedCastList =
            viewModel.generateRecommendationList(data = recommendations,
                dispatcher = mainDispatcherRule.testDispatcher)

        assertEquals(10, generatedCastList.size)

        val normalCastList = generatedCastList.take(9)
        normalCastList.all { it is RvListHelper.DataWrapper }

        val viewMore = generatedCastList.last()
        assertTrue(viewMore is RvListHelper.ViewMore)
    }

    @Test
    fun generateRecommendationList_castSizeIsLessThan10() = runTest {
        val recommendations = testDataFactory.generateMovieDetailsTestData()
            .toMovieDetails().recommendations!!.take(8)
        val generatedCastList =
            viewModel.generateRecommendationList(data = recommendations,
                dispatcher = mainDispatcherRule.testDispatcher)
        val castsSize = recommendations.size

        assertEquals(castsSize, generatedCastList.size)

        generatedCastList.all { it is RvListHelper.DataWrapper }
    }

    @Test
    fun uiState() = runTest {
        val uiStateList = arrayListOf<MovieDetailViewModel.MovieDetailUiState>()
        val job = launch(mainDispatcherRule.testDispatcher) {
            viewModel.uiState.toList(uiStateList)
        }

        authUseCase.login(username = "", password = "")

        // viewModel flow filters if movieId != 0
        // thus need to update savedMovieId so that flow will emit data
        savedStateHandle[MovieDetailViewModel.SAVED_MOVIE_ID] = 1

        assertNotNull(uiStateList.last().movieDetails)
        // default uiState sets isFavorite and isBookmarked as false
        // generated fake data will update both isFavorite and isBookmarked to true
        assertTrue(uiStateList.last().isFavorite)
        assertTrue(uiStateList.last().isBookmarked)

        job.cancel()
    }

    @Test
    fun updateBookmark_success() = runTest {
        authUseCase.login(username = "", password = "")
        val uiStateList = arrayListOf<MovieDetailViewModel.MovieDetailUiState>()
        val job = launch(mainDispatcherRule.testDispatcher) {
            viewModel.uiState.toList(uiStateList)
        }

        val isBookmarked = nextBoolean()
        viewModel.updateBookmark(movieId = 0, isBookmarked = isBookmarked)

        assertTrue(uiStateList[1].isUpdating)
        assertFalse(uiStateList.last().isUpdating)
        assertEquals(isBookmarked, uiStateList.last().isBookmarked)
        assertNull(uiStateList.last().userMsg)

        job.cancel()

    }

    @Test
    fun updateBookmark_empty() = runTest {

        movieDetailsUseCase.isBodyEmpty = true

        authUseCase.login(username = "", password = "")
        val uiStateList = arrayListOf<MovieDetailViewModel.MovieDetailUiState>()
        val job = launch(mainDispatcherRule.testDispatcher) {
            viewModel.uiState.toList(uiStateList)
        }

        val isBookmarked = nextBoolean()
        viewModel.updateBookmark(movieId = 0, isBookmarked = isBookmarked)

        assertTrue(uiStateList[1].isUpdating)
        assertFalse(uiStateList.last().isUpdating)
        assertNotNull(uiStateList.last().userMsg)

        job.cancel()

    }

    @Test
    fun updateBookmark_error() = runTest {

        movieDetailsUseCase.isSuccess = false

        authUseCase.login(username = "", password = "")
        val uiStateList = arrayListOf<MovieDetailViewModel.MovieDetailUiState>()
        val job = launch(mainDispatcherRule.testDispatcher) {
            viewModel.uiState.toList(uiStateList)
        }

        val isBookmarked = nextBoolean()
        viewModel.updateBookmark(movieId = 0, isBookmarked = isBookmarked)

        assertTrue(uiStateList[1].isUpdating)
        assertFalse(uiStateList.last().isUpdating)
        assertNotNull(uiStateList.last().userMsg)

        job.cancel()

    }

    @Test
    fun updateFavorite_success() = runTest {
        authUseCase.login(username = "", password = "")
        val uiStateList = arrayListOf<MovieDetailViewModel.MovieDetailUiState>()
        val job = launch(mainDispatcherRule.testDispatcher) {
            viewModel.uiState.toList(uiStateList)
        }

        val isFavorite = nextBoolean()
        viewModel.updateFavorite(movieId = 0, isFavorite = isFavorite)

        assertTrue(uiStateList[1].isUpdating)
        assertFalse(uiStateList.last().isUpdating)
        assertEquals(isFavorite, uiStateList.last().isFavorite)
        assertNull(uiStateList.last().userMsg)

        job.cancel()

    }

    @Test
    fun updateFavorite_empty() = runTest {

        movieDetailsUseCase.isBodyEmpty = true

        authUseCase.login(username = "", password = "")
        val uiStateList = arrayListOf<MovieDetailViewModel.MovieDetailUiState>()
        val job = launch(mainDispatcherRule.testDispatcher) {
            viewModel.uiState.toList(uiStateList)
        }

        val isFavorite = nextBoolean()
        viewModel.updateFavorite(movieId = 0, isFavorite = isFavorite)

        assertTrue(uiStateList[1].isUpdating)
        assertFalse(uiStateList.last().isUpdating)
        assertNotNull(uiStateList.last().userMsg)

        job.cancel()

    }

    @Test
    fun updateFavorite_error() = runTest {

        movieDetailsUseCase.isSuccess = false

        authUseCase.login(username = "", password = "")
        val uiStateList = arrayListOf<MovieDetailViewModel.MovieDetailUiState>()
        val job = launch(mainDispatcherRule.testDispatcher) {
            viewModel.uiState.toList(uiStateList)
        }

        val isFavorite = nextBoolean()
        viewModel.updateFavorite(movieId = 0, isFavorite = isFavorite)

        assertTrue(uiStateList[1].isUpdating)
        assertFalse(uiStateList.last().isUpdating)
        assertNotNull(uiStateList.last().userMsg)

        job.cancel()

    }

//    @Test
//    fun uiState_isLoading() = runTest {
//
//        assertTrue(viewModel.uiState.value is MovieDetailViewModel.MovieDetailUiState.Loading)
//
//    }
//
//    @Test
//    fun uiState_success() = runTest {
//
//        val stateList = arrayListOf<MovieDetailViewModel.MovieDetailUiState>()
//
//        val movieId = 9999
//        viewModel.saveMovieId(movieId = movieId)
//
//        val job = launch(mainDispatcherRule.testDispatcher) {
//            viewModel.uiState.toList(stateList)
//        }
//
//        assertEquals(2, stateList.size)
//        assertTrue(stateList.first() is MovieDetailViewModel.MovieDetailUiState.Loading)
//        assertTrue(stateList[1] is MovieDetailViewModel.MovieDetailUiState.Success)
//
//        job.cancel()
//    }
//
//    @Test
//    fun uiState_error() = runTest {
//
//        movieUseCase.isSuccess = false
//
//        val stateList = arrayListOf<MovieDetailViewModel.MovieDetailUiState>()
//
//        val movieId = 9999
//        viewModel.saveMovieId(movieId = movieId)
//
//        val job = launch(mainDispatcherRule.testDispatcher) {
//            viewModel.uiState.toList(stateList)
//        }
//
//        assertEquals(2, stateList.size)
//        assertTrue(stateList.first() is MovieDetailViewModel.MovieDetailUiState.Loading)
//        assertTrue(stateList[1] is MovieDetailViewModel.MovieDetailUiState.Error)
//
//        job.cancel()
//    }
//
//    @Test
//    fun uiState_empty() = runTest {
//
//        movieUseCase.isBodyEmpty = true
//
//        val stateList = arrayListOf<MovieDetailViewModel.MovieDetailUiState>()
//
//        val movieId = 9999
//        viewModel.saveMovieId(movieId = movieId)
//
//        val job = launch(mainDispatcherRule.testDispatcher) {
//            viewModel.uiState.toList(stateList)
//        }
//
//        assertEquals(2, stateList.size)
//        assertTrue(stateList.first() is MovieDetailViewModel.MovieDetailUiState.Loading)
//        assertTrue(stateList[1] is MovieDetailViewModel.MovieDetailUiState.Error)
//
//        job.cancel()
//    }
//
//    @Test
//    fun updatePosterLoadingStatus() = runTest {
//
//        val stateList = arrayListOf<MovieDetailViewModel.MovieDetailUiState>()
//
//        val movieId = 9999
//        viewModel.saveMovieId(movieId = movieId)
//
//        val job = launch(mainDispatcherRule.testDispatcher) {
//            viewModel.uiState.toList(stateList)
//        }
//
//        assertEquals(2, stateList.size)
//        assertTrue(stateList.first() is MovieDetailViewModel.MovieDetailUiState.Loading)
//        assertTrue(stateList[1] is MovieDetailViewModel.MovieDetailUiState.Success)
//        assertTrue((stateList[1] as MovieDetailViewModel.MovieDetailUiState.Success).isLoading)
//
//        viewModel.updatePosterLoadingStatus(false)
//
//        assertEquals(3, stateList.size)
//        assertFalse((stateList[2] as MovieDetailViewModel.MovieDetailUiState.Success).isLoading)
//
//        job.cancel()
//    }
//

}