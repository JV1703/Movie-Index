package com.example.movieindex.core.data.remote.model.details


import com.squareup.moshi.Json

data class ProductionCountryResponse(
    @Json(name = "iso_3166_1")
    val iso_3166_1: String,
    @Json(name = "name")
    val name: String,
)