package com.example.movieindex.core.data.remote.model.common


import com.squareup.moshi.Json

data class PostResponse(
    @Json(name = "status_code")
    val statusCode: Int,
    @Json(name = "status_message")
    val statusMessage: String,
    @Json(name = "success")
    val success: Boolean
)