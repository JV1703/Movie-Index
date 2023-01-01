package com.example.movieindex.core.data.remote.model.details


import com.squareup.moshi.Json

data class GenreResponse(
    @Json(name = "id")
    val id: Int,
    @Json(name = "name")
    val name: String,
)