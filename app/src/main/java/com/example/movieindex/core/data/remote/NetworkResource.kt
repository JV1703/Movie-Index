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

                response.message().toString()
                    .contains("(only-if-cached)") && response.code() == 504 -> {
                    NetworkResource.Error(errCode = response.code(),
                        errMessage = "Please check internet connection")
                }

                response.errorBody() != null -> {
                    val errorBody = getErrorBody(response)?.parseErrorMessage()
                    Timber.i("response - message:${response.message()}, errBody: ${response.errorBody()}")
                    Timber.e("network_result", errorBody)
                    Timber.e("safeNetworkCall - errMsg: $errorBody")
                    NetworkResource.Error(errCode = response.code(),
                        errMessage = errorBody)
                }

                response.isSuccessful -> {
                    val content = response.body()
                    if (content == null) {
                        NetworkResource.Empty
                    } else {
                        val convertedData = conversion(content)
                        Timber.i("safeNetworkCall - is data from cache: ${response.raw().networkResponse == null} ")
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
    val status_code: String? = null,
    val status_message: String = "unknown_error",
    val success: Boolean = false,
) {
    fun parseErrorMessage(): String {
        return "code: $status_code, msg: $status_message"
    }
}

fun <T> getErrorBody(response: Response<T>): NetworkErrorResponse? {
    return Gson().fromJson(
        response.errorBody()!!.charStream(),
        object : TypeToken<NetworkErrorResponse>() {}.type
    )
}