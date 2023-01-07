package com.example.movieindex.core.repository.implementation

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.movieindex.core.data.external.*
import com.example.movieindex.core.data.local.abstraction.CacheDataSource
import com.example.movieindex.core.data.remote.NetworkResource
import com.example.movieindex.core.data.remote.abstraction.NetworkDataSource
import com.example.movieindex.core.data.remote.model.common.MoviesResponse
import com.example.movieindex.core.data.remote.model.common.toResult
import com.example.movieindex.core.data.remote.model.details.MovieDetailsResponse
import com.example.movieindex.core.data.remote.model.details.toMovieDetails
import com.example.movieindex.core.repository.abstraction.MovieRepository
import com.example.movieindex.core.repository.paging.MoviesPagingSource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

fun <T, R> networkResourceHandler(
    networkResource: NetworkResource<T>,
    conversion: (T) -> R,
): Resource<R> {
    return when (networkResource) {
        is NetworkResource.Success -> {
            val data = networkResource.data
            val convertedData = conversion(data)
            Resource.Success(convertedData)
        }
        is NetworkResource.Error -> {
            Resource.Error(errMsg = "errCode: ${networkResource.errCode}, errMsg: ${networkResource.errMessage}",
                errCode = networkResource.errCode)
        }
        is NetworkResource.Empty -> {
            Resource.Empty
        }
    }
}

