package com.example.movieindex.core.data.external.model

import com.example.movieindex.core.data.remote.NetworkResource

sealed class Resource<out T>() {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val errMsg: String? = null, val errCode: Int? = null) :
        Resource<Nothing>()

    object Empty : Resource<Nothing>()
//    object Loading : Resource<Nothing>()
}

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
            Resource.Error(errMsg = networkResource.errMsg,
                errCode = networkResource.errCode)
        }
        is NetworkResource.Empty -> {
            Resource.Empty
        }
    }
}