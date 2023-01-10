package com.example.movieindex.core.data.remote.model.auth.response

import com.squareup.moshi.Json

data class DeleteSessionResponse(
    @Json(name = "success")
    val success: Boolean,
)