class MovieRepositoryImpl @Inject constructor(
    private val network: NetworkDataSource,
    private val cache: CacheDataSource,
) : MovieRepository {

    override fun getNowPlaying(
        page: Int,
        language: String?,
        region: String?,
    ): Flow<Resource<List<Result>>> = flow {
        emit(Resource.Loading)
        val networkResource =
            network.getNowPlaying(page = page, language = language, region = region)
        emit(networkResourceHandler(networkResource) { moviesResponse: MoviesResponse ->
            moviesResponse.results.map { it.toResult() }
        })
    }.catch { t -> Timber.e("getNowPlaying - ${t.message}") }

    override fun getPopularMovies(
        page: Int,
        language: String?,
        region: String?,
    ): Flow<Resource<List<Result>>> = flow {
        emit(Resource.Loading)
        val networkResource = network.getPopularMovies(
            page = page,
            language = language,
            region = region)
        emit(networkResourceHandler(networkResource) { moviesResponse: MoviesResponse ->
            moviesResponse.results.map { it.toResult() }
        })
    }.catch { t -> Timber.e("getPopularMovies - ${t.message}") }

    override fun getTrendingMovies(
        page: Int,
        mediaType: String,
        timeWindow: String,
    ): Flow<Resource<List<Result>>> = flow {
        emit(Resource.Loading)
        val networkResource = network.getTrendingMovies(
            page = page,
            mediaType = mediaType,
            timeWindow = timeWindow)
        emit(networkResourceHandler(networkResource) { moviesResponse: MoviesResponse ->
            moviesResponse.results.map { it.toResult() }
        })
    }.catch { t -> Timber.e("getTrendingMovies - ${t.message}") }

    override suspend fun getMovieRecommendations(
        movieId: Int,
        page: Int,
        language: String?,
    ): Flow<Resource<List<Result>>> = flow {
        emit(Resource.Loading)
        val networkResource = network.getMovieRecommendations(
            movieId = movieId,
            page = page,
            language = language)
        emit(networkResourceHandler(networkResource) { moviesResponse: MoviesResponse ->
            moviesResponse.results.map { it.toResult() }
        })
    }.catch { t -> Timber.e("getMovieRecommendations - ${t.message}") }

    override fun getNowPlayingPagingSource(
        loadSinglePage: Boolean,
        region: String?,
        language: String?,
    ): Flow<PagingData<Result>> = Pager(config = PagingConfig(
        pageSize = 20, enablePlaceholders = false
    ), pagingSourceFactory = {
        MoviesPagingSource(loadSinglePage = loadSinglePage, networkCall = { page ->
            network.getNowPlaying(page = page, language = language, region = region)
        })
    }).flow.map { pagingData -> pagingData.map { it.toResult() } }
        .catch { t -> Timber.e("getMovieNowPlayingPagingSource: ${t.message}") }

    override fun getPopularMoviesPagingSource(
        loadSinglePage: Boolean,
        region: String?,
        language: String?,
    ): Flow<PagingData<Result>> = Pager(config = PagingConfig(
        pageSize = 20, enablePlaceholders = false
    ), pagingSourceFactory = {
        MoviesPagingSource(loadSinglePage = loadSinglePage, networkCall = { page ->
            network.getPopularMovies(page = page, language = language, region = region)
        })
    }).flow.map { pagingData -> pagingData.map { it.toResult() } }
        .catch { t -> Timber.e("getPopularMoviesPagingSource: ${t.message}") }

    override fun getTrendingMoviesPagingSource(
        loadSinglePage: Boolean,
        mediaType: String,
        timeWindow: String,
    ): Flow<PagingData<Result>> = Pager(config = PagingConfig(
        pageSize = 20, enablePlaceholders = false
    ), pagingSourceFactory = {
        MoviesPagingSource(loadSinglePage = loadSinglePage, networkCall = { page ->
            network.getTrendingMovies(page = page, mediaType = mediaType, timeWindow = timeWindow)
        })
    }).flow.map { pagingData -> pagingData.map { it.toResult() } }
        .catch { t -> Timber.e("getTrendingMoviesPagingSource: ${t.message}") }

    override fun getMovieDetails(
        movieId: Int,
        language: String?,
        appendToResponse: String?,
    ): Flow<Resource<MovieDetails>> = flow {

        emit(Resource.Loading)

        val networkResource = network.getMovieDetails(
            movieId = movieId,
            language = language,
            appendToResponse = appendToResponse)

        emit(networkResourceHandler(networkResource) { movieDetailsResponse: MovieDetailsResponse ->
            movieDetailsResponse.toMovieDetails()
        })

    }.catch { t -> Timber.e("getMovieDetails: ${t.message}") }

    override fun searchMoviesPagingSource(
        loadSinglePage: Boolean,
        query: String,
        language: String?,
        includeAdult: Boolean?,
        region: String?,
        year: Int?,
        primaryReleaseYear: Int?,
    ): Flow<PagingData<Result>> = Pager(config = PagingConfig(
        pageSize = 20, enablePlaceholders = false
    ), pagingSourceFactory = {
        MoviesPagingSource(loadSinglePage = loadSinglePage, networkCall = { page ->
            network.searchMovies(query = query,
                language = language,
                page = page,
                includeAdult = includeAdult,
                region = region,
                year = year,
                primaryReleaseYear = primaryReleaseYear)
        })
    }).flow.map { pagingData ->
        pagingData.map {
            it.toResult()
        }
    }.catch { t ->
        Timber.e("searchMoviesPaging: ${t.message}")
    }

    override fun getMovieRecommendationPagingSource(
        movieId: Int,
        loadSinglePage: Boolean,
        language: String?,
    ): Flow<PagingData<Result>> = Pager(config = PagingConfig(
        pageSize = 20, enablePlaceholders = false
    ), pagingSourceFactory = {
        MoviesPagingSource(loadSinglePage = loadSinglePage, networkCall = { page ->
            network.getMovieRecommendations(movieId = movieId, page = page, language = language)
        })
    }).flow.map { pagingData -> pagingData.map { it.toResult() } }
        .catch { t -> Timber.e("getMovieRecommendationPagingSource: ${t.message}") }

    override suspend fun saveCasts(casts: List<Cast>) {
        val castString = Gson().toJson(casts)
        cache.saveCasts(castString)
    }

    override fun getCasts(): Flow<List<Cast>> = cache.getCasts().map {
        val gson = Gson()
        val listType = object : TypeToken<List<Cast>>() {}.type
        gson.fromJson<List<Cast>>(it, listType)
    }.catch { t -> Timber.e("getCasts: ${t.message}") }

    override suspend fun saveCrews(crews: List<Crew>) {
        val crewString = Gson().toJson(crews)
        cache.saveCrews(crewString)
    }

    override fun getCrews(): Flow<List<Crew>> = cache.getCrews().map {
        val gson = Gson()
        val listType = object : TypeToken<List<Crew>>() {}.type
        gson.fromJson<List<Crew>>(it, listType)
    }.catch { t -> Timber.e("getCasts: ${t.message}") }

}