package com.example.movieindex.core.data.remote.model.details


import com.example.movieindex.core.data.external.model.ReleaseDetails
import com.squareup.moshi.Json

data class ReleaseDateDetailsResponse(
    @Json(name = "certification")
    val certification: String?,
    @Json(name = "iso_639_1")
    val iso_639_1: String?,
    @Json(name = "note")
    val note: String?,
    @Json(name = "release_date")
    val release_date: String?,
    @Json(name = "type")
    val type: Int?,
)

fun ReleaseDateDetailsResponse.toReleaseDetails() = ReleaseDetails(
    certification = certification,
    iso6391 = iso_639_1,
    note = note,
    releaseDate = release_date,
    type = type
)