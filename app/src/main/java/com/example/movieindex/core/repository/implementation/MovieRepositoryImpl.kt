package com.example.movieindex.core.repository.implementation

import androidx.paging.*
import com.example.movieindex.core.data.external.MovieDetails
import com.example.movieindex.core.data.external.Resource
import com.example.movieindex.core.data.external.Result
import com.example.movieindex.core.data.local.abstraction.CacheDataSource
import com.example.movieindex.core.data.local.model.MovieEntity
import com.example.movieindex.core.data.local.model.MovieKeyEntity
import com.example.movieindex.core.data.local.model.MoviePagingCategory
import com.example.movieindex.core.data.local.model.toResult
import com.example.movieindex.core.data.remote.NetworkResource
import com.example.movieindex.core.data.remote.abstraction.NetworkDataSource
import com.example.movieindex.core.data.remote.model.common.MoviesResponse
import com.example.movieindex.core.data.remote.model.common.toResult
import com.example.movieindex.core.data.remote.model.details.toMovieDetails
import com.example.movieindex.core.repository.abstraction.MovieRepository
import com.example.movieindex.core.repository.paging.MoviesPagingSource
import com.example.movieindex.core.repository.paging.MoviesRemoteMediator
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

@OptIn(ExperimentalPagingApi::class)
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
    }

    override fun getPopularMovies(
        page: Int,
        region: String?,
        language: String?,
    ): Flow<Resource<List<Result>>> = flow {
        emit(Resource.Loading)
        val networkResource = network.getNowPlaying(
            page = page,
            language = language,
            region = region)
        emit(networkResourceHandler(networkResource) { moviesResponse: MoviesResponse ->
            moviesResponse.results.map { it.toResult() }
        })
    }

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
    }

    override fun searchMovies(
        query: String,
        page: Int,
        language: String?,
        includeAdult: Boolean?,
        region: String?,
        year: Int?,
        primaryReleaseYear: Int?,
    ): Flow<Resource<List<Result>>> = flow {
        emit(Resource.Loading)

        val networkResource = network.searchMovies(query = query,
            page = 1,
            language = language,
            includeAdult = includeAdult,
            region = region,
            year = year,
            primaryReleaseYear = primaryReleaseYear)

        when (networkResource) {
            is NetworkResource.Success -> {
                val data = networkResource.data.results.map { it.toResult() }
                emit(Resource.Success(data))
            }
            is NetworkResource.Error -> {
                emit(Resource.Error(errMsg = "errCode: ${networkResource.errCode}, errMsg: ${networkResource.errMessage}",
                    errCode = networkResource.errCode))
            }
            is NetworkResource.Empty -> {
                emit(Resource.Empty)
            }
        }
    }

    override fun getNowPlayingPagingData(
        isInitialLoad: Boolean,
        loadSinglePage: Boolean,
        region: String?,
        language: String?,
    ): Flow<PagingData<Result>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        remoteMediator = MoviesRemoteMediator(
            isInitialLoad = isInitialLoad,
            loadSinglePage = loadSinglePage,
            networkCall = { page: Int ->
                network.getNowPlaying(page = page)
            },
            pagingCategory = MoviePagingCategory.NOW_PLAYING,
            dbCallGetMovieKey = { id: String, pagingCategory: MoviePagingCategory ->
                movieKeyId(id = id, pagingCategory = pagingCategory)
            },
            dbCallOnRefreshClearDb = { pagingCategory: MoviePagingCategory ->
                clearMovieKeys(pagingCategory = pagingCategory)
                clearMovies(pagingCategory = pagingCategory)
            },
            dbCallOnSuccess = { movieKeys: List<MovieKeyEntity>, movies: List<MovieEntity> ->
                insertAllMovieKeys(movieKeys)
                insertAllMovies(movies)
            }
        ),
        pagingSourceFactory = {
            getMovies(pagingCategory = MoviePagingCategory.NOW_PLAYING)
        }
    ).flow.map { pagingData ->
        pagingData.map {
            it.toResult()
        }
    }.catch { t: Throwable ->
        Timber.e("getNowPlaying: ${t.message}")
    }

    override fun getPopularMoviesPagingData(
        isInitialLoad: Boolean,
        loadSinglePage: Boolean,
        region: String?,
        language: String?,
    ): Flow<PagingData<Result>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        remoteMediator = MoviesRemoteMediator(
            isInitialLoad = isInitialLoad,
            loadSinglePage = loadSinglePage,
            networkCall = { page: Int ->
                network.getPopularMovies(page = page)
            },
            pagingCategory = MoviePagingCategory.POPULAR,
            dbCallGetMovieKey = { id: String, pagingCategory: MoviePagingCategory ->
                movieKeyId(id = id, pagingCategory = pagingCategory)
            },
            dbCallOnRefreshClearDb = { pagingCategory: MoviePagingCategory ->
                clearMovieKeys(pagingCategory = pagingCategory)
                clearMovies(pagingCategory = pagingCategory)
            },
            dbCallOnSuccess = { movieKeys: List<MovieKeyEntity>, movies: List<MovieEntity> ->
                insertAllMovieKeys(movieKeys)
                insertAllMovies(movies)
            }
        ),
        pagingSourceFactory = {
            getMovies(pagingCategory = MoviePagingCategory.POPULAR)
        }
    ).flow.map { pagingData ->
        pagingData.map {
            it.toResult()
        }
    }.catch { t: Throwable ->
        Timber.e("getPopularMovies: ${t.message}")
    }

    override fun getTrendingMoviesPagingData(
        isInitialLoad: Boolean,
        loadSinglePage: Boolean,
        mediaType: String,
        timeWindow: String,
    ): Flow<PagingData<Result>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        remoteMediator = MoviesRemoteMediator(
            isInitialLoad = isInitialLoad,
            loadSinglePage = loadSinglePage,
            networkCall = { page: Int ->
                network.getTrendingMovies(
                    page = page,
                    mediaType = mediaType,
                    timeWindow = timeWindow)
            },
            pagingCategory = MoviePagingCategory.TRENDING,
            dbCallGetMovieKey = { id: String, pagingCategory: MoviePagingCategory ->
                movieKeyId(id = id, pagingCategory)
            },
            dbCallOnRefreshClearDb = { pagingCategory: MoviePagingCategory ->
                clearMovieKeys(pagingCategory = pagingCategory)
                clearMovies(pagingCategory = pagingCategory)
            },
            dbCallOnSuccess = { movieKeys: List<MovieKeyEntity>, movies: List<MovieEntity> ->
                insertAllMovieKeys(movieKeys)
                insertAllMovies(movies)
            }
        ), pagingSourceFactory = {
            getMovies(pagingCategory = MoviePagingCategory.TRENDING)
        }
    ).flow.map { pagingData ->
        pagingData.map {
            it.toResult()
        }
    }.catch { t: Throwable ->
        Timber.e("getTrendingMovies: ${t.message}")
    }

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

        when (networkResource) {
            is NetworkResource.Success -> {
                val data = networkResource.data.toMovieDetails()
                emit(Resource.Success(data))
            }
            is NetworkResource.Error -> {
                emit(Resource.Error(errMsg = "errCode: ${networkResource.errCode}, errMsg: ${networkResource.errMessage}",
                    errCode = networkResource.errCode))
            }
            is NetworkResource.Empty -> {
                emit(Resource.Empty)
            }
        }

    }

    override fun searchMoviesPaging(
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
        Timber.e("getTrendingMovies: ${t.message}")
    }

    // Cache

    override fun getMoviesWithReferenceToPagingCategory(pagingCategory: MoviePagingCategory): Flow<List<Result>> =
        cache.getMoviesWithReferenceToPagingCategory(pagingCategory = pagingCategory)
            .map { movieEntityList ->
                movieEntityList.map { it.toResult() }
            }

    // Paging - Remote Mediator Cache Actions

    override suspend fun insertAllMovies(movies: List<MovieEntity>) {
        cache.insertAllMovies(movies)
    }

    override fun getMovies(pagingCategory: MoviePagingCategory): PagingSource<Int, MovieEntity> =
        cache.getMovies(pagingCategory = pagingCategory)

    override suspend fun clearMovies(pagingCategory: MoviePagingCategory) {
        cache.clearMovies(pagingCategory = pagingCategory)
    }

    override suspend fun insertAllMovieKeys(movieKeys: List<MovieKeyEntity>) {
        cache.insertAllMovieKeys(movieKeys)
    }

    override suspend fun movieKeyId(
        id: String,
        pagingCategory: MoviePagingCategory,
    ): MovieKeyEntity? = cache.movieKeyId(id = id, pagingCategory)

    override suspend fun clearMovieKeys(pagingCategory: MoviePagingCategory) {
        cache.clearMovieKeys(pagingCategory = pagingCategory)
    }

}