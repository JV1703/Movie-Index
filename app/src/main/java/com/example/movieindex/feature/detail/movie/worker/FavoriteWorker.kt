package com.example.movieindex.feature.detail.movie.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.movieindex.core.common.WorkerConstants.FAVORITE_WORKER_FAVORITE_KEY
import com.example.movieindex.core.common.WorkerConstants.MAX_RETRY_ATTEMPT
import com.example.movieindex.core.common.WorkerConstants.WORKER_MOVIE_ID_KEY
import com.example.movieindex.core.common.WorkerConstants.WORKER_MOVIE_TYPE_KEY
import com.example.movieindex.core.data.local.abstraction.CacheDataSource
import com.example.movieindex.core.data.remote.NetworkResource
import com.example.movieindex.core.data.remote.abstraction.NetworkDataSource
import com.example.movieindex.core.data.remote.model.favorite.body.FavoriteBody
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

@HiltWorker
class FavoriteWorker @AssistedInject constructor(
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
        val favorite = workerParams.inputData.getBoolean(FAVORITE_WORKER_FAVORITE_KEY, false)
        val movieId = workerParams.inputData.getInt(WORKER_MOVIE_ID_KEY, 0)
        val mediaType = workerParams.inputData.getString(WORKER_MOVIE_TYPE_KEY) ?: "movie"

        val body = FavoriteBody(
            favorite = favorite,
            mediaId = movieId,
            mediaType = mediaType
        )

        return if(accountId == null || sessionId.isEmpty()){
            Timber.e("FavoriteWorker - invalidCredentials - sessionId: $sessionId, accountId: $accountId")
            Result.failure()
        }else{
            val networkResource = network.addToFavorite(accountId = accountId,
                sessionId = sessionId,
                body = body)

            when (networkResource) {
                is NetworkResource.Success -> {
                    Result.success()
                }
                is NetworkResource.Error -> {
                    if (runAttemptCount > MAX_RETRY_ATTEMPT) {
                        Timber.e("FavoriteWorker - fail - errMsg: ${networkResource.errMessage}")
                        Result.failure()
                    } else {
                        Timber.e("FavoriteWorker - retry - errMsg: ${networkResource.errMessage}")
                        Result.retry()
                    }
                }
                is NetworkResource.Empty -> {
                    if (runAttemptCount > MAX_RETRY_ATTEMPT) {
                        Result.failure()
                    } else {
                        Result.retry()
                    }
                }
            }
        }
    }
}