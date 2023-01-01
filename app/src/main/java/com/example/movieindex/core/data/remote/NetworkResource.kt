package com.example.movieindex.core.data.remote

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.Response
import timber.log.Timber

sealed class NetworkResource<out T> {
    data class Success<out T>(val data: T) : NetworkResource<T>()
    data class Error(val errMessage: String? = null, val errCode: Int? = null) :
        NetworkResource<Nothing>()

    object Empty : NetworkResource<Nothing>()
}

suspend fun <T, R> safeNetworkCall(
    dispatcher: CoroutineDispatcher,
    networkCall: suspend () -> Response<T>,
    conversion: (T) -> R,
): NetworkResource<R> {
    return withContext(dispatcher) {
        try {
            val response = networkCall()

            when {
                response.message().toString().contains("timeout") -> {
                    Timber.e("safeNetworkCall - errMsg: timeOut")
                    NetworkResource.Error(errCode = 408, errMessage = "Network Timeout")
                }

                response.errorBody() != null -> {
                    val errorBody = getErrorBody(response)
                    Timber.e("network_result", errorBody)
                    Timber.e("safeNetworkCall - errMsg: $errorBody")
                    NetworkResource.Error(errCode = response.code(),
                        errMessage = errorBody.toString())
                }

                response.isSuccessful -> {
                    val content = response.body()
                    if (content == null) {
                        NetworkResource.Empty
                    } else {
                        val convertedData = conversion(content)
                        NetworkResource.Success(data = convertedData)
                    }
                }

                else -> {
                    Timber.e("safeNetworkCall - miscErr: ${response.message()}")
                    NetworkResource.Error(errMessage = response.message())
                }
            }
        } catch (e: Exception) {
            Timber.e("safeNetworkCall - miscErr: ${e.message}")
            NetworkResource.Error(errMessage = e.message ?: "Non api error")
        }
    }
}

data class NetworkErrorResponse(
    val status_code: String,
    val status_message: String,
    val success: Boolean,
)

fun <T> getErrorBody(response: Response<T>): NetworkErrorResponse {
    return Gson().fromJson(
        response.errorBody()!!.charStream(),
        object : TypeToken<NetworkErrorResponse>() {}.type
    )
}