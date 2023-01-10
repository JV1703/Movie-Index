package com.example.movieindex.core.repository.implementation

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.work.*
import com.example.movieindex.core.common.WorkerConstants.FAVORITE_WORKER_FAVORITE_KEY
import com.example.movieindex.core.common.WorkerConstants.WATCH_LIST_WORKER_WATCH_LIST_KEY
import com.example.movieindex.core.common.WorkerConstants.WORKER_MOVIE_ID_KEY
import com.example.movieindex.core.common.WorkerConstants.WORKER_MOVIE_TYPE_KEY
import com.example.movieindex.core.data.external.*
import com.example.movieindex.core.data.local.abstraction.CacheDataSource
import com.example.movieindex.core.data.local.model.toSavedMovie
import com.example.movieindex.core.data.remote.NetworkResource
import com.example.movieindex.core.data.remote.abstraction.NetworkDataSource
import com.example.movieindex.core.data.remote.model.account.AccountDetailsResponse
import com.example.movieindex.core.data.remote.model.account.toAccountDetails
import com.example.movieindex.core.data.remote.model.common.MoviesResponse
import com.example.movieindex.core.data.remote.model.common.toResult
import com.example.movieindex.core.data.remote.model.details.MovieDetailsResponse
import com.example.movieindex.core.data.remote.model.details.toMovieDetails
import com.example.movieindex.core.repository.abstraction.MovieRepository
import com.example.movieindex.core.repository.paging.MoviesPagingSource
import com.example.movieindex.feature.detail.movie.worker.FavoriteWorker
import com.example.movieindex.feature.detail.movie.worker.WatchListWorker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import timber.log.Timber
import java.util.concurrent.TimeUnit
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
            Resource.Error(errMsg = networkResource.errMessage,
                errCode = networkResource.errCode)
        }
        is NetworkResource.Empty -> {
            Resource.Empty
        }
    }
}

//suspend fun <T, R> networkResourceHandler(
//    networkResource: NetworkResource<T>,
//    onSuccess: suspend (T) -> Resource.Success<R>,
//): Resource<R> {
//    return when (networkResource) {
//        is NetworkResource.Success -> {
//            val data = networkResource.data
//            onSuccess(data)
//        }
//        is NetworkResource.Error -> {
//            Resource.Error(errMsg = networkResource.errMessage,
//                errCode = networkResource.errCode)
//        }
//        is NetworkResource.Empty -> {
//            Resource.Empty
//        }
//    }
//}

class MovieRepositoryImpl @Inject constructor(
    private val workManager: WorkManager,
    private val network: NetworkDataSource,
    private val cache: CacheDataSource,
) : MovieRepository {

    private var nowPlayingMoviesList: List<Result> = emptyList()
    private val nowPlayingMutex = Mutex()
    override fun getNowPlaying(
        page: Int,
        language: String?,
        region: String?,
    ): Flow<Resource<List<Result>>> = flow {
        emit(Resource.Loading)

        val networkResource = network.getNowPlaying(
            page = page,
            language = language, region = region)
        emit(networkResourceHandler(networkResource,
            conversion = { moviesResponse: MoviesResponse ->
                moviesResponse.results.map { it.toResult() }
            }))

    }.catch { t -> Timber.e("getNowPlaying - ${t.message}") }

    override fun getPopularMovies(
        page: Int,
        language: String?,
        region: String?,
    ): Flow<Resource<List<Result>>> = flow {
        emit(Resource.Loading)
        val networkResource = network.getPopularMovies(
            page = page,
            language = language, region = region)
        emit(networkResourceHandler(networkResource,
            conversion = { moviesResponse: MoviesResponse ->
                moviesResponse.results.map { it.toResult() }
            }))
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
        emit(networkResourceHandler(networkResource,
            conversion = { moviesResponse: MoviesResponse ->
                moviesResponse.results.map { it.toResult() }
            }))
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
        emit(networkResourceHandler(networkResource,
            conversion = { moviesResponse: MoviesResponse ->
                moviesResponse.results.map { it.toResult() }
            }))
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

        emit(networkResourceHandler(networkResource,
            conversion = { movieDetailsResponse: MovieDetailsResponse ->
                movieDetailsResponse.toMovieDetails()
            }))

    }.catch { t -> Timber.e("getMovieDetails: ${t.message}") }

    override fun getAccountDetails(sessionId: String): Flow<Resource<AccountDetails>> = flow {
        emit(Resource.Loading)

        val networkResource = network.getAccountDetails(sessionId = sessionId)

        emit(networkResourceHandler(networkResource,
            conversion = { accountDetailsResponse: AccountDetailsResponse ->
                accountDetailsResponse.toAccountDetails()
            }))
    }.catch { t -> Timber.e("getAccountDetails: ${t.message}") }

    override fun addToFavorite(
        favorite: Boolean,
        mediaId: Int,
        mediaType: String,
    ) {

        val request = OneTimeWorkRequestBuilder<FavoriteWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED)
                .build())
            .setInputData(
                workDataOf(
                    FAVORITE_WORKER_FAVORITE_KEY to favorite,
                    WORKER_MOVIE_ID_KEY to mediaId,
                    WORKER_MOVIE_TYPE_KEY to mediaType
                )
            )
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL,
                OneTimeWorkRequest.MAX_BACKOFF_MILLIS,
                TimeUnit.MINUTES)
            .build()

        workManager.enqueueUniqueWork(
            "addToFavoriteWork",
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            request)
    }

    override fun addToWatchList(
        watchlist: Boolean,
        mediaId: Int,
        mediaType: String,
    ) {
        val request = OneTimeWorkRequestBuilder<WatchListWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED)
                .build())
            .setInputData(
                workDataOf(
                    WATCH_LIST_WORKER_WATCH_LIST_KEY to watchlist,
                    WORKER_MOVIE_ID_KEY to mediaId,
                    WORKER_MOVIE_TYPE_KEY to mediaType
                )
            )
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL,
                OneTimeWorkRequest.MAX_BACKOFF_MILLIS,
                TimeUnit.MINUTES)
            .build()

        workManager.enqueueUniqueWork(
            "addToFavoriteWork",
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            request)
    }

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

    override fun getFavoriteList(
        accountId: Int,
        sessionId: String, page: Int, language: String?,
        sortBy: String?,
    ): Flow<Resource<List<Result>>> = flow {
        emit(Resource.Loading)

        val networkResource = network.getFavoriteList(
            accountId = accountId,
            sessionId = sessionId,
            page = page,
            language = language,
            sortBy = sortBy)
        emit(networkResourceHandler(networkResource,
            conversion = { moviesResponse: MoviesResponse ->
                moviesResponse.results.map { it.toResult() }
            }))

    }.catch { t -> Timber.e("getFavoriteList - ${t.message}") }

    override fun getWatchList(
        accountId: Int,
        sessionId: String, page: Int, language: String?,
        sortBy: String?,
    ): Flow<Resource<List<Result>>> = flow {
        emit(Resource.Loading)

        val networkResource = network.getWatchList(
            accountId = accountId,
            sessionId = sessionId,
            page = page,
            language = language,
            sortBy = sortBy)
        emit(networkResourceHandler(networkResource,
            conversion = { moviesResponse: MoviesResponse ->
                moviesResponse.results.map { it.toResult() }
            }))

    }.catch { t -> Timber.e("getWatchList - ${t.message}") }


    override fun getFavoriteListPagingSource(
        accountId: Int,
        sessionId: String,
        loadSinglePage: Boolean,
        language: String?,
        sortBy: String?,
    ): Flow<PagingData<Result>> = Pager(config = PagingConfig(
        pageSize = 20, enablePlaceholders = false
    ), pagingSourceFactory = {
        MoviesPagingSource(loadSinglePage = loadSinglePage, networkCall = { page ->
            network.getFavoriteList(
                accountId = accountId,
                sessionId = sessionId,
                page = page,
                language = language,
                sortBy = sortBy)
        })
    }).flow.map { pagingData -> pagingData.map { it.toResult() } }
        .catch { t -> Timber.e("getFavoriteListPagingSource: ${t.message}") }

    override fun getWatchListPagingSource(
        accountId: Int,
        sessionId: String,
        loadSinglePage: Boolean,
        language: String?,
        sortBy: String?,
    ): Flow<PagingData<Result>> = Pager(config = PagingConfig(
        pageSize = 20, enablePlaceholders = false
    ), pagingSourceFactory = {
        MoviesPagingSource(loadSinglePage = loadSinglePage, networkCall = { page ->
            network.getWatchList(
                accountId = accountId,
                sessionId = sessionId,
                page = page,
                language = language,
                sortBy = sortBy)
        })
    }).flow.map { pagingData -> pagingData.map { it.toResult() } }
        .catch { t -> Timber.e("getWatchListPagingSource: ${t.message}") }

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

    override suspend fun saveAccountIdCache(accountId: Int) {
        cache.saveAccountId(accountId = accountId)
    }

    override fun getAccountIdCache(): Flow<Int> = cache.getAccountId()

    override suspend fun insertMovieToCache(
        movie: MovieDetails,
        isFavorite: Boolean,
        isBookmark: Boolean,
    ) {
        val entity = movie.toMovieEntity(
            isFavorite,
            isBookmark
        )
        cache.insertMovie(entity)
    }

    override fun getCachedMovie(movieId: Int): Flow<SavedMovie?> =
        cache.getMovie(movieId).map { it?.toSavedMovie() }
            .catch { t -> Timber.e("getMovie: ${t.message}") }

    override fun getCachedFavoriteMovies(): Flow<List<SavedMovie>> =
        cache.getFavoriteMovies().map { it.map { it.toSavedMovie() } }
            .catch { t -> Timber.e("getFavoriteMovies: ${t.message}") }

    override fun getCachedBookmarkedMovies(): Flow<List<SavedMovie>> =
        cache.getFavoriteMovies().map { it.map { it.toSavedMovie() } }
            .catch { t -> Timber.e("getBookmarkedMovies: ${t.message}") }

    override suspend fun updateBookmarkCache(movieId: Int, isBookmark: Boolean) {
        cache.updateBookmark(movieId = movieId, isBookmark = isBookmark)
    }

    override suspend fun updateFavoriteCache(movieId: Int, isFavorite: Boolean) {
        cache.updateFavorite(movieId = movieId, isFavorite = isFavorite)
    }

    override suspend fun deleteMovieCache(movieId: Int) {
        cache.deleteMovie(movieId = movieId)
    }

}