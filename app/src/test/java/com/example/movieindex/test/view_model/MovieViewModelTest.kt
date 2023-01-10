package com.example.movieindex.test.view_model

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.test.core.app.ApplicationProvider
import com.example.movieindex.core.data.external.model.Resource
import com.example.movieindex.fake.datastore.FakePreferenceDataStore
import com.example.movieindex.fake.use_case.FakeMovieUseCase
import com.example.movieindex.feature.main.ui.home.MovieViewModel
import com.example.movieindex.util.MainCoroutineRule
import com.example.movieindex.util.TestDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class MovieViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainCoroutineRule()

    private lateinit var testDataFactory: TestDataFactory
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var testScope: TestScope
    private lateinit var movieUseCase: FakeMovieUseCase

    private lateinit var viewModel: MovieViewModel

    @Before
    fun setup() {
        testScope = TestScope()
        testDataFactory = TestDataFactory()
        dataStore =
            FakePreferenceDataStore(testContext = ApplicationProvider.getApplicationContext(),
                testCoroutineScope = testScope).testDataStore
        movieUseCase =
            FakeMovieUseCase(mainDispatcherRule.testDispatcher, dataStore, testDataFactory)
        viewModel = MovieViewModel(movieUseCase = movieUseCase)
    }

    @Test
    fun nowPlaying_loading() = runTest {
        val actualResult = movieUseCase.getNowPlaying().first()
        assertTrue(actualResult is Resource.Loading)
    }

    @Test
    fun nowPlaying_success() = runTest {
        val actualResult = movieUseCase.getNowPlaying().toList()
        assertEquals(2, actualResult.size)
        assertTrue(actualResult[1] is Resource.Success)
    }

    @Test
    fun nowPlaying_error() = runTest {
        movieUseCase.isSuccess = false
        val actualResult = movieUseCase.getNowPlaying().toList()
        assertEquals(2, actualResult.size)
        assertTrue(actualResult[1] is Resource.Error)
    }

    @Test
    fun nowPlaying_empty() = runTest {
        movieUseCase.isBodyEmpty = true
        val actualResult = movieUseCase.getNowPlaying().toList()
        assertEquals(2, actualResult.size)
        assertTrue(actualResult[1] is Resource.Empty)
    }

    @Test
    fun popularMovies_loading() = runTest {
        val actualResult = movieUseCase.getPopularMovies().first()
        assertTrue(actualResult is Resource.Loading)
    }

    @Test
    fun popularMovies_success() = runTest {
        val actualResult = movieUseCase.getPopularMovies().toList()
        assertEquals(2, actualResult.size)
        assertTrue(actualResult[1] is Resource.Success)
    }

    @Test
    fun popularMovies_error() = runTest {
        movieUseCase.isSuccess = false
        val actualResult = movieUseCase.getPopularMovies().toList()
        assertEquals(2, actualResult.size)
        assertTrue(actualResult[1] is Resource.Error)
    }

    @Test
    fun popularMovies_empty() = runTest {
        movieUseCase.isBodyEmpty = true
        val actualResult = movieUseCase.getPopularMovies().toList()
        assertEquals(2, actualResult.size)
        assertTrue(actualResult[1] is Resource.Empty)
    }

    @Test
    fun trendingMovies_loading() = runTest {
        val actualResult = movieUseCase.getTrendingMovies().first()
        assertTrue(actualResult is Resource.Loading)
    }

    @Test
    fun trendingMovies_success() = runTest {
        val actualResult = movieUseCase.getTrendingMovies().toList()
        assertEquals(2, actualResult.size)
        assertTrue(actualResult[1] is Resource.Success)
    }

    @Test
    fun trendingMovies_error() = runTest {
        movieUseCase.isSuccess = false
        val actualResult = movieUseCase.getTrendingMovies().toList()
        assertEquals(2, actualResult.size)
        assertTrue(actualResult[1] is Resource.Error)
    }

    @Test
    fun trendingMovies_empty() = runTest {
        movieUseCase.isBodyEmpty = true
        val actualResult = movieUseCase.getTrendingMovies().toList()
        assertEquals(2, actualResult.size)
        assertTrue(actualResult[1] is Resource.Empty)
    }

}