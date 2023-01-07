package com.example.movieindex.core.data.remote.interceptor

import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

class CacheInterceptor : Interceptor {

    // network interceptor
    // max age is used to determine if we will get data from cache or network
    // i.e. if max age is set to 1 hours, this means all the network call made within 1 hour of the initial call will be from cache
    override fun intercept(chain: Interceptor.Chain): Response {
        val response: Response = chain.proceed(chain.request())
        val cacheControl = CacheControl.Builder()
            .maxAge(1, TimeUnit.HOURS) // 5 minutes cache
            .build();
        return response.newBuilder()
            .header("Cache-Control", cacheControl.toString())
            .build()
    }
}