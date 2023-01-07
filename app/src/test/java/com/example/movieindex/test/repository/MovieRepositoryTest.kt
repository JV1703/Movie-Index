package com.example.movieindex.test.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.paging.PagingSource
import androidx.test.core.app.ApplicationProvider
import com.example.movieindex.core.data.external.Resource
import com.example.movieindex.core.data.local.abstraction.CacheDataSource
import com.example.movieindex.core.data.remote.NetworkResource
import com.example.movieindex.core.data.remote.model.common.MoviesResponse
import com.example.movieindex.core.data.remote.model.common.toResult
import com.example.movieindex.core.data.remote.model.details.MovieDetailsResponse
import com.example.movieindex.core.data.remote.model.details.toMovieDetails
import com.example.movieindex.core.repository.abstraction.MovieRepository
import com.example.movieindex.core.repository.implementation.MovieRepositoryImpl
import com.example.movieindex.core.repository.implementation.networkResourceHandler
import com.example.movieindex.core.repository.paging.MoviesPagingSource
import com.example.movieindex.fake.data_source.FakeCacheDataSource
import com.example.movieindex.fake.data_source.FakeNetworkDataSource
import com.example.movieindex.fake.datastore.FakePreferenceDataStore
import com.example.movieindex.util.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestDispatcher
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
class MovieRepositoryTest {

    @get:Rule
    val mainDispatcherRule = MainCoroutineRule()

    private lateinit var network: FakeNetworkDataSource
    private lateinit var cache: CacheDataSource

    private lateinit var dataStore: DataStore<Preferences>

    private lateinit var repository: MovieRepository

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var testScope: TestScope

    @Before
    fun setup() {
        testDispatcher = mainDispatcherRule.testDispatcher
        testScope = TestScope(testDispatcher)
        dataStore = FakePreferenceDataStore(ApplicationProvider.getApplicationContext(),
            testScope).testDataStore
        network = FakeNetworkDataSource(testDispatcher)
        cache = FakeCacheDataSource(testDispatcher = testDispatcher, dataStore = dataStore)
        repository = MovieRepositoryImpl(network = network, cache = cache)
    }

    @Test
    fun getNowPlaying_Loading() = runTest {
        val actualResult = repository.getNowPlaying().first()

        assertTrue(actualResult is Resource.Loading)
    }

