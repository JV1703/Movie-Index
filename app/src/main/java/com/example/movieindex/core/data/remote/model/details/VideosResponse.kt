package com.example.movieindex.core.data.remote.model.details


import com.squareup.moshi.Json

data class VideosResponse(
    @Json(name = "results")
    val results: List<VideosResultResponse>,
)