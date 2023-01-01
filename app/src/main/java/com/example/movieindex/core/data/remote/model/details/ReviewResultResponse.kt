package com.example.movieindex.core.data.remote.model.details


import com.example.movieindex.core.data.external.ReviewResult
import com.squareup.moshi.Json

data class ReviewResultResponse(
    @Json(name = "author") val author: String,
    @Json(name = "author_details") val authorDetails: AuthorDetailsResponse,
    @Json(name = "content") val content: String,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "id") val id: String,
    @Json(name = "updated_at") val updatedAt: String,
    @Json(name = "url") val url: String,
)

fun ReviewResultResponse.toReviewResult() = ReviewResult(author = author,
    authorDetails = authorDetails.toAuthorDetails(),
    content = content,
    createdAt = createdAt,
    id = id,
    updatedAt = updatedAt,
    url = url)