package com.example.movieindex.core.data.remote.model.account


import com.squareup.moshi.Json

data class Avatar(
    @Json(name = "gravatar")
    val gravatar: Gravatar,
    @Json(name = "tmdb")
    val tmdb: Tmdb,
)