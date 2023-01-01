package com.example.movieindex.core.data.remote.model.details


import com.squareup.moshi.Json

data class ReleaseDatesResponse(
    @Json(name = "results")
    val results: List<ReleaseDateResultResponse>,
)