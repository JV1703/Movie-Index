package com.example.movieindex.test.view_model

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import com.example.movieindex.core.common.RvListHelper
import com.example.movieindex.core.data.remote.model.details.toMovieDetails
import com.example.movieindex.fake.datastore.FakePreferenceDataStore
import com.example.movieindex.fake.use_case.FakeMovieUseCase
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

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class MovieDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainCoroutineRule()

    private lateinit var testDataFactory: TestDataFactory
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var testScope: TestScope
    private lateinit var movieUseCase: FakeMovieUseCase
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
        movieUseCase =
            FakeMovieUseCase(mainDispatcherRule.testDispatcher, dataStore, testDataFactory)
        viewModel =
            MovieDetailViewModel(movieUseCase = movieUseCase, savedStateHandle = savedStateHandle)
    }

    @Test
    fun saveCasts() = runTest {
        val casts = testDataFactory.generateMovieDetailsTestData().toMovieDetails().casts
        viewModel.saveCasts(casts)
        advanceUntilIdle()
        val cachedData = movieUseCase.getCasts().first()
        assertEquals(casts, cachedData)
    }

    @Test
    fun saveCrews() = runTest {
        val crews = testDataFactory.generateMovieDetailsTestData().toMovieDetails().crews
        viewModel.saveCrews(crews)
        advanceUntilIdle()
        val cachedData = movieUseCase.getCrews().first()
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
    fun uiState_isLoading() = runTest {

        assertTrue(viewModel.uiState.value is MovieDetailViewModel.MovieDetailUiState.Loading)

    }

    @Test
    fun uiState_success() = runTest {

        val stateList = arrayListOf<MovieDetailViewModel.MovieDetailUiState>()

        val movieId = 9999
        viewModel.saveMovieId(movieId = movieId)

        val job = launch(mainDispatcherRule.testDispatcher) {
            viewModel.uiState.toList(stateList)
        }

        assertEquals(2, stateList.size)
        assertTrue(stateList.first() is MovieDetailViewModel.MovieDetailUiState.Loading)
        assertTrue(stateList[1] is MovieDetailViewModel.MovieDetailUiState.Success)

        job.cancel()
    }

    @Test
    fun uiState_error() = runTest {

        movieUseCase.isSuccess = false

        val stateList = arrayListOf<MovieDetailViewModel.MovieDetailUiState>()

        val movieId = 9999
        viewModel.saveMovieId(movieId = movieId)

        val job = launch(mainDispatcherRule.testDispatcher) {
            viewModel.uiState.toList(stateList)
        }

        assertEquals(2, stateList.size)
        assertTrue(stateList.first() is MovieDetailViewModel.MovieDetailUiState.Loading)
        assertTrue(stateList[1] is MovieDetailViewModel.MovieDetailUiState.Error)

        job.cancel()
    }

    @Test
    fun uiState_empty() = runTest {

        movieUseCase.isBodyEmpty = true

        val stateList = arrayListOf<MovieDetailViewModel.MovieDetailUiState>()

        val movieId = 9999
        viewModel.saveMovieId(movieId = movieId)

        val job = launch(mainDispatcherRule.testDispatcher) {
            viewModel.uiState.toList(stateList)
        }

        assertEquals(2, stateList.size)
        assertTrue(stateList.first() is MovieDetailViewModel.MovieDetailUiState.Loading)
        assertTrue(stateList[1] is MovieDetailViewModel.MovieDetailUiState.Error)

        job.cancel()
    }

    @Test
    fun updatePosterLoadingStatus() = runTest {

        val stateList = arrayListOf<MovieDetailViewModel.MovieDetailUiState>()

        val movieId = 9999
        viewModel.saveMovieId(movieId = movieId)

        val job = launch(mainDispatcherRule.testDispatcher) {
            viewModel.uiState.toList(stateList)
        }

        assertEquals(2, stateList.size)
        assertTrue(stateList.first() is MovieDetailViewModel.MovieDetailUiState.Loading)
        assertTrue(stateList[1] is MovieDetailViewModel.MovieDetailUiState.Success)
        assertTrue((stateList[1] as MovieDetailViewModel.MovieDetailUiState.Success).isLoading)

        viewModel.updatePosterLoadingStatus(false)

        assertEquals(3, stateList.size)
        assertFalse((stateList[2] as MovieDetailViewModel.MovieDetailUiState.Success).isLoading)

        job.cancel()
    }


}