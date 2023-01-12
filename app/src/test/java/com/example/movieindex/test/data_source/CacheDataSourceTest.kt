package com.example.movieindex.test.data_source

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.test.core.app.ApplicationProvider
import com.example.movieindex.core.data.local.abstraction.CacheDataSource
import com.example.movieindex.core.data.local.dao.AccountDao
import com.example.movieindex.core.data.local.dao.MoviePagingDao
import com.example.movieindex.core.data.local.dao.MoviePagingKeyDao
import com.example.movieindex.core.data.local.implementation.CacheDataSourceImpl
import com.example.movieindex.core.data.local.model.MoviePagingCategory
import com.example.movieindex.core.data.remote.model.account.toAccountEntity
import com.example.movieindex.core.data.remote.model.common.toMoviePagingEntity
import com.example.movieindex.core.data.remote.model.details.toMovieDetails
import com.example.movieindex.fake.apis.FakeAccountDao
import com.example.movieindex.fake.apis.FakeMoviePagingDao
import com.example.movieindex.fake.apis.FakeMoviePagingKeyDao
import com.example.movieindex.fake.datastore.FakePreferenceDataStore
import com.example.movieindex.util.MainCoroutineRule
import com.example.movieindex.util.TestDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class CacheDataSourceTest {

    @get:Rule
    val mainDispatcherRule = MainCoroutineRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    private lateinit var testDataFactory: TestDataFactory
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var cache: CacheDataSource

    private lateinit var moviePagingDao: MoviePagingDao
    private lateinit var moviePagingKeyDao: MoviePagingKeyDao
    private lateinit var accountDao: AccountDao

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var testScope: TestScope

    @Before
    fun setup() {
        testDataFactory = TestDataFactory()
        testDispatcher = mainDispatcherRule.testDispatcher
        testScope = TestScope(testDispatcher)
        dataStore = FakePreferenceDataStore(context,
            testScope).testDataStore
        moviePagingDao = FakeMoviePagingDao()
        moviePagingKeyDao = FakeMoviePagingKeyDao()
        accountDao = FakeAccountDao()
        cache = CacheDataSourceImpl(
            moviePagingDao = moviePagingDao,
            moviePagingKeyDao = moviePagingKeyDao,
            accountDao = accountDao,
            dataStore = dataStore,
            ioDispatcher = testDispatcher
        )
    }

    @Test
    fun saveSessionId_getSessionId() = runTest {
        val sessionId = UUID.randomUUID().toString()
        cache.saveSessionId(sessionId = sessionId)
        val cachedSessionId = cache.getSessionId().first()

        assertEquals(sessionId, cachedSessionId)
    }

    @Test
    fun saveCasts_getCasts() = runTest {
        val casts = testDataFactory.generateMovieDetailsTestData().toMovieDetails().casts.toString()
        cache.saveCasts(casts)
        val cachedData = cache.getCasts().first()
        assertEquals(casts, cachedData)
    }

    @Test
    fun saveCrews_getCrews() = runTest {
        val crews = testDataFactory.generateMovieDetailsTestData().toMovieDetails().crews.toString()
        cache.saveCrews(crews)
        val cachedData = cache.getCrews().first()
        assertEquals(crews, cachedData)
    }

    @Test
    fun insertAllMovies_getAllMovies() = runTest {
        val data = testDataFactory.generatePopularMoviesTestData().results.map {
            it.toMoviePagingEntity(pagingCategory = MoviePagingCategory.FAVORITE)
        }
        cache.insertAllMovies(data)

        val cachedData = cache.getAllMovies().first()
        assertEquals(data, cachedData)
    }

    @Test
    fun getMoviesWithReferenceToPagingCategory() = runTest {
        val data =
            testDataFactory.generatePopularMoviesTestData().results.mapIndexed { index, movies ->
                movies.toMoviePagingEntity(pagingCategory = if (index % 2 == 0) MoviePagingCategory.FAVORITE else MoviePagingCategory.WATCHLIST)
            }
        moviePagingDao.insertAllMovies(data)

        val expectedResult = data.filter { it.pagingCategory == MoviePagingCategory.FAVORITE }
        val cachedData =
            cache.getMoviesWithReferenceToPagingCategory(pagingCategory = MoviePagingCategory.FAVORITE)
                .first()

        assertEquals(expectedResult, cachedData)
    }

    @Test
    fun clearMovies() = runTest {
        val data =
            testDataFactory.generatePopularMoviesTestData().results.mapIndexed { index, movies ->
                movies.toMoviePagingEntity(pagingCategory = if (index % 2 == 0) MoviePagingCategory.FAVORITE else MoviePagingCategory.WATCHLIST)
            }
        moviePagingDao.insertAllMovies(data)

        val cachedData = cache.getAllMovies().first()
        assertTrue(cachedData.isNotEmpty())

        cache.clearMovies(pagingCategory = MoviePagingCategory.FAVORITE)
        val updatedCachedData = cache.getAllMovies().first()

        val expectedResult = data.filter { it.pagingCategory == MoviePagingCategory.WATCHLIST }
        assertEquals(expectedResult, updatedCachedData)
        assertTrue(updatedCachedData.none { it.pagingCategory == MoviePagingCategory.FAVORITE })

    }

    @Test
    fun insertAccountDetails_getAccountDetails() = runTest {
        val data = testDataFactory.generateAccountDetailsResponseTestData().toAccountEntity()
        cache.insertAccountDetails(data)

        val cachedData = cache.getAccountDetails().first()
        assertEquals(data, cachedData)
    }

    @Test
    fun getAccountId() = runTest {
        val data = testDataFactory.generateAccountDetailsResponseTestData().toAccountEntity()
        cache.insertAccountDetails(data)

        val cachedData = cache.getAccountDetails().first()
        assertEquals(data.id, cachedData?.id)
    }

    @Test
    fun deleteAccountDetails() = runTest {
        val data = testDataFactory.generateAccountDetailsResponseTestData().toAccountEntity()
        cache.insertAccountDetails(data)

        val cachedData = cache.getAccountDetails().first()
        assertEquals(data, cachedData)

        cache.deleteAccountDetails()
        val updatedCachedData = cache.getAccountDetails().first()

        assertNull(updatedCachedData)
    }

}