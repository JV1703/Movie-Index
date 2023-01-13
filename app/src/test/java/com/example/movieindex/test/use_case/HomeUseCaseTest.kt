package com.example.movieindex.test.use_case

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.test.core.app.ApplicationProvider
import com.example.movieindex.core.data.external.model.Resource
import com.example.movieindex.fake.datastore.FakePreferenceDataStore
import com.example.movieindex.fake.repository.FakeMovieRepository
import com.example.movieindex.feature.main.ui.home.domain.abstraction.HomeUseCase
import com.example.movieindex.feature.main.ui.home.domain.implementation.HomeUseCaseImpl
import com.example.movieindex.util.MainCoroutineRule
import com.example.movieindex.util.TestDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class HomeUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainCoroutineRule()

    private lateinit var testDataFactory: TestDataFactory
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var testScope: TestScope
    private lateinit var movieRepository: FakeMovieRepository

    private lateinit var useCase: HomeUseCase

    @Before
    fun setup() {
        testScope = TestScope()
        testDataFactory = TestDataFactory()
        dataStore =
            FakePreferenceDataStore(testContext = ApplicationProvider.getApplicationContext(),
                testCoroutineScope = testScope).testDataStore
        movieRepository =
            FakeMovieRepository(testDispatcher = mainDispatcherRule.testDispatcher,
                dataStore = dataStore, testDataFactory = testDataFactory)

        useCase = HomeUseCaseImpl(movieRepository = movieRepository)
    }

    @Test
    fun getNowPlaying_success() = runTest {
        val apiCall = movieRepository.getNowPlaying()
        assertTrue(apiCall is Resource.Success)
    }

    @Test
    fun getNowPlaying_empty() = runTest {
        movieRepository.isBodyEmpty = true
        val apiCall = movieRepository.getNowPlaying()
        assertTrue(apiCall is Resource.Empty)
    }

    @Test
    fun getNowPlaying_error() = runTest {
        movieRepository.isSuccess = false
        val apiCall = movieRepository.getNowPlaying()
        assertTrue(apiCall is Resource.Error)
    }

    @Test
    fun getPopularMovies_success() = runTest {
        val apiCall = movieRepository.getPopularMovies()
        assertTrue(apiCall is Resource.Success)
    }

    @Test
    fun getPopularMovies_empty() = runTest {
        movieRepository.isBodyEmpty = true
        val apiCall = movieRepository.getPopularMovies()
        assertTrue(apiCall is Resource.Empty)
    }

    @Test
    fun getPopularMovies_error() = runTest {
        movieRepository.isSuccess = false
        val apiCall = movieRepository.getPopularMovies()
        assertTrue(apiCall is Resource.Error)
    }

    @Test
    fun getTrendingMovies_success() = runTest {
        val apiCall = movieRepository.getTrendingMovies()
        assertTrue(apiCall is Resource.Success)
    }

    @Test
    fun getTrendingMovies_empty() = runTest {
        movieRepository.isBodyEmpty = true
        val apiCall = movieRepository.getTrendingMovies()
        assertTrue(apiCall is Resource.Empty)
    }

    @Test
    fun getTrendingMovies_error() = runTest {
        movieRepository.isSuccess = false
        val apiCall = movieRepository.getTrendingMovies()
        assertTrue(apiCall is Resource.Error)
    }

}