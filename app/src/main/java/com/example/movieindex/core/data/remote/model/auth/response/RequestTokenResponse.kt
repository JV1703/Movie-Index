package com.example.movieindex.core.data.remote.model.auth.response


import com.squareup.moshi.Json

data class RequestTokenResponse(
    @Json(name = "expires_at")
    val expires_at: String,
    @Json(name = "request_token")
    val request_token: String,
    @Json(name = "success")
    val success: Boolean,
)