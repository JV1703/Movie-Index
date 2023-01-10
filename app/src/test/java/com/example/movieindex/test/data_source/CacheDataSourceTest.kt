package com.example.movieindex.test.data_source

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.test.core.app.ApplicationProvider
import com.example.movieindex.core.data.external.toMovieEntity
import com.example.movieindex.core.data.local.abstraction.CacheDataSource
import com.example.movieindex.core.data.local.dao.MovieDao
import com.example.movieindex.core.data.local.implementation.CacheDataSourceImpl
import com.example.movieindex.core.data.remote.model.common.toMovies
import com.example.movieindex.core.data.remote.model.details.toMovieDetails
import com.example.movieindex.fake.apis.FakeMovieDao
import com.example.movieindex.fake.datastore.FakePreferenceDataStore
import com.example.movieindex.util.MainCoroutineRule
import com.example.movieindex.util.TestDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*
import kotlin.random.Random.Default.nextInt

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class CacheDataSourceTest {

    @get:Rule
    val mainDispatcherRule = MainCoroutineRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    private lateinit var testDataFactory: TestDataFactory
    private lateinit var dao: MovieDao
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var cache: CacheDataSource

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var testScope: TestScope

    @Before
    fun setup() {
        testDataFactory = TestDataFactory()
        testDispatcher = mainDispatcherRule.testDispatcher
        testScope = TestScope(testDispatcher)
        dataStore = FakePreferenceDataStore(context,
            testScope).testDataStore
        dao = FakeMovieDao()
        cache = CacheDataSourceImpl(
            movieDao = dao, dataStore = dataStore, ioDispatcher = testDispatcher)
    }

    @Test
    fun insertMovie_getMovie() = runTest {
        val data = testDataFactory.generatePopularMoviesTestData().toMovies().results.random()
            .toMovieEntity()
        dao.insertMovie(movie = data)

        val cachedData = cache.getMovie(data.movieId).first()
        assertEquals(data, cachedData)
    }

    @Test
    fun getFavoriteMovies() = runTest {
        val data =
            testDataFactory.generatePopularMoviesTestData().toMovies().results.shuffled().take(10)
                .mapIndexed { index, movie ->
                    movie.toMovieEntity(isFavorite = index % 2 == 0)
                }

        data.forEach {
            dao.insertMovie(it)
        }

        val cachedData = cache.getFavoriteMovies().first()

        assertEquals(data.filter { it.isFavorite }, cachedData)
    }

    @Test
    fun getBookmarkedMovies() = runTest {
        val data =
            testDataFactory.generatePopularMoviesTestData().toMovies().results.shuffled().take(10)
                .mapIndexed { index, movie ->
                    movie.toMovieEntity(isBookmark = index % 2 == 0)
                }

        data.forEach {
            dao.insertMovie(it)
        }

        val cachedData = cache.getBookmarkedMovies().first()

        assertEquals(data.filter { it.isBookmark }, cachedData)
    }

    @Test
    fun updateBookmark() = runTest {
        val data =
            testDataFactory.generatePopularMoviesTestData().toMovies().results.shuffled().take(10)
                .mapIndexed { index, movie ->
                    movie.toMovieEntity(isBookmark = index % 2 == 0)
                }

        data.forEach {
            dao.insertMovie(it)
        }

        val dataToUpdate = data.random()

        dao.updateBookmark(dataToUpdate.movieId, isBookmark = !dataToUpdate.isBookmark)

        val cachedData = cache.getMovie(dataToUpdate.movieId).first()

        assertEquals(!dataToUpdate.isBookmark, cachedData?.isBookmark)
    }

    @Test
    fun updateFavorite() = runTest {
        val data =
            testDataFactory.generatePopularMoviesTestData().toMovies().results.shuffled().take(10)
                .mapIndexed { index, movie ->
                    movie.toMovieEntity(isFavorite = index % 2 == 0)
                }

        data.forEach {
            dao.insertMovie(it)
        }

        val dataToUpdate = data.random()

        dao.updateFavorite(dataToUpdate.movieId, isFavorite = !dataToUpdate.isFavorite)

        val cachedData = cache.getMovie(dataToUpdate.movieId).first()

        assertEquals(!dataToUpdate.isFavorite, cachedData?.isFavorite)
    }

    @Test
    fun deleteMovie() = runTest {
        val data =
            testDataFactory.generatePopularMoviesTestData().toMovies().results.shuffled().take(10)
                .mapIndexed { index, movie ->
                    movie.toMovieEntity(isFavorite = index % 2 == 0)
                }

        data.forEach {
            dao.insertMovie(it)
        }

        val dataToDelete = data.random()

        dao.deleteMovie(dataToDelete.movieId)

        val cachedData = cache.getMovie(movieId = dataToDelete.movieId).first()

        assertNull(cachedData)
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
    fun saveAccountId_getAccountId() = runTest {
        val accountId = nextInt(until = 99_999_999)
        cache.saveAccountId(accountId = accountId)
        val cachedData = cache.getAccountId().first()

        assertEquals(accountId, cachedData)
    }

}