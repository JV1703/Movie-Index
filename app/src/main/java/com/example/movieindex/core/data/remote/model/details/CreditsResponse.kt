package com.example.movieindex.core.data.remote.model.details


import com.squareup.moshi.Json

data class CreditsResponse(
    @Json(name = "cast")
    val castResponse: List<CastResponse>,
    @Json(name = "crew")
    val crewResponse: List<CrewResponse>,
)