    @Test
    fun getNowPlaying_Success() = runTest {
        val data = network.getNowPlaying()
        val expectedResult = networkResourceHandler(data) { moviesResponse: MoviesResponse ->
            moviesResponse.results.map { it.toResult() }
        }

        val resultList = repository.getNowPlaying().toList()
        val actualResult = resultList[1]
        assertEquals(2, resultList.size)
        assertTrue(actualResult is Resource.Success)
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun getNowPlaying_Error() = runTest {

        network.isSuccess = false

        val data = network.getNowPlaying()
        val expectedResult = networkResourceHandler(data) { moviesResponse: MoviesResponse ->
            moviesResponse.results.map { it.toResult() }
        }

        val resultList = repository.getNowPlaying().toList()
        val actualResult = resultList[1]
        assertEquals(2, resultList.size)
        assertTrue(actualResult is Resource.Error)
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun getNowPlaying_Empty() = runTest {

        network.isBodyEmpty = true

        val resultList = repository.getNowPlaying().toList()
        val actualResult = resultList[1]
        assertEquals(2, resultList.size)
        assertTrue(actualResult is Resource.Empty)
    }

    @Test
    fun getPopularMovies_Loading() = runTest {
        val actualResult = repository.getPopularMovies().first()

        assertTrue(actualResult is Resource.Loading)
    }

    @Test
    fun getPopularMovies_Success() = runTest {
        val data = network.getPopularMovies()
        val expectedResult = networkResourceHandler(data) { moviesResponse: MoviesResponse ->
            moviesResponse.results.map { it.toResult() }
        }

        val resultList = repository.getPopularMovies().toList()
        val actualResult = resultList[1]
        assertEquals(2, resultList.size)
        assertTrue(actualResult is Resource.Success)
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun getPopularMovies_Error() = runTest {

        network.isSuccess = false

        val data = network.getPopularMovies()
        val expectedResult = networkResourceHandler(data) { moviesResponse: MoviesResponse ->
            moviesResponse.results.map { it.toResult() }
        }

        val resultList = repository.getPopularMovies().toList()
        val actualResult = resultList[1]
        assertEquals(2, resultList.size)
        assertTrue(actualResult is Resource.Error)
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun getPopularMovies_Empty() = runTest {

        network.isBodyEmpty = true

        val resultList = repository.getPopularMovies().toList()
        val actualResult = resultList[1]
        assertEquals(2, resultList.size)
        assertTrue(actualResult is Resource.Empty)
    }

    @Test
    fun getTrendingMovies_Loading() = runTest {
        val actualResult = repository.getTrendingMovies().first()

        assertTrue(actualResult is Resource.Loading)
    }

    @Test
    fun getTrendingMovies_Success() = runTest {
        val data = network.getTrendingMovies()
        val expectedResult = networkResourceHandler(data) { moviesResponse: MoviesResponse ->
            moviesResponse.results.map { it.toResult() }
        }

        val resultList = repository.getTrendingMovies().toList()
        val actualResult = resultList[1]
        assertEquals(2, resultList.size)
        assertTrue(actualResult is Resource.Success)
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun getTrendingMovies_Error() = runTest {

        network.isSuccess = false

        val data = network.getTrendingMovies()
        val expectedResult = networkResourceHandler(data) { moviesResponse: MoviesResponse ->
            moviesResponse.results.map { it.toResult() }
        }

        val resultList = repository.getTrendingMovies().toList()
        val actualResult = resultList[1]
        assertEquals(2, resultList.size)
        assertTrue(actualResult is Resource.Error)
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun getTrendingMovies_Empty() = runTest {

        network.isBodyEmpty = true

        val resultList = repository.getTrendingMovies().toList()
        val actualResult = resultList[1]
        assertEquals(2, resultList.size)
        assertTrue(actualResult is Resource.Empty)
    }

    @Test
    fun getMovieRecommendations_Loading() = runTest {
        val actualResult = repository.getMovieRecommendations(movieId = 0).first()

        assertTrue(actualResult is Resource.Loading)
    }

    @Test
    fun getMovieRecommendations_Success() = runTest {
        val data = network.getMovieRecommendations(movieId = 0)
        val expectedResult = networkResourceHandler(data) { moviesResponse: MoviesResponse ->
            moviesResponse.results.map { it.toResult() }
        }

        val resultList = repository.getMovieRecommendations(movieId = 0).toList()
        val actualResult = resultList[1]
        assertEquals(2, resultList.size)
        assertTrue(actualResult is Resource.Success)
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun getMovieRecommendations_Error() = runTest {

        network.isSuccess = false

        val data = network.getMovieRecommendations(movieId = 0)
        val expectedResult = networkResourceHandler(data) { moviesResponse: MoviesResponse ->
            moviesResponse.results.map { it.toResult() }
        }

        val resultList = repository.getMovieRecommendations(movieId = 0).toList()
        val actualResult = resultList[1]
        assertEquals(2, resultList.size)
        assertTrue(actualResult is Resource.Error)
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun getMovieRecommendations_Empty() = runTest {

        network.isBodyEmpty = true

        val resultList = repository.getMovieRecommendations(movieId = 0).toList()
        val actualResult = resultList[1]
        assertEquals(2, resultList.size)
        assertTrue(actualResult is Resource.Empty)
    }

    @Test
    fun getMovieDetails_Loading() = runTest {
        val actualResult = repository.getMovieDetails(movieId = 0).first()

        assertTrue(actualResult is Resource.Loading)
    }

    @Test
    fun getMovieDetails_Success() = runTest {
        val data = network.getMovieDetails(movieId = 0)
        val expectedResult = networkResourceHandler(data) { movieDetails: MovieDetailsResponse ->
            movieDetails.toMovieDetails()
        }

        val resultList = repository.getMovieDetails(movieId = 0).toList()
        val actualResult = resultList[1]
        assertEquals(2, resultList.size)
        assertTrue(actualResult is Resource.Success)
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun getMovieDetails_Error() = runTest {

        network.isSuccess = false

        val data = network.getMovieDetails(movieId = 0)
        val expectedResult = networkResourceHandler(data) { movieDetails: MovieDetailsResponse ->
            movieDetails.toMovieDetails()
        }

        val resultList = repository.getMovieDetails(movieId = 0).toList()
        val actualResult = resultList[1]
        assertEquals(2, resultList.size)
        assertTrue(actualResult is Resource.Error)
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun getMovieDetails_Empty() = runTest {

        network.isBodyEmpty = true

        val resultList = repository.getMovieDetails(movieId = 0).toList()
        val actualResult = resultList[1]
        assertEquals(2, resultList.size)
        assertTrue(actualResult is Resource.Empty)
    }

    @Test
    fun getNowPlayingPagingSource_loadSinglePage_true_load_returnsCorrectDataAndKeys() =
        runTest {
            val dataPage1 = (network.getNowPlaying() as NetworkResource.Success).data

            val pagingSource = MoviesPagingSource(true, networkCall = { network.getNowPlaying() })
            val expected = PagingSource.LoadResult.Page(data = dataPage1.results,
                prevKey = null,
                nextKey = null)
            val actual = pagingSource.load(PagingSource.LoadParams.Refresh(key = null,
                loadSize = 20,
                placeholdersEnabled = false))

            assertEquals(expected, actual)
        }

    @Test
    fun getNowPlayingPagingSource_loadSinglePage_false_load_returnsCorrectDataAndKeys() =
        runTest {
            val dataPage1 = (network.getNowPlaying() as NetworkResource.Success).data
            val dataPage2 = (network.getNowPlaying() as NetworkResource.Success).data.copy(page = 2)

            val pagingSource = MoviesPagingSource(false, networkCall = { network.getNowPlaying() })
            val expected = PagingSource.LoadResult.Page(data = dataPage1.results,
                prevKey = null,
                nextKey = dataPage2.page)
            val actual = pagingSource.load(PagingSource.LoadParams.Refresh(key = null,
                loadSize = 20,
                placeholdersEnabled = false))

            assertEquals(expected, actual)
        }

    @Test
    fun getNowPlayingPagingSource_Error() = runTest {

        network.isSuccess = false

        val pagingSource = MoviesPagingSource(false, networkCall = { network.getNowPlaying() })
        val actual = pagingSource.load(PagingSource.LoadParams.Refresh(key = null,
            loadSize = 20,
            placeholdersEnabled = false))

        assertTrue(actual is PagingSource.LoadResult.Error)
    }

    @Test
    fun getPopularMoviesPagingSource_loadSinglePage_true_load_returnsCorrectDataAndKeys() =
        runTest {
            val dataPage1 = (network.getPopularMovies() as NetworkResource.Success).data

            val pagingSource =
                MoviesPagingSource(true, networkCall = { network.getPopularMovies() })
            val expected = PagingSource.LoadResult.Page(data = dataPage1.results,
                prevKey = null,
                nextKey = null)
            val actual = pagingSource.load(PagingSource.LoadParams.Refresh(key = null,
                loadSize = 20,
                placeholdersEnabled = false))

            assertEquals(expected, actual)
        }

    @Test
    fun getPopularMoviesPagingSource_loadSinglePage_false_load_returnsCorrectDataAndKeys() =
        runTest {
            val dataPage1 = (network.getPopularMovies() as NetworkResource.Success).data
            val dataPage2 =
                (network.getPopularMovies() as NetworkResource.Success).data.copy(page = 2)

            val pagingSource =
                MoviesPagingSource(false, networkCall = { network.getPopularMovies() })
            val expected = PagingSource.LoadResult.Page(data = dataPage1.results,
                prevKey = null,
                nextKey = dataPage2.page)
            val actual = pagingSource.load(PagingSource.LoadParams.Refresh(key = null,
                loadSize = 20,
                placeholdersEnabled = false))

            assertEquals(expected, actual)
        }

    @Test
    fun getPopularMoviesPagingSource_Error() = runTest {

        network.isSuccess = false

        val pagingSource = MoviesPagingSource(false, networkCall = { network.getPopularMovies() })
        val actual = pagingSource.load(PagingSource.LoadParams.Refresh(key = null,
            loadSize = 20,
            placeholdersEnabled = false))

        assertTrue(actual is PagingSource.LoadResult.Error)
    }

    @Test
    fun getTrendingMoviesPagingSource_loadSinglePage_true_load_returnsCorrectDataAndKeys() =
        runTest {
            val dataPage1 = (network.getTrendingMovies() as NetworkResource.Success).data

            val pagingSource =
                MoviesPagingSource(true, networkCall = { network.getTrendingMovies() })
            val expected = PagingSource.LoadResult.Page(data = dataPage1.results,
                prevKey = null,
                nextKey = null)
            val actual = pagingSource.load(PagingSource.LoadParams.Refresh(key = null,
                loadSize = 20,
                placeholdersEnabled = false))

            assertEquals(expected, actual)
        }

    @Test
    fun getTrendingMoviesPagingSource_loadSinglePage_false_load_returnsCorrectDataAndKeys() =
        runTest {
            val dataPage1 = (network.getTrendingMovies() as NetworkResource.Success).data
            val dataPage2 =
                (network.getTrendingMovies() as NetworkResource.Success).data.copy(page = 2)

            val pagingSource =
                MoviesPagingSource(false, networkCall = { network.getTrendingMovies() })
            val expected = PagingSource.LoadResult.Page(data = dataPage1.results,
                prevKey = null,
                nextKey = dataPage2.page)
            val actual = pagingSource.load(PagingSource.LoadParams.Refresh(key = null,
                loadSize = 20,
                placeholdersEnabled = false))

            assertEquals(expected, actual)
        }

    @Test
    fun getTrendingMoviesPagingSource_Error() = runTest {

        network.isSuccess = false

        val pagingSource = MoviesPagingSource(false, networkCall = { network.getTrendingMovies() })
        val actual = pagingSource.load(PagingSource.LoadParams.Refresh(key = null,
            loadSize = 20,
            placeholdersEnabled = false))

        assertTrue(actual is PagingSource.LoadResult.Error)
    }

    @Test
    fun searchMoviesPagingSource_loadSinglePage_true_load_returnsCorrectDataAndKeys() =
        runTest {
            val dataPage1 = (network.searchMovies(query = "") as NetworkResource.Success).data

            val pagingSource =
                MoviesPagingSource(true, networkCall = { network.searchMovies(query = "") })
            val expected = PagingSource.LoadResult.Page(data = dataPage1.results,
                prevKey = null,
                nextKey = null)
            val actual = pagingSource.load(PagingSource.LoadParams.Refresh(key = null,
                loadSize = 20,
                placeholdersEnabled = false))

            assertEquals(expected, actual)
        }

    @Test
    fun searchMoviesPagingSource_loadSinglePage_false_load_returnsCorrectDataAndKeys() =
        runTest {
            val dataPage1 = (network.searchMovies(query = "") as NetworkResource.Success).data
            val dataPage2 =
                (network.searchMovies(query = "") as NetworkResource.Success).data.copy(page = 2)

            val pagingSource =
                MoviesPagingSource(false, networkCall = { network.searchMovies(query = "") })
            val expected = PagingSource.LoadResult.Page(data = dataPage1.results,
                prevKey = null,
                nextKey = dataPage2.page)
            val actual = pagingSource.load(PagingSource.LoadParams.Refresh(key = null,
                loadSize = 20,
                placeholdersEnabled = false))

            assertEquals(expected, actual)
        }

    @Test
    fun searchMoviesPagingSource_Error() = runTest {

        network.isSuccess = false

        val pagingSource =
            MoviesPagingSource(false, networkCall = { network.searchMovies(query = "") })
        val actual = pagingSource.load(PagingSource.LoadParams.Refresh(key = null,
            loadSize = 20,
            placeholdersEnabled = false))

        assertTrue(actual is PagingSource.LoadResult.Error)
    }

    @Test
    fun getMovieRecommendationPagingSource_loadSinglePage_true_load_returnsCorrectDataAndKeys() =
        runTest {
            val dataPage1 =
                (network.getMovieRecommendations(movieId = 0) as NetworkResource.Success).data

            val pagingSource = MoviesPagingSource(true,
                networkCall = { network.getMovieRecommendations(movieId = 0) })
            val expected = PagingSource.LoadResult.Page(data = dataPage1.results,
                prevKey = null,
                nextKey = null)
            val actual = pagingSource.load(PagingSource.LoadParams.Refresh(key = null,
                loadSize = 20,
                placeholdersEnabled = false))

            assertEquals(expected, actual)
        }

    @Test
    fun getMovieRecommendationPagingSource_loadSinglePage_false_load_returnsCorrectDataAndKeys() =
        runTest {
            val dataPage1 =
                (network.getMovieRecommendations(movieId = 0) as NetworkResource.Success).data
            val dataPage2 =
                (network.getMovieRecommendations(movieId = 0) as NetworkResource.Success).data.copy(
                    page = 2)

            val pagingSource = MoviesPagingSource(false,
                networkCall = { network.getMovieRecommendations(movieId = 0) })
            val expected = PagingSource.LoadResult.Page(data = dataPage1.results,
                prevKey = null,
                nextKey = dataPage2.page)
            val actual = pagingSource.load(PagingSource.LoadParams.Refresh(key = null,
                loadSize = 20,
                placeholdersEnabled = false))

            assertEquals(expected, actual)
        }

    @Test
    fun getMovieRecommendationPagingSource_Error() = runTest {

        network.isSuccess = false

        val pagingSource = MoviesPagingSource(false,
            networkCall = { network.getMovieRecommendations(movieId = 0) })
        val actual = pagingSource.load(PagingSource.LoadParams.Refresh(key = null,
            loadSize = 20,
            placeholdersEnabled = false))

        assertTrue(actual is PagingSource.LoadResult.Error)
    }

    @Test
    fun saveCasts_getCasts() = runTest {
        val casts =
            (network.getMovieDetails(movieId = 0) as NetworkResource.Success).data.toMovieDetails().casts
        repository.saveCasts(casts)

        val cachedData = repository.getCasts().first()

        assertEquals(casts, cachedData)
    }

    @Test
    fun saveCrews_getCrews() = runTest {
        val crews =
            (network.getMovieDetails(movieId = 0) as NetworkResource.Success).data.toMovieDetails().crews
        repository.saveCrews(crews)

        val cachedData = repository.getCrews().first()

        assertEquals(crews, cachedData)
    }

}