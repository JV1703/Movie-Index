package com.example.movieindex.core.data.remote.interceptor

import android.content.Context
import com.example.movieindex.core.common.hasInternetConnection
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class ForceCacheInterceptor @Inject constructor(@ApplicationContext private val context: Context) :
    Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder: Request.Builder = chain.request().newBuilder()
        if (!hasInternetConnection(context)) {
            builder.cacheControl(CacheControl.FORCE_CACHE)
        }
        return chain.proceed(builder.build())
    }
}