package com.example.movieindex.test.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.paging.*
import androidx.test.core.app.ApplicationProvider
import com.example.movieindex.core.data.external.model.Resource
import com.example.movieindex.core.data.local.abstraction.CacheDataSource
import com.example.movieindex.core.data.local.model.*
import com.example.movieindex.core.data.remote.model.account.toAccountDetails
import com.example.movieindex.core.data.remote.model.account.toAccountEntity
import com.example.movieindex.core.repository.abstraction.AccountRepository
import com.example.movieindex.core.repository.implementation.AccountRepositoryImpl
import com.example.movieindex.core.repository.paging.MoviesPagingRemoteMediator
import com.example.movieindex.fake.data_source.FakeCacheDataSource
import com.example.movieindex.fake.data_source.FakeNetworkDataSource
import com.example.movieindex.fake.datastore.FakePreferenceDataStore
import com.example.movieindex.util.MainCoroutineRule
import com.example.movieindex.util.TestDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.random.Random.Default.nextBoolean

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalPagingApi::class)
@RunWith(RobolectricTestRunner::class)
class AccountRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainCoroutineRule()

    private val context: Context = ApplicationProvider.getApplicationContext()
    private lateinit var network: FakeNetworkDataSource
    private lateinit var cache: CacheDataSource
    private lateinit var testDataFactory: TestDataFactory

    private lateinit var dataStore: DataStore<Preferences>

    private lateinit var repository: AccountRepository

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var testScope: TestScope

    @Before
    fun setup() {
        testDispatcher = mainDispatcherRule.testDispatcher
        testScope = TestScope(testDispatcher)
        testDataFactory = TestDataFactory()
        dataStore = FakePreferenceDataStore(context,
            testScope).testDataStore
        network = FakeNetworkDataSource(testDataFactory = testDataFactory,
            testDispatcher = testDispatcher)
        cache = FakeCacheDataSource(testDispatcher = testDispatcher, dataStore = dataStore)
        repository =
            AccountRepositoryImpl(network = network, cache = cache)
    }

    @Test
    fun getAccountDetails_Success() = runTest {
        val accountDetailsData = AccountEntity(avatarPath = "",
            id = 1,
            include_adult = nextBoolean(),
            iso_639_1 = "",
            iso_3166_1 = "",
            name = "Banana",
            username = "Banana")
        cache.insertAccountDetails(accountDetailsData)
        val apiCall = repository.getAccountDetails(sessionId = "").toList()
        val firstExpectedResult = accountDetailsData.toAccountDetails()
        val lastExpectedResult =
            testDataFactory.generateAccountDetailsResponseTestData().toAccountDetails()

        assertEquals(firstExpectedResult, (apiCall[0] as Resource.Success).data)
        assertEquals(lastExpectedResult, (apiCall[1] as Resource.Success).data)
    }

    @Test
    fun getAccountDetails_Empty() = runTest {
        network.isBodyEmpty = true
        val accountDetailsData = AccountEntity(avatarPath = "",
            id = 1,
            include_adult = nextBoolean(),
            iso_639_1 = "",
            iso_3166_1 = "",
            name = "Banana",
            username = "Banana")
        cache.insertAccountDetails(accountDetailsData)
        val apiCall = repository.getAccountDetails(sessionId = "").toList()
        val expectedResult = accountDetailsData.toAccountDetails()

        assertEquals(expectedResult, (apiCall[0] as Resource.Success).data)
        assertTrue(apiCall[1] is Resource.Empty)
    }

    @Test
    fun getAccountDetails_Error() = runTest {
        network.isSuccess = false
        val accountDetailsData = AccountEntity(avatarPath = "",
            id = 1,
            include_adult = nextBoolean(),
            iso_639_1 = "",
            iso_3166_1 = "",
            name = "Banana",
            username = "Banana")
        cache.insertAccountDetails(accountDetailsData)
        val apiCall = repository.getAccountDetails(sessionId = "").toList()
        val expectedResult = accountDetailsData.toAccountDetails()

        assertEquals(expectedResult, (apiCall[0] as Resource.Success).data)
        assertTrue(apiCall[1] is Resource.Error)
    }

    @Test
    fun addToFavorite_Success() = runTest {
        val apiCall = repository.addToFavorite(
            sessionId = "",
            accountId = 0,
            favorite = nextBoolean(),
            mediaId = 0)

        assertTrue(apiCall is Resource.Success)
    }

    @Test
    fun addToFavorite_Empty() = runTest {
        network.isBodyEmpty = true
        val apiCall = repository.addToFavorite(
            sessionId = "",
            accountId = 0,
            favorite = nextBoolean(),
            mediaId = 0)

        assertTrue(apiCall is Resource.Empty)
    }

    @Test
    fun addToFavorite_Error() = runTest {
        network.isSuccess = false
        val apiCall = repository.addToFavorite(
            sessionId = "",
            accountId = 0,
            favorite = nextBoolean(),
            mediaId = 0)

        assertTrue(apiCall is Resource.Error)
    }

    @Test
    fun addToWatchList_Success() = runTest {
        val apiCall = repository.addToWatchList(
            sessionId = "",
            accountId = 0,
            watchlist = nextBoolean(),
            mediaId = 0)

        assertTrue(apiCall is Resource.Success)
    }

    @Test
    fun addToWatchList_Empty() = runTest {
        network.isBodyEmpty = true
        val apiCall = repository.addToWatchList(
            sessionId = "",
            accountId = 0,
            watchlist = nextBoolean(),
            mediaId = 0)

        assertTrue(apiCall is Resource.Empty)
    }

    @Test
    fun addToWatchList_Error() = runTest {
        network.isSuccess = false
        val apiCall = repository.addToWatchList(
            sessionId = "",
            accountId = 0,
            watchlist = nextBoolean(),
            mediaId = 0)

        assertTrue(apiCall is Resource.Error)
    }

    @Test
    fun getAccountIdCache() = runTest {
        val data = testDataFactory.generateAccountDetailsResponseTestData().toAccountEntity()
        cache.insertAccountDetails(data)

        val cachedData = repository.getAccountIdCache().first()
        assertEquals(data.id, cachedData)
    }

    @Test
    fun deleteAccountDetailsCache() = runTest {
        val data = testDataFactory.generateAccountDetailsResponseTestData().toAccountEntity()
        cache.insertAccountDetails(data)

        val cachedData = repository.getAccountIdCache().first()
        assertNotNull(cachedData)

        cache.deleteAccountDetails()
        val updatedCachedData = repository.getAccountIdCache().first()
        assertNull(updatedCachedData)
    }

    @Test
    fun getMovieAccountState_Success() = runTest {
        val apiCall = repository.getMovieAccountState(movieId = 0, sessionId = "")
        assertTrue(apiCall is Resource.Success)
    }

    @Test
    fun getMovieAccountState_Empty() = runTest {
        network.isBodyEmpty = true
        val apiCall = repository.getMovieAccountState(movieId = 0, sessionId = "")
        assertTrue(apiCall is Resource.Empty)
    }

    @Test
    fun getMovieAccountState_Error() = runTest {
        network.isSuccess = false
        val apiCall = repository.getMovieAccountState(movieId = 0, sessionId = "")
        assertTrue(apiCall is Resource.Error)
    }

    @Test
    fun getWatchListRemoteMediator_LoadFirstPage_False_Success() = runTest {
        val remoteMediator = MoviesPagingRemoteMediator(
            loadSinglePage = false,
            pagingCategory = MoviePagingCategory.WATCHLIST,
            networkCall = { page ->
                network.getWatchList(accountId = 0,
                    sessionId = "",
                    page = page)
            },
            dbCallGetMovieKey = { id -> cache.movieKeyId(id = id) },
            dbCallOnRefreshClearDb = { pagingCategory: MoviePagingCategory ->
                cache.clearMovieKeys(pagingCategory = pagingCategory)
                cache.clearMovies(pagingCategory = pagingCategory)
            },
            dbCallOnSuccess = { movieKeys: List<MovieEntityKey>, movies: List<MoviePagingEntity> ->
                cache.insertAllMovieKeys(movieKeys)
                cache.insertAllMovies(movies)
            }
        )

        val pagingState = PagingState<Int, MoviePagingEntity>(pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(10),
            leadingPlaceholderCount = 10)

        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun getWatchListRemoteMediator_LoadFirstPage_True_Success() = runTest {
        val remoteMediator = MoviesPagingRemoteMediator(
            loadSinglePage = true,
            pagingCategory = MoviePagingCategory.WATCHLIST,
            networkCall = { page ->
                network.getWatchList(accountId = 0,
                    sessionId = "",
                    page = page)
            },
            dbCallGetMovieKey = { id -> cache.movieKeyId(id = id) },
            dbCallOnRefreshClearDb = { pagingCategory: MoviePagingCategory ->
                cache.clearMovieKeys(pagingCategory = pagingCategory)
                cache.clearMovies(pagingCategory = pagingCategory)
            },
            dbCallOnSuccess = { movieKeys: List<MovieEntityKey>, movies: List<MoviePagingEntity> ->
                cache.insertAllMovieKeys(movieKeys)
                cache.insertAllMovies(movies)
            }
        )

        val pagingState = PagingState<Int, MoviePagingEntity>(pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(10),
            leadingPlaceholderCount = 10)

        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun getWatchListRemoteMediator_Error() = runTest {

        network.isSuccess = false

        val remoteMediator = MoviesPagingRemoteMediator(
            loadSinglePage = true,
            pagingCategory = MoviePagingCategory.WATCHLIST,
            networkCall = { page ->
                network.getWatchList(accountId = 0,
                    sessionId = "",
                    page = page)
            },
            dbCallGetMovieKey = { id -> cache.movieKeyId(id = id) },
            dbCallOnRefreshClearDb = { pagingCategory: MoviePagingCategory ->
                cache.clearMovieKeys(pagingCategory = pagingCategory)
                cache.clearMovies(pagingCategory = pagingCategory)
            },
            dbCallOnSuccess = { movieKeys: List<MovieEntityKey>, movies: List<MoviePagingEntity> ->
                cache.insertAllMovieKeys(movieKeys)
                cache.insertAllMovies(movies)
            }
        )

        val pagingState = PagingState<Int, MoviePagingEntity>(pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(10),
            leadingPlaceholderCount = 10)

        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Error)
    }

    @Test
    fun getFavoriteListRemoteMediator_LoadFirstPage_False_Success() = runTest {
        val remoteMediator = MoviesPagingRemoteMediator(
            loadSinglePage = false,
            pagingCategory = MoviePagingCategory.FAVORITE,
            networkCall = { page ->
                network.getFavoriteList(accountId = 0,
                    sessionId = "",
                    page = page)
            },
            dbCallGetMovieKey = { id -> cache.movieKeyId(id = id) },
            dbCallOnRefreshClearDb = { pagingCategory: MoviePagingCategory ->
                cache.clearMovieKeys(pagingCategory = pagingCategory)
                cache.clearMovies(pagingCategory = pagingCategory)
            },
            dbCallOnSuccess = { movieKeys: List<MovieEntityKey>, movies: List<MoviePagingEntity> ->
                cache.insertAllMovieKeys(movieKeys)
                cache.insertAllMovies(movies)
            }
        )

        val pagingState = PagingState<Int, MoviePagingEntity>(pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(10),
            leadingPlaceholderCount = 10)

        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun getFavoriteListRemoteMediator_LoadFirstPage_True_Success() = runTest {
        val remoteMediator = MoviesPagingRemoteMediator(
            loadSinglePage = true,
            pagingCategory = MoviePagingCategory.FAVORITE,
            networkCall = { page ->
                network.getFavoriteList(accountId = 0,
                    sessionId = "",
                    page = page)
            },
            dbCallGetMovieKey = { id -> cache.movieKeyId(id = id) },
            dbCallOnRefreshClearDb = { pagingCategory: MoviePagingCategory ->
                cache.clearMovieKeys(pagingCategory = pagingCategory)
                cache.clearMovies(pagingCategory = pagingCategory)
            },
            dbCallOnSuccess = { movieKeys: List<MovieEntityKey>, movies: List<MoviePagingEntity> ->
                cache.insertAllMovieKeys(movieKeys)
                cache.insertAllMovies(movies)
            }
        )

        val pagingState = PagingState<Int, MoviePagingEntity>(pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(10),
            leadingPlaceholderCount = 10)

        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun getFavoriteListRemoteMediator_Error() = runTest {

        network.isSuccess = false

        val remoteMediator = MoviesPagingRemoteMediator(
            loadSinglePage = true,
            pagingCategory = MoviePagingCategory.FAVORITE,
            networkCall = { page ->
                network.getFavoriteList(accountId = 0,
                    sessionId = "",
                    page = page)
            },
            dbCallGetMovieKey = { id -> cache.movieKeyId(id = id) },
            dbCallOnRefreshClearDb = { pagingCategory: MoviePagingCategory ->
                cache.clearMovieKeys(pagingCategory = pagingCategory)
                cache.clearMovies(pagingCategory = pagingCategory)
            },
            dbCallOnSuccess = { movieKeys: List<MovieEntityKey>, movies: List<MoviePagingEntity> ->
                cache.insertAllMovieKeys(movieKeys)
                cache.insertAllMovies(movies)
            }
        )

        val pagingState = PagingState<Int, MoviePagingEntity>(pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(10),
            leadingPlaceholderCount = 10)

        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Error)
    }

}