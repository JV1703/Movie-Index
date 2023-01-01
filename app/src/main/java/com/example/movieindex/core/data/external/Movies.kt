package com.example.movieindex.core.data.external

import com.example.movieindex.core.data.remote.model.now_playing.Dates

data class Movies(
    val dates: Dates?,
    val page: Int,
    val results: List<Result>,
    val totalPages: Int,
    val totalResults: Int,
)