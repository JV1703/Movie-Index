package com.example.movieindex.core.data.remote.model.details


import com.squareup.moshi.Json

data class ReviewsResponse(
    @Json(name = "page")
    val page: Int,
    @Json(name = "results")
    val results: List<ReviewResultResponse>,
    @Json(name = "total_pages")
    val totalPages: Int,
    @Json(name = "total_results")
    val totalResults: Int,
)