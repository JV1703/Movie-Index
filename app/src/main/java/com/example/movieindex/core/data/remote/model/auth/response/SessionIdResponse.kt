package com.example.movieindex.core.data.remote.model.auth.response

import com.squareup.moshi.Json

data class SessionIdResponse(
    @Json(name = "session_id")
    val session_id: String,
    @Json(name = "success")
    val success: Boolean,
)