package com.example.movieindex.feature.detail.movie.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.movieindex.core.common.WorkerConstants
import com.example.movieindex.core.common.WorkerConstants.WATCH_LIST_WORKER_WATCH_LIST_KEY
import com.example.movieindex.core.data.local.abstraction.CacheDataSource
import com.example.movieindex.core.data.remote.NetworkResource
import com.example.movieindex.core.data.remote.abstraction.NetworkDataSource
import com.example.movieindex.core.data.remote.model.watchlist.body.WatchListBody
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

@HiltWorker
class WatchListWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    @Inject
    lateinit var cache: CacheDataSource

    @Inject
    lateinit var network: NetworkDataSource

    override suspend fun doWork(): Result {
        val sessionId = cache.getSessionId().first()
        val accountId = cache.getAccountDetails().first()?.id
        val watchlist =
            workerParams.inputData.getBoolean(WATCH_LIST_WORKER_WATCH_LIST_KEY, false)
        val movieId = workerParams.inputData.getInt(WorkerConstants.WORKER_MOVIE_ID_KEY, 0)
        val mediaType = workerParams.inputData.getString(WorkerConstants.WORKER_MOVIE_TYPE_KEY) ?: "movie"

        Timber.i("watchlist worker - watchList: $watchlist")

        val body = WatchListBody(
            watchlist = watchlist,
            mediaId = movieId,
            mediaType = mediaType
        )

        return if(accountId == null || sessionId.isEmpty()){
            Timber.e("WatchListWorker - invalidCredentials - sessionId: $sessionId, accountId: $accountId")
            Result.failure()
        }else{
            val networkResource = network.addToWatchList(accountId = accountId,
                sessionId = sessionId,
                body = body)

            when (networkResource) {
                is NetworkResource.Success -> {
                    Result.success()
                }
                is NetworkResource.Error -> {
                    if (runAttemptCount > WorkerConstants.MAX_RETRY_ATTEMPT) {
                        Timber.e("WatchListWorker - fail - errMsg: ${networkResource.errMessage}")
                        Result.failure()
                    } else {
                        Timber.e("WatchListWorker - retry - errMsg: ${networkResource.errMessage}")
                        Result.retry()
                    }
                }
                is NetworkResource.Empty -> {
                    if (runAttemptCount > WorkerConstants.MAX_RETRY_ATTEMPT) {
                        Result.failure()
                    } else {
                        Result.retry()
                    }
                }
            }
        }



    }
}