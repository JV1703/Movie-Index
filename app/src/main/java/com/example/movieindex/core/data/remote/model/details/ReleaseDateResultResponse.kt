package com.example.movieindex.core.data.remote.model.details


import com.example.movieindex.core.data.external.ReleaseDate
import com.squareup.moshi.Json

data class ReleaseDateResultResponse(
    @Json(name = "iso_3166_1")
    val iso31661: String,
    @Json(name = "release_dates")
    val releaseDates: List<ReleaseDateDetailsResponse>,
)

fun ReleaseDateResultResponse.toReleaseDate() = ReleaseDate(
    country = iso31661,
    releaseDates = releaseDates.map { it.toReleaseDetails() }
)