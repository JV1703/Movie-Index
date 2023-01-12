package com.example.movieindex.test.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.paging.PagingSource
import androidx.test.core.app.ApplicationProvider
import androidx.work.*
import com.example.movieindex.core.data.external.model.Resource
import com.example.movieindex.core.data.local.abstraction.CacheDataSource
import com.example.movieindex.core.data.remote.NetworkResource
import com.example.movieindex.core.data.remote.model.details.toMovieDetails
import com.example.movieindex.core.repository.abstraction.MovieRepository
import com.example.movieindex.core.repository.implementation.MovieRepositoryImpl
import com.example.movieindex.core.repository.paging.MoviesPagingSource
import com.example.movieindex.fake.data_source.FakeCacheDataSource
import com.example.movieindex.fake.data_source.FakeNetworkDataSource
import com.example.movieindex.fake.datastore.FakePreferenceDataStore
import com.example.movieindex.util.MainCoroutineRule
import com.example.movieindex.util.TestDataFactory
import com.example.movieindex.util.TestWorkManagerUtil
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

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class MovieRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainCoroutineRule()

    private val context: Context = ApplicationProvider.getApplicationContext()
    private lateinit var network: FakeNetworkDataSource
    private lateinit var cache: CacheDataSource
    private lateinit var testWorkManagerUtil: TestWorkManagerUtil
    private lateinit var workManager: WorkManager
    private lateinit var testDataFactory: TestDataFactory

    private lateinit var dataStore: DataStore<Preferences>

    private lateinit var repository: MovieRepository

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
        testWorkManagerUtil = TestWorkManagerUtil(context)
        testWorkManagerUtil.setupWorkManager()
        workManager = WorkManager.getInstance(context)
        cache = FakeCacheDataSource(testDispatcher = testDispatcher, dataStore = dataStore)
        repository =
            MovieRepositoryImpl(network = network, cache = cache, workManager = workManager)
    }

    @Test
    fun getNowPlaying_Success() = runTest {
        val apiCall = repository.getNowPlaying()
        assertTrue(apiCall is Resource.Success)
    }

    @Test
    fun getNowPlaying_Empty() = runTest {
        network.isBodyEmpty = true
        val apiCall = repository.getNowPlaying()
        assertTrue(apiCall is Resource.Empty)
    }

    @Test
    fun getNowPlaying_Error() = runTest {
        network.isSuccess = false
        val apiCall = repository.getNowPlaying()
        assertTrue(apiCall is Resource.Error)
    }

    @Test
    fun getPopularMovies_Success() = runTest {
        val apiCall = repository.getPopularMovies()
        assertTrue(apiCall is Resource.Success)
    }

    @Test
    fun getPopularMovies_Empty() = runTest {
        network.isBodyEmpty = true
        val apiCall = repository.getPopularMovies()
        assertTrue(apiCall is Resource.Empty)
    }

    @Test
    fun getPopularMovies_Error() = runTest {
        network.isSuccess = false
        val apiCall = repository.getPopularMovies()
        assertTrue(apiCall is Resource.Error)
    }

    @Test
    fun getTrendingMovies_Success() = runTest {
        val apiCall = repository.getTrendingMovies()
        assertTrue(apiCall is Resource.Success)
    }

    @Test
    fun getTrendingMovies_Empty() = runTest {
        network.isBodyEmpty = true
        val apiCall = repository.getTrendingMovies()
        assertTrue(apiCall is Resource.Empty)
    }

    @Test
    fun getTrendingMovies_Error() = runTest {
        network.isSuccess = false
        val apiCall = repository.getTrendingMovies()
        assertTrue(apiCall is Resource.Error)
    }

    @Test
    fun getMovieRecommendations_Success() = runTest {
        val apiCall = repository.getMovieRecommendations(movieId = 0)
        assertTrue(apiCall is Resource.Success)
    }

    @Test
    fun getMovieRecommendations_Empty() = runTest {
        network.isBodyEmpty = true
        val apiCall = repository.getMovieRecommendations(movieId = 0)
        assertTrue(apiCall is Resource.Empty)
    }

    @Test
    fun getMovieRecommendations_Error() = runTest {
        network.isSuccess = false
        val apiCall = repository.getMovieRecommendations(movieId = 0)
        assertTrue(apiCall is Resource.Error)
    }

    @Test
    fun getMovieDetails_Success() = runTest {
        val apiCall = repository.getMovieDetails(movieId = 0)
        assertTrue(apiCall is Resource.Success)
    }

    @Test
    fun getMovieDetails_Empty() = runTest {
        network.isBodyEmpty = true
        val apiCall = repository.getMovieDetails(movieId = 0)
        assertTrue(apiCall is Resource.Empty)
    }

    @Test
    fun getMovieDetails_Error() = runTest {
        network.isSuccess = false
        val apiCall = repository.getMovieDetails(movieId = 0)
        assertTrue(apiCall is Resource.Error)
    }

    @Test
    fun saveCasts_getCasts() = runTest {
        val casts = testDataFactory.generateMovieDetailsTestData().toMovieDetails().casts
        repository.saveCasts(casts)

        val cachedData = repository.getCasts().first()
        assertEquals(casts, cachedData)
    }

    @Test
    fun saveCrews_getCrews() = runTest {
        val crews = testDataFactory.generateMovieDetailsTestData().toMovieDetails().crews
        repository.saveCrews(crews)

        val cachedData = repository.getCrews().first()
        assertEquals(crews, cachedData)
    }

    @Test
    fun getNowPlayingPagingSource_Success_loadSinglePage_False() = runTest {
        val dataPage1 = (network.getNowPlaying() as NetworkResource.Success)
        val dataPage2 = (network.getNowPlaying() as NetworkResource.Success)

        val pagingSource = MoviesPagingSource(loadSinglePage = false, networkCall = { dataPage1 })
        val expected = PagingSource.LoadResult.Page(data = dataPage1.data.results,
            prevKey = null,
            nextKey = dataPage2.data.copy(page = 2).page)
        val actual = pagingSource.load(PagingSource.LoadParams.Refresh(key = null,
            loadSize = 20,
            placeholdersEnabled = false))

        assertEquals(expected, actual)
    }

    @Test
    fun getNowPlayingPagingSource_Success_loadSinglePage_True() = runTest {
        val dataPage1 = (network.getNowPlaying() as NetworkResource.Success)

        val pagingSource = MoviesPagingSource(loadSinglePage = true, networkCall = { dataPage1 })
        val expected = PagingSource.LoadResult.Page(data = dataPage1.data.results,
            prevKey = null,
            nextKey = null)
        val actual = pagingSource.load(PagingSource.LoadParams.Refresh(key = null,
            loadSize = 20,
            placeholdersEnabled = false))

        assertEquals(expected, actual)
    }

    @Test
    fun getNowPlayingPagingSource_Error() = runTest {

        network.isSuccess = false

        val data = network.getNowPlaying()

        val pagingSource = MoviesPagingSource(loadSinglePage = true, networkCall = { data })
        val actual = pagingSource.load(PagingSource.LoadParams.Refresh(key = null,
            loadSize = 20,
            placeholdersEnabled = false))

        assertTrue(actual is PagingSource.LoadResult.Error)

    }

    @Test
    fun getPopularMoviesPagingSource_Success_loadSinglePage_False() = runTest {
        val dataPage1 = (network.getPopularMovies() as NetworkResource.Success)
        val dataPage2 = (network.getPopularMovies() as NetworkResource.Success)

        val pagingSource = MoviesPagingSource(loadSinglePage = false, networkCall = { dataPage1 })
        val expected = PagingSource.LoadResult.Page(data = dataPage1.data.results,
            prevKey = null,
            nextKey = dataPage2.data.copy(page = 2).page)
        val actual = pagingSource.load(PagingSource.LoadParams.Refresh(key = null,
            loadSize = 20,
            placeholdersEnabled = false))

        assertEquals(expected, actual)
    }

    @Test
    fun getPopularMoviesPagingSource_Success_loadSinglePage_True() = runTest {
        val dataPage1 = (network.getPopularMovies() as NetworkResource.Success)

        val pagingSource = MoviesPagingSource(loadSinglePage = true, networkCall = { dataPage1 })
        val expected = PagingSource.LoadResult.Page(data = dataPage1.data.results,
            prevKey = null,
            nextKey = null)
        val actual = pagingSource.load(PagingSource.LoadParams.Refresh(key = null,
            loadSize = 20,
            placeholdersEnabled = false))

        assertEquals(expected, actual)
    }

    @Test
    fun getPopularMoviesPagingSource_Error() = runTest {

        network.isSuccess = false

        val data = network.getPopularMovies()

        val pagingSource = MoviesPagingSource(loadSinglePage = true, networkCall = { data })
        val actual = pagingSource.load(PagingSource.LoadParams.Refresh(key = null,
            loadSize = 20,
            placeholdersEnabled = false))

        assertTrue(actual is PagingSource.LoadResult.Error)

    }

    @Test
    fun getTrendingMoviesPagingSource_Success_loadSinglePage_False() = runTest {
        val dataPage1 = (network.getTrendingMovies() as NetworkResource.Success)
        val dataPage2 = (network.getTrendingMovies() as NetworkResource.Success)

        val pagingSource = MoviesPagingSource(loadSinglePage = false, networkCall = { dataPage1 })
        val expected = PagingSource.LoadResult.Page(data = dataPage1.data.results,
            prevKey = null,
            nextKey = dataPage2.data.copy(page = 2).page)
        val actual = pagingSource.load(PagingSource.LoadParams.Refresh(key = null,
            loadSize = 20,
            placeholdersEnabled = false))

        assertEquals(expected, actual)
    }

    @Test
    fun getTrendingMoviesPagingSource_Success_loadSinglePage_True() = runTest {
        val dataPage1 = (network.getTrendingMovies() as NetworkResource.Success)

        val pagingSource = MoviesPagingSource(loadSinglePage = true, networkCall = { dataPage1 })
        val expected = PagingSource.LoadResult.Page(data = dataPage1.data.results,
            prevKey = null,
            nextKey = null)
        val actual = pagingSource.load(PagingSource.LoadParams.Refresh(key = null,
            loadSize = 20,
            placeholdersEnabled = false))

        assertEquals(expected, actual)
    }

    @Test
    fun getTrendingMoviesPagingSource_Error() = runTest {

        network.isSuccess = false

        val data = network.getTrendingMovies()

        val pagingSource = MoviesPagingSource(loadSinglePage = true, networkCall = { data })
        val actual = pagingSource.load(PagingSource.LoadParams.Refresh(key = null,
            loadSize = 20,
            placeholdersEnabled = false))

        assertTrue(actual is PagingSource.LoadResult.Error)

    }

    @Test
    fun searchMoviesPagingSource_Success_loadSinglePage_False() = runTest {
        val dataPage1 = (network.searchMovies(query = "") as NetworkResource.Success)
        val dataPage2 = (network.searchMovies(query = "") as NetworkResource.Success)

        val pagingSource = MoviesPagingSource(loadSinglePage = false, networkCall = { dataPage1 })
        val expected = PagingSource.LoadResult.Page(data = dataPage1.data.results,
            prevKey = null,
            nextKey = dataPage2.data.copy(page = 2).page)
        val actual = pagingSource.load(PagingSource.LoadParams.Refresh(key = null,
            loadSize = 20,
            placeholdersEnabled = false))

        assertEquals(expected, actual)
    }

    @Test
    fun searchMoviesPagingSource_Success_loadSinglePage_True() = runTest {
        val dataPage1 = (network.searchMovies(query = "") as NetworkResource.Success)

        val pagingSource = MoviesPagingSource(loadSinglePage = true, networkCall = { dataPage1 })
        val expected = PagingSource.LoadResult.Page(data = dataPage1.data.results,
            prevKey = null,
            nextKey = null)
        val actual = pagingSource.load(PagingSource.LoadParams.Refresh(key = null,
            loadSize = 20,
            placeholdersEnabled = false))

        assertEquals(expected, actual)
    }

    @Test
    fun searchMoviesPagingSource_Error() = runTest {

        network.isSuccess = false

        val data = network.searchMovies(query = "")

        val pagingSource = MoviesPagingSource(loadSinglePage = true, networkCall = { data })
        val actual = pagingSource.load(PagingSource.LoadParams.Refresh(key = null,
            loadSize = 20,
            placeholdersEnabled = false))

        assertTrue(actual is PagingSource.LoadResult.Error)

    }

    @Test
    fun getMovieRecommendationsPagingSource_Success_loadSinglePage_False() = runTest {
        val dataPage1 = (network.getMovieRecommendations(movieId = 0) as NetworkResource.Success)
        val dataPage2 = (network.getMovieRecommendations(movieId = 0) as NetworkResource.Success)

        val pagingSource = MoviesPagingSource(loadSinglePage = false, networkCall = { dataPage1 })
        val expected = PagingSource.LoadResult.Page(data = dataPage1.data.results,
            prevKey = null,
            nextKey = dataPage2.data.copy(page = 2).page)
        val actual = pagingSource.load(PagingSource.LoadParams.Refresh(key = null,
            loadSize = 20,
            placeholdersEnabled = false))

        assertEquals(expected, actual)
    }

    @Test
    fun getMovieRecommendationsPagingSource_Success_loadSinglePage_True() = runTest {
        val dataPage1 = (network.getMovieRecommendations(movieId = 0) as NetworkResource.Success)

        val pagingSource = MoviesPagingSource(loadSinglePage = true, networkCall = { dataPage1 })
        val expected = PagingSource.LoadResult.Page(data = dataPage1.data.results,
            prevKey = null,
            nextKey = null)
        val actual = pagingSource.load(PagingSource.LoadParams.Refresh(key = null,
            loadSize = 20,
            placeholdersEnabled = false))

        assertEquals(expected, actual)
    }

    @Test
    fun getMovieRecommendationsPagingSource_Error() = runTest {

        network.isSuccess = false

        val data = network.getMovieRecommendations(movieId = 0)

        val pagingSource = MoviesPagingSource(loadSinglePage = true, networkCall = { data })
        val actual = pagingSource.load(PagingSource.LoadParams.Refresh(key = null,
            loadSize = 20,
            placeholdersEnabled = false))

        assertTrue(actual is PagingSource.LoadResult.Error)

    }

}