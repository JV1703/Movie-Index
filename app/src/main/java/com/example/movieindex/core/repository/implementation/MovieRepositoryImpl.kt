package com.example.movieindex.core.repository.implementation

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.movieindex.core.common.wrapEspressoIdlingResource
import com.example.movieindex.core.data.external.model.*
import com.example.movieindex.core.data.local.abstraction.CacheDataSource
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
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val network: NetworkDataSource,
    private val cache: CacheDataSource,
) : MovieRepository {

    override suspend fun getNowPlaying(
        page: Int,
        language: String?,
        region: String?,
    ): Resource<List<Result>> {
        wrapEspressoIdlingResource {
            val networkResource = network.getNowPlaying(
                page = page,
                language = language, region = region)
            return networkResourceHandler(networkResource,
                conversion = { moviesResponse: MoviesResponse ->
                    moviesResponse.results.map { it.toResult() }
                })
        }
    }

    override suspend fun getPopularMovies(
        page: Int,
        language: String?,
        region: String?,
    ): Resource<List<Result>> {
        wrapEspressoIdlingResource {
            val networkResource = network.getPopularMovies(
                page = page,
                language = language, region = region)
            return networkResourceHandler(networkResource,
                conversion = { moviesResponse: MoviesResponse ->
                    moviesResponse.results.map { it.toResult() }
                })
        }
    }

    override suspend fun getTrendingMovies(
        page: Int,
        mediaType: String,
        timeWindow: String,
    ): Resource<List<Result>> {
        wrapEspressoIdlingResource {
            val networkResource = network.getTrendingMovies(
                page = page,
                mediaType = mediaType,
                timeWindow = timeWindow)
            return networkResourceHandler(networkResource,
                conversion = { moviesResponse: MoviesResponse ->
                    moviesResponse.results.map { it.toResult() }
                })
        }
    }

    override suspend fun getMovieRecommendations(
        movieId: Int,
        page: Int,
        language: String?,
    ): Resource<List<Result>> {
        wrapEspressoIdlingResource {
            val networkResource = network.getMovieRecommendations(
                movieId = movieId,
                page = page,
                language = language)
            return networkResourceHandler(networkResource,
                conversion = { moviesResponse: MoviesResponse ->
                    moviesResponse.results.map { it.toResult() }
                })
        }
    }

    override fun getNowPlayingPagingSource(
        loadSinglePage: Boolean,
        region: String?,
        language: String?,
    ): Flow<PagingData<Result>> =
        wrapEspressoIdlingResource {
            Pager(config = PagingConfig(
                pageSize = 20, enablePlaceholders = false
            ), pagingSourceFactory = {
                MoviesPagingSource(loadSinglePage = loadSinglePage,
                    networkCall = { page ->
                        network.getNowPlaying(page = page, language = language, region = region)
                    })
            })
        }.flow.map { pagingData -> pagingData.map { it.toResult() } }
            .catch { t -> Timber.e("getMovieNowPlayingPagingSource: ${t.message}") }


    override fun getPopularMoviesPagingSource(
        loadSinglePage: Boolean,
        region: String?,
        language: String?,
    ): Flow<PagingData<Result>> =
        wrapEspressoIdlingResource {
            Pager(config = PagingConfig(
                pageSize = 20, enablePlaceholders = false
            ), pagingSourceFactory = {
                MoviesPagingSource(loadSinglePage = loadSinglePage,
                    networkCall = { page ->
                        network.getPopularMovies(page = page, language = language, region = region)
                    })
            })
        }.flow.map { pagingData -> pagingData.map { it.toResult() } }
            .catch { t -> Timber.e("getPopularMoviesPagingSource: ${t.message}") }


    override fun getTrendingMoviesPagingSource(
        loadSinglePage: Boolean,
        mediaType: String,
        timeWindow: String,
    ): Flow<PagingData<Result>> =
        wrapEspressoIdlingResource {
            Pager(config = PagingConfig(
                pageSize = 20, enablePlaceholders = false
            ), pagingSourceFactory = {
                MoviesPagingSource(loadSinglePage = loadSinglePage,
                    networkCall = { page ->
                        network.getTrendingMovies(page = page,
                            mediaType = mediaType,
                            timeWindow = timeWindow)
                    })
            })
        }.flow.map { pagingData -> pagingData.map { it.toResult() } }
            .catch { t -> Timber.e("getTrendingMoviesPagingSource: ${t.message}") }


    override suspend fun getMovieDetails(
        movieId: Int,
        language: String?,
        appendToResponse: String?,
    ): Resource<MovieDetails> {
        wrapEspressoIdlingResource {
            val networkResource = network.getMovieDetails(
                movieId = movieId,
                language = language,
                appendToResponse = appendToResponse)

            return networkResourceHandler(networkResource,
                conversion = { movieDetailsResponse: MovieDetailsResponse ->
                    movieDetailsResponse.toMovieDetails()
                })
        }
    }

//    override fun addToFavorite(
//        favorite: Boolean,
//        mediaId: Int,
//        mediaType: String,
//    ) {
//        val request = OneTimeWorkRequestBuilder<FavoriteWorker>()
//            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
//            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED)
//                .build())
//            .setInputData(
//                workDataOf(
//                    FAVORITE_WORKER_FAVORITE_KEY to favorite,
//                    WORKER_MOVIE_ID_KEY to mediaId,
//                    WORKER_MOVIE_TYPE_KEY to mediaType
//                )
//            )
//            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL,
//                OneTimeWorkRequest.MAX_BACKOFF_MILLIS,
//                TimeUnit.MINUTES)
//            .build()
//
//        workManager.enqueueUniqueWork(
//            "addToFavoriteWork",
//            ExistingWorkPolicy.APPEND_OR_REPLACE,
//            request)
//    }

//    override fun addToWatchList(
//        watchlist: Boolean,
//        mediaId: Int,
//        mediaType: String,
//    ) {
//        val request = OneTimeWorkRequestBuilder<WatchListWorker>()
//            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
//            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED)
//                .build())
//            .setInputData(
//                workDataOf(
//                    WATCH_LIST_WORKER_WATCH_LIST_KEY to watchlist,
//                    WORKER_MOVIE_ID_KEY to mediaId,
//                    WORKER_MOVIE_TYPE_KEY to mediaType
//                )
//            )
//            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL,
//                OneTimeWorkRequest.MAX_BACKOFF_MILLIS,
//                TimeUnit.MINUTES)
//            .build()
//
//        workManager.enqueueUniqueWork(
//            "addToFavoriteWork",
//            ExistingWorkPolicy.APPEND_OR_REPLACE,
//            request)
//    }

    override fun searchMoviesPagingSource(
        loadSinglePage: Boolean,
        query: String,
        language: String?,
        includeAdult: Boolean?,
        region: String?,
        year: Int?,
        primaryReleaseYear: Int?,
    ): Flow<PagingData<Result>> =
        wrapEspressoIdlingResource {
            Pager(config = PagingConfig(
                pageSize = 20, enablePlaceholders = false
            ), pagingSourceFactory = {
                MoviesPagingSource(loadSinglePage = loadSinglePage,
                    networkCall = { page ->
                        network.searchMovies(query = query,
                            language = language,
                            page = page,
                            includeAdult = includeAdult,
                            region = region,
                            year = year,
                            primaryReleaseYear = primaryReleaseYear)
                    })
            })
        }.flow.map { pagingData ->
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
    ): Flow<PagingData<Result>> =
        wrapEspressoIdlingResource {
            Pager(config = PagingConfig(
                pageSize = 20, enablePlaceholders = false
            ), pagingSourceFactory = {
                MoviesPagingSource(loadSinglePage = loadSinglePage,
                    networkCall = { page ->
                        network.getMovieRecommendations(movieId = movieId,
                            page = page,
                            language = language)
                    })
            })
        }.flow.map { pagingData -> pagingData.map { it.toResult() } }
            .catch { t -> Timber.e("getMovieRecommendationPagingSource: ${t.message}") }


    override suspend fun saveCasts(casts: List<Cast>) {
        wrapEspressoIdlingResource {
            val castString = Gson().toJson(casts)
            cache.saveCasts(castString)
        }
    }

    override fun getCasts(): Flow<List<Cast>> = wrapEspressoIdlingResource {
        cache.getCasts().map {
            val gson = Gson()
            val listType = object : TypeToken<List<Cast>>() {}.type
            gson.fromJson<List<Cast>>(it, listType)
        }.catch { t -> Timber.e("getCasts: ${t.message}") }
    }

    override suspend fun saveCrews(crews: List<Crew>) {
        wrapEspressoIdlingResource {
            val crewString = Gson().toJson(crews)
            cache.saveCrews(crewString)
        }
    }

    override fun getCrews(): Flow<List<Crew>> = wrapEspressoIdlingResource {
        cache.getCrews().map {
            val gson = Gson()
            val listType = object : TypeToken<List<Crew>>() {}.type
            gson.fromJson<List<Crew>>(it, listType)
        }.catch { t -> Timber.e("getCasts: ${t.message}") }
    }

}