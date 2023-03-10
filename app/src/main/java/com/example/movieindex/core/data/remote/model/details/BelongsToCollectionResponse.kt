package com.example.movieindex.core.data.remote.model.details


import com.squareup.moshi.Json

data class BelongsToCollectionResponse(
    @Json(name = "backdrop_path")
    val backdrop_path: String?,
    @Json(name = "id")
    val id: Int,
    @Json(name = "name")
    val name: String?,
    @Json(name = "poster_path")
    val poster_path: String?,
)