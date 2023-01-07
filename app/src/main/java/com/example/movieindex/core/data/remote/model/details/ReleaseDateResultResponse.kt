package com.example.movieindex.core.data.remote.model.details


import com.example.movieindex.core.data.external.ReleaseDate
import com.squareup.moshi.Json

data class ReleaseDateResultResponse(
    @Json(name = "iso_3166_1")
    val iso_3166_1: String,
    @Json(name = "release_dates")
    val release_dates: List<ReleaseDateDetailsResponse>,
)

fun ReleaseDateResultResponse.toReleaseDate() = ReleaseDate(
    country = iso_3166_1,
    releaseDates = release_dates.map { it.toReleaseDetails() }
)