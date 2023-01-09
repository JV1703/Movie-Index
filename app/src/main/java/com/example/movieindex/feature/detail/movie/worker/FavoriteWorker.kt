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
        val accountId = cache.getAccountId().first()
        val favorite = workerParams.inputData.getBoolean(FAVORITE_WORKER_FAVORITE_KEY, false)
        val movieId = workerParams.inputData.getInt(WORKER_MOVIE_ID_KEY, 0)
        val mediaType = workerParams.inputData.getString(WORKER_MOVIE_TYPE_KEY) ?: "movie"

        val body = FavoriteBody(
            favorite = favorite,
            mediaId = movieId,
            mediaType = mediaType
        )

        val networkResource = network.addToFavorite(accountId = accountId,
            sessionId = sessionId,
            body = body)

        return when (networkResource) {
            is NetworkResource.Success -> {
                Result.success()
            }
            is NetworkResource.Error -> {
                if (runAttemptCount > MAX_RETRY_ATTEMPT) {
                    Result.failure()
                } else {
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