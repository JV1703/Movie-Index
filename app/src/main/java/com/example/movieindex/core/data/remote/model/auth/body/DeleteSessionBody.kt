package com.example.movieindex.core.data.remote.model.auth.body

import com.squareup.moshi.Json

data class DeleteSessionBody(
    @Json(name = "session_id")
    val sessionId: String
)