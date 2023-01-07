package com.example.movieindex.core.data.remote.model.details


import com.squareup.moshi.Json

data class SpokenLanguageResponse(
    @Json(name = "english_name")
    val english_name: String,
    @Json(name = "iso_639_1")
    val iso_639_1: String,
    @Json(name = "name")
    val name: String,
)