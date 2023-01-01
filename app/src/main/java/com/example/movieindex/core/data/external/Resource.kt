package com.example.movieindex.core.data.external

sealed class Resource<out T>() {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val errMsg: String? = null, val errCode: Int? = null) :
        Resource<Nothing>()

    object Empty : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}


