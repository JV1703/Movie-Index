package com.example.movieindex.test.use_case

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.test.core.app.ApplicationProvider
import com.example.movieindex.core.data.external.model.Resource
import com.example.movieindex.core.data.remote.model.details.toMovieDetails
import com.example.movieindex.fake.datastore.FakePreferenceDataStore
import com.example.movieindex.fake.repository.FakeAccountRepository
import com.example.movieindex.fake.repository.FakeMovieRepository
import com.example.movieindex.feature.detail.movie.domain.abstraction.MovieDetailsUseCase
import com.example.movieindex.feature.detail.movie.domain.implementation.MovieDetailsUseCaseImpl
import com.example.movieindex.util.MainCoroutineRule
import com.example.movieindex.util.TestDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.random.Random.Default.nextBoolean

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class MovieDetailsUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainCoroutineRule()

    private lateinit var testDataFactory: TestDataFactory
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var testScope: TestScope
    private lateinit var movieRepository: FakeMovieRepository
    private lateinit var accountRepository: FakeAccountRepository

    private lateinit var useCase: MovieDetailsUseCase

    @Before
    fun setup() {
        testScope = TestScope()
        testDataFactory = TestDataFactory()
        dataStore =
            FakePreferenceDataStore(testContext = ApplicationProvider.getApplicationContext(),
                testCoroutineScope = testScope).testDataStore
        movieRepository = FakeMovieRepository(testDispatcher = mainDispatcherRule.testDispatcher,
            dataStore = dataStore, testDataFactory = testDataFactory)
        accountRepository =
            FakeAccountRepository(testDispatcher = mainDispatcherRule.testDispatcher,
                dataStore = dataStore, testDataFactory = testDataFactory)

        useCase = MovieDetailsUseCaseImpl(movieRepository = movieRepository,
            accountRepository = accountRepository)
    }

    @Test
    fun getMovieDetails_success() = runTest {
        val apiCall = useCase.getMovieDetails(movieId = 0)
        assertTrue(apiCall is Resource.Success)
    }

    @Test
    fun getMovieDetails_empty() = runTest {
        movieRepository.isBodyEmpty = true
        val apiCall = useCase.getMovieDetails(movieId = 0)
        assertTrue(apiCall is Resource.Empty)
    }

    @Test
    fun getMovieDetails_error() = runTest {
        movieRepository.isSuccess = false
        val apiCall = useCase.getMovieDetails(movieId = 0)
        assertTrue(apiCall is Resource.Error)
    }

    @Test
    fun saveCasts() = runTest {
        val casts = testDataFactory.generateMovieDetailsTestData().toMovieDetails().casts
        useCase.saveCasts(casts)
        val cachedData = movieRepository.getCasts().first()
        assertEquals(casts, cachedData)
    }

    @Test
    fun saveCrews() = runTest {
        val crews = testDataFactory.generateMovieDetailsTestData().toMovieDetails().crews
        useCase.saveCrews(crews)
        val cachedData = movieRepository.getCrews().first()
        assertEquals(crews, cachedData)
    }

    @Test
    fun addToFavoriteList_success() = runTest {
        val apiCall = useCase.addToFavorite(
            accountId = 0,
            sessionId = "",
            favorite = nextBoolean(),
            mediaId = 0)

        assertTrue(apiCall is Resource.Success)
    }

    @Test
    fun addToFavoriteList_empty() = runTest {
        accountRepository.isBodyEmpty = true
        val apiCall = useCase.addToFavorite(
            accountId = 0,
            sessionId = "",
            favorite = nextBoolean(),
            mediaId = 0)

        assertTrue(apiCall is Resource.Empty)
    }

    @Test
    fun addToFavoriteList_error() = runTest {
        accountRepository.isSuccess = false
        val apiCall = useCase.addToFavorite(
            accountId = 0,
            sessionId = "",
            favorite = nextBoolean(),
            mediaId = 0)

        assertTrue(apiCall is Resource.Error)
    }

    @Test
    fun addToWatchList_success() = runTest {
        val apiCall = useCase.addToWatchList(
            accountId = 0,
            sessionId = "",
            watchlist = nextBoolean(),
            mediaId = 0)

        assertTrue(apiCall is Resource.Success)
    }

    @Test
    fun addToWatchList_empty() = runTest {
        accountRepository.isBodyEmpty = true
        val apiCall = useCase.addToWatchList(
            accountId = 0,
            sessionId = "",
            watchlist = nextBoolean(),
            mediaId = 0)

        assertTrue(apiCall is Resource.Empty)
    }

    @Test
    fun addToWatchList_error() = runTest {
        accountRepository.isSuccess = false
        val apiCall = useCase.addToWatchList(
            accountId = 0,
            sessionId = "",
            watchlist = nextBoolean(),
            mediaId = 0)

        assertTrue(apiCall is Resource.Error)
    }

    @Test
    fun getMovieAccountState_success() = runTest {
        val apiCall = useCase.getMovieAccountState(movieId = 0, sessionId = "")
        assertTrue(apiCall is Resource.Success)
    }

    @Test
    fun getMovieAccountState_empty() = runTest {
        accountRepository.isBodyEmpty = true
        val apiCall = useCase.getMovieAccountState(movieId = 0, sessionId = "")
        assertTrue(apiCall is Resource.Empty)
    }

    @Test
    fun getMovieAccountState_error() = runTest {
        accountRepository.isSuccess = false
        val apiCall = useCase.getMovieAccountState(movieId = 0, sessionId = "")
        assertTrue(apiCall is Resource.Error)
    }

}