package com.example.movieindex.test.use_case

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.test.core.app.ApplicationProvider
import com.example.movieindex.core.data.external.Resource
import com.example.movieindex.core.data.remote.model.details.toMovieDetails
import com.example.movieindex.fake.datastore.FakePreferenceDataStore
import com.example.movieindex.fake.repository.FakeMovieRepository
import com.example.movieindex.feature.common.domain.abstraction.MovieUseCase
import com.example.movieindex.feature.common.domain.implementation.MovieUseCaseImpl
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
class MovieUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainCoroutineRule()

    private lateinit var testDataFactory: TestDataFactory
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var testScope: TestScope
    private lateinit var movieRepository: FakeMovieRepository

    private lateinit var movieUseCase: MovieUseCase

    @Before
    fun setup() {
        testScope = TestScope()
        testDataFactory = TestDataFactory()
        dataStore =
            FakePreferenceDataStore(testContext = ApplicationProvider.getApplicationContext(),
                testCoroutineScope = testScope).testDataStore
        movieRepository = FakeMovieRepository(testDispatcher = mainDispatcherRule.testDispatcher,
            dataStore = dataStore, testDataFactory = testDataFactory)

        movieUseCase = MovieUseCaseImpl(repository = movieRepository)
    }

    @Test
    fun getNowPlaying_loading() = runTest {
        val actualResult = movieUseCase.getNowPlaying().first()
        assertTrue(actualResult is Resource.Loading)
    }

    @Test
    fun getNowPlaying_success() = runTest {
        val actualResult = movieUseCase.getNowPlaying().toList()
        assertEquals(2, actualResult.size)
        assertTrue(actualResult[1] is Resource.Success)
    }

    @Test
    fun getNowPlaying_error() = runTest {

        movieRepository.isSuccess = false

        val actualResult = movieUseCase.getNowPlaying().toList()
        assertEquals(2, actualResult.size)
        assertTrue(actualResult[1] is Resource.Error)

    }

    @Test
    fun getNowPlaying_empty() = runTest {

        movieRepository.isBodyEmpty = true

        val actualResult = movieUseCase.getNowPlaying().toList()
        assertEquals(2, actualResult.size)
        assertTrue(actualResult[1] is Resource.Empty)

    }

    @Test
    fun getPopularMovies_loading() = runTest {
        val actualResult = movieUseCase.getPopularMovies().first()
        assertTrue(actualResult is Resource.Loading)
    }

    @Test
    fun getPopularMovies_success() = runTest {
        val actualResult = movieUseCase.getPopularMovies().toList()
        assertEquals(2, actualResult.size)
        assertTrue(actualResult[1] is Resource.Success)
    }

    @Test
    fun getPopularMovies_error() = runTest {

        movieRepository.isSuccess = false

        val actualResult = movieUseCase.getPopularMovies().toList()
        assertEquals(2, actualResult.size)
        assertTrue(actualResult[1] is Resource.Error)

    }

    @Test
    fun getPopularMovies_empty() = runTest {

        movieRepository.isBodyEmpty = true

        val actualResult = movieUseCase.getPopularMovies().toList()
        assertEquals(2, actualResult.size)
        assertTrue(actualResult[1] is Resource.Empty)

    }

    @Test
    fun getTrendingMovies_loading() = runTest {
        val actualResult = movieUseCase.getTrendingMovies().first()
        assertTrue(actualResult is Resource.Loading)
    }

    @Test
    fun getTrendingMovies_success() = runTest {
        val actualResult = movieUseCase.getTrendingMovies().toList()
        assertEquals(2, actualResult.size)
        assertTrue(actualResult[1] is Resource.Success)
    }

    @Test
    fun getTrendingMovies_error() = runTest {

        movieRepository.isSuccess = false

        val actualResult = movieUseCase.getTrendingMovies().toList()
        assertEquals(2, actualResult.size)
        assertTrue(actualResult[1] is Resource.Error)

    }

    @Test
    fun getTrendingMovies_empty() = runTest {

        movieRepository.isBodyEmpty = true

        val actualResult = movieUseCase.getTrendingMovies().toList()
        assertEquals(2, actualResult.size)
        assertTrue(actualResult[1] is Resource.Empty)

    }

    @Test
    fun getMovieDetails_loading() = runTest {
        val actualResult = movieUseCase.getMovieDetails(movieId = 0).first()
        assertTrue(actualResult is Resource.Loading)
    }

    @Test
    fun getMovieDetails_success() = runTest {
        val actualResult = movieUseCase.getMovieDetails(movieId = 0).toList()
        assertEquals(2, actualResult.size)
        assertTrue(actualResult[1] is Resource.Success)
    }

    @Test
    fun getMovieDetails_error() = runTest {

        movieRepository.isSuccess = false

        val actualResult = movieUseCase.getMovieDetails(movieId = 0).toList()
        assertEquals(2, actualResult.size)
        assertTrue(actualResult[1] is Resource.Error)

    }

    @Test
    fun getMovieDetails_empty() = runTest {

        movieRepository.isBodyEmpty = true

        val actualResult = movieUseCase.getMovieDetails(movieId = 0).toList()
        assertEquals(2, actualResult.size)
        assertTrue(actualResult[1] is Resource.Empty)

    }

    @Test
    fun saveCasts_getCasts() = runTest {
        val casts = testDataFactory.generateMovieDetailsTestData().toMovieDetails().casts
        movieUseCase.saveCasts(casts)

        val cachedData = movieUseCase.getCasts().first()

        assertEquals(casts, cachedData)
    }


    @Test
    fun saveCrews_getCrews() = runTest {

        val crews = testDataFactory.generateMovieDetailsTestData().toMovieDetails().crews
        movieUseCase.saveCrews(crews)

        val cachedData = movieUseCase.getCrews().first()

        assertEquals(crews, cachedData)

    }

}