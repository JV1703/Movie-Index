package com.example.movieindex.core.data.remote.interceptor

import android.content.Context
import com.example.movieindex.core.common.hasInternetConnection
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class CacheInterceptor @Inject constructor(@ApplicationContext private val context: Context) :
    Interceptor {

    // network interceptor
    // max age is used to determine if we will get data from cache or network
    // i.e. if max age is set to 1 hours, this means all the network call made within 1 hour of the initial call will be from cache
    override fun intercept(chain: Interceptor.Chain): Response {

        var request = chain.request()

        request = if (hasInternetConnection(context)) {
            request.newBuilder().header("Cache-Control", "public, max-age=" + 5).build()
        } else {
            request.newBuilder()
                .header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 2)
                .build()
        }

        return chain.proceed(request)

    }
}