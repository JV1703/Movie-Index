package com.example.movieindex.core.data.remote.model.details


import com.example.movieindex.core.data.external.ReviewResult
import com.squareup.moshi.Json

data class ReviewResultResponse(
    @Json(name = "author") val author: String,
    @Json(name = "author_details") val author_details: AuthorDetailsResponse?,
    @Json(name = "content") val content: String,
    @Json(name = "created_at") val created_at: String,
    @Json(name = "id") val id: String,
    @Json(name = "updated_at") val updated_at: String,
    @Json(name = "url") val url: String,
)

fun ReviewResultResponse.toReviewResult() = ReviewResult(author = author,
    authorDetails = author_details?.toAuthorDetails(),
    content = content,
    createdAt = created_at,
    id = id,
    updatedAt = updated_at,
    url = url)