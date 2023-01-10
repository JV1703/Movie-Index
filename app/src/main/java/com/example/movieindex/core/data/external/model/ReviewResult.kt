package com.example.movieindex.core.data.external.model

data class ReviewResult(
    val author: String,
    val authorDetails: AuthorDetails?,
    val content: String,
    val createdAt: String,
    val id: String,
    val updatedAt: String,
    val url: String,
)