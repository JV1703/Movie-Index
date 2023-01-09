package com.example.movieindex.core.data.remote.model.account


import com.squareup.moshi.Json

data class Gravatar(
    @Json(name = "hash")
    val hash: String
)