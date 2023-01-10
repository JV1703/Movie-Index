package com.example.movieindex.test.data_source

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.movieindex.core.data.remote.NetworkResource
import com.example.movieindex.core.data.remote.abstraction.NetworkDataSource
import com.example.movieindex.core.data.remote.implementation.NetworkDataSourceImpl
import com.example.movieindex.core.data.remote.model.auth.body.LoginBody
import com.example.movieindex.core.data.remote.safeNetworkCall
import com.example.movieindex.fake.apis.FakeMovieApi
import com.example.movieindex.util.MainCoroutineRule
import com.example.movieindex.util.TestDataFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NetworkDataSourceTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainCoroutineRule()

    private lateinit var testDataFactory: TestDataFactory
    private lateinit var api: FakeMovieApi
    private lateinit var network: NetworkDataSource

    @Before
    fun setup() {
        testDataFactory = TestDataFactory()
        api = FakeMovieApi(testDataFactory = testDataFactory)
        network = NetworkDataSourceImpl(api, Dispatchers.Main)
    }

    @Test
    fun getNowPlaying_success() = runTest {
        val expectedResult =
            safeNetworkCall(Dispatchers.Main,
                networkCall = { api.getNowPlaying() },
                conversion = { it })
        val actualResult = network.getNowPlaying()

        assertTrue(actualResult is NetworkResource.Success)
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun getNowPlaying_empty() = runTest {

        api.isBodyEmpty = true

        val actualResult = network.getNowPlaying()

        assertTrue(actualResult is NetworkResource.Empty)
    }

    @Test
    fun getNowPlaying_error() = runTest {

        api.isSuccess = false

        val actualResult = network.getNowPlaying()

        assertTrue(actualResult is NetworkResource.Error)
    }

    @Test
    fun getPopularMovies_success() = runTest {
        val expectedResult =
            safeNetworkCall(Dispatchers.Main,
                networkCall = { api.getPopularMovies() },
                conversion = { it })
        val actualResult = network.getPopularMovies()

        assertTrue(actualResult is NetworkResource.Success)
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun getPopularMovies_empty() = runTest {

        api.isBodyEmpty = true

        val actualResult = network.getPopularMovies()

        assertTrue(actualResult is NetworkResource.Empty)
    }

    @Test
    fun getPopularMovies_error() = runTest {

        api.isSuccess = false

        val actualResult = network.getPopularMovies()

        assertTrue(actualResult is NetworkResource.Error)
    }

    @Test
    fun getRecommendations_success() = runTest {
        val expectedResult =
            safeNetworkCall(Dispatchers.Main,
                networkCall = { api.getMovieRecommendations(movieId = 0) },
                conversion = { it })
        val actualResult = network.getMovieRecommendations(movieId = 0)

        assertTrue(actualResult is NetworkResource.Success)
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun getRecommendations_empty() = runTest {

        api.isBodyEmpty = true

        val actualResult = network.getMovieRecommendations(movieId = 0)

        assertTrue(actualResult is NetworkResource.Empty)
    }

    @Test
    fun getRecommendations_error() = runTest {

        api.isSuccess = false

        val actualResult = network.getMovieRecommendations(movieId = 0)

        assertTrue(actualResult is NetworkResource.Error)
    }

    @Test
    fun getTrendingMovies_success() = runTest {
        val expectedResult =
            safeNetworkCall(Dispatchers.Main,
                networkCall = { api.getTrendingMovies() },
                conversion = { it })
        val actualResult = network.getTrendingMovies()

        assertTrue(actualResult is NetworkResource.Success)
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun getTrendingMovies_empty() = runTest {

        api.isBodyEmpty = true

        val actualResult = network.getTrendingMovies()

        assertTrue(actualResult is NetworkResource.Empty)
    }

    @Test
    fun getTrendingMovies_error() = runTest {

        api.isSuccess = false

        val actualResult = network.getTrendingMovies()

        assertTrue(actualResult is NetworkResource.Error)
    }

    @Test
    fun getMovieDetails_success() = runTest {
        val expectedResult =
            safeNetworkCall(Dispatchers.Main,
                networkCall = { api.getMovieDetails(movieId = 0) },
                conversion = { it })
        val actualResult = network.getMovieDetails(movieId = 0)

        assertTrue(actualResult is NetworkResource.Success)
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun getMovieDetails_empty() = runTest {

        api.isBodyEmpty = true

        val actualResult = network.getMovieDetails(movieId = 0)

        assertTrue(actualResult is NetworkResource.Empty)
    }

    @Test
    fun getMovieDetails_error() = runTest {

        api.isSuccess = false

        val actualResult = network.getMovieDetails(movieId = 0)

        assertTrue(actualResult is NetworkResource.Error)
    }

    @Test
    fun searchMovies_success() = runTest {
        val expectedResult =
            safeNetworkCall(Dispatchers.Main,
                networkCall = { api.searchMovies(query = "") },
                conversion = { it })
        val actualResult = network.searchMovies(query = "")

        assertTrue(actualResult is NetworkResource.Success)
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun searchMovies_empty() = runTest {

        api.isBodyEmpty = true

        val actualResult = network.searchMovies(query = "")

        assertTrue(actualResult is NetworkResource.Empty)
    }

    @Test
    fun searchMovies_error() = runTest {

        api.isSuccess = false

        val actualResult = network.searchMovies(query = "")

        assertTrue(actualResult is NetworkResource.Error)
    }

    @Test
    fun requestToken_success() = runTest {

        val actualResult = network.requestToken()

        assertTrue(actualResult is NetworkResource.Success)
    }

    @Test
    fun requestToken_empty() = runTest {

        api.isBodyEmpty = true

        val actualResult = network.requestToken()

        assertTrue(actualResult is NetworkResource.Empty)
    }

    @Test
    fun requestToken_error() = runTest {

        api.isSuccess = false

        val actualResult = network.requestToken()

        assertTrue(actualResult is NetworkResource.Error)
    }

    @Test
    fun login_success() = runTest {

        val loginBody = LoginBody(username = "", password = "", request_token = "")

        val actualResult = network.login(loginBody = loginBody)

        assertTrue(actualResult is NetworkResource.Success)
    }

    @Test
    fun login_empty() = runTest {

        api.isBodyEmpty = true

        val loginBody = LoginBody(username = "", password = "", request_token = "")

        val actualResult = network.login(loginBody = loginBody)

        assertTrue(actualResult is NetworkResource.Empty)
    }

    @Test
    fun login_error() = runTest {

        api.isSuccess = false

        val loginBody = LoginBody(username = "", password = "", request_token = "")

        val actualResult = network.login(loginBody = loginBody)

        assertTrue(actualResult is NetworkResource.Error)
    }

    @Test
    fun createSession_success() = runTest {

        val actualResult = network.createSession(requestToken = "")

        assertTrue(actualResult is NetworkResource.Success)
    }

    @Test
    fun createSession_empty() = runTest {

        api.isBodyEmpty = true

        val actualResult = network.createSession(requestToken = "")

        assertTrue(actualResult is NetworkResource.Empty)
    }

    @Test
    fun createSession_error() = runTest {

        api.isSuccess = false

        val actualResult = network.createSession(requestToken = "")

        assertTrue(actualResult is NetworkResource.Error)
    }

    @Test
    fun getAccountDetails_success() = runTest {
        val actualResult = network.getAccountDetails(sessionId = "")
        assertTrue(actualResult is NetworkResource.Success)
    }

    @Test
    fun getAccountDetails_empty() = runTest {
        api.isBodyEmpty = true
        val actualResult = network.getAccountDetails(sessionId = "")
        assertTrue(actualResult is NetworkResource.Empty)
    }

    @Test
    fun getAccountDetails_error() = runTest {
        api.isSuccess = false
        val actualResult = network.getAccountDetails(sessionId = "")
        assertTrue(actualResult is NetworkResource.Error)
    }

    @Test
    fun addToFavorite_success() = runTest {
        val favoriteBody = testDataFactory.generateFavoriteBody()
        val actualResult = network.addToFavorite(0, "", favoriteBody)

        assertTrue(actualResult is NetworkResource.Success)
    }

    @Test
    fun addToFavorite_empty() = runTest {
        api.isBodyEmpty = true
        val favoriteBody = testDataFactory.generateFavoriteBody()
        val actualResult = network.addToFavorite(0, "", favoriteBody)

        assertTrue(actualResult is NetworkResource.Empty)
    }

    @Test
    fun addToFavorite_error() = runTest {
        api.isSuccess = false
        val favoriteBody = testDataFactory.generateFavoriteBody()
        val actualResult = network.addToFavorite(0, "", favoriteBody)

        assertTrue(actualResult is NetworkResource.Error)
    }

    @Test
    fun addToWatchList_success() = runTest {
        val watchlistBody = testDataFactory.generateWatchListBody()
        val actualResult = network.addToWatchList(0, "", watchlistBody)

        assertTrue(actualResult is NetworkResource.Success)
    }

    @Test
    fun addToWatchList_empty() = runTest {
        api.isBodyEmpty = true
        val watchlistBody = testDataFactory.generateWatchListBody()
        val actualResult = network.addToWatchList(0, "", watchlistBody)

        assertTrue(actualResult is NetworkResource.Empty)
    }

    @Test
    fun addToWatchList_error() = runTest {
        api.isSuccess = false
        val watchlistBody = testDataFactory.generateWatchListBody()
        val actualResult = network.addToWatchList(0, "", watchlistBody)

        assertTrue(actualResult is NetworkResource.Error)
    }

    @Test
    fun getFavoriteList_success() = runTest {
        val expectedResult =
            safeNetworkCall(Dispatchers.Main,
                networkCall = { api.getFavoriteList(accountId = 0, sessionId = "") },
                conversion = { it })
        val actualResult = network.getFavoriteList(accountId = 0, sessionId = "")

        assertTrue(actualResult is NetworkResource.Success)
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun getFavoriteList_empty() = runTest {

        api.isBodyEmpty = true

        val actualResult = network.getFavoriteList(accountId = 0, sessionId = "")

        assertTrue(actualResult is NetworkResource.Empty)
    }

    @Test
    fun getFavoriteList_error() = runTest {

        api.isSuccess = false

        val actualResult = network.getFavoriteList(accountId = 0, sessionId = "")

        assertTrue(actualResult is NetworkResource.Error)
    }

    @Test
    fun getWatchList_success() = runTest {
        val expectedResult =
            safeNetworkCall(Dispatchers.Main,
                networkCall = { api.getWatchList(accountId = 0, sessionId = "") },
                conversion = { it })
        val actualResult = network.getWatchList(accountId = 0, sessionId = "")

        assertTrue(actualResult is NetworkResource.Success)
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun getWatchList_empty() = runTest {

        api.isBodyEmpty = true

        val actualResult = network.getWatchList(accountId = 0, sessionId = "")

        assertTrue(actualResult is NetworkResource.Empty)
    }

    @Test
    fun getWatchList_error() = runTest {

        api.isSuccess = false

        val actualResult = network.getWatchList(accountId = 0, sessionId = "")

        assertTrue(actualResult is NetworkResource.Error)
    }


}