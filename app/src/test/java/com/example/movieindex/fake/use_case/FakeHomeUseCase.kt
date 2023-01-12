package com.example.movieindex.fake.use_case

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.movieindex.core.data.external.model.Resource
import com.example.movieindex.core.data.external.model.Result
import com.example.movieindex.core.data.external.model.networkResourceHandler
import com.example.movieindex.core.data.remote.model.common.toResult
import com.example.movieindex.core.data.remote.safeNetworkCall
import com.example.movieindex.feature.main.ui.home.domain.abstraction.HomeUseCase
import com.example.movieindex.util.TestDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class FakeHomeUseCase(
    private val testDispatcher: TestDispatcher,
    private val dataStore: DataStore<Preferences>,
    private val testDataFactory: TestDataFactory,
) : HomeUseCase {

    var isSuccess = true
    var isBodyEmpty = false

    override suspend fun getNowPlaying(
        page: Int,
        language: String?,
        region: String?,
    ): Resource<List<Result>> {
        val data = testDataFactory.generateNowPlayingMoviesTestData()
        val response = testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(data) }
        val networkResource = safeNetworkCall(dispatcher = testDispatcher,
            networkCall = { response },
            conversion = { it.results.map { it.toResult() } })
        return networkResourceHandler(networkResource = networkResource, conversion = { it })
    }

    override suspend fun getPopularMovies(
        page: Int,
        language: String?,
        region: String?,
    ): Resource<List<Result>> {
        val data = testDataFactory.generatePopularMoviesTestData()
        val response = testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(data) }
        val networkResource = safeNetworkCall(dispatcher = testDispatcher,
            networkCall = { response },
            conversion = { it.results.map { it.toResult() } })
        return networkResourceHandler(networkResource = networkResource, conversion = { it })
    }

    override suspend fun getTrendingMovies(
        page: Int,
        mediaType: String,
        timeWindow: String,
    ): Resource<List<Result>> {
        val data = testDataFactory.generateTrendingMoviesTestData()
        val response = testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(data) }
        val networkResource = safeNetworkCall(dispatcher = testDispatcher,
            networkCall = { response },
            conversion = { it.results.map { it.toResult() } })
        return networkResourceHandler(networkResource = networkResource, conversion = { it })
    }
}