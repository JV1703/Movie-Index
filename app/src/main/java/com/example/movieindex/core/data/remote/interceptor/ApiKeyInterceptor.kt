package com.example.movieindex.core.data.remote.interceptor

import com.example.movieindex.core.data.remote.DevCredentials.API_KEY
import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var original = chain.request()
        val apiKey = API_KEY
        val url = original.url.newBuilder().addQueryParameter("api_key", apiKey).build()
        original = original.newBuilder().url(url).build()
        return chain.proceed(original)
    }

}