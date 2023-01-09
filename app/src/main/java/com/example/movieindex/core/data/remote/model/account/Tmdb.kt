package com.example.movieindex.core.data.remote.model.account


import com.squareup.moshi.Json

data class Tmdb(
    @Json(name = "avatar_path")
    val avatarPath: Any?
)