package com.example.movieindex.core.data.remote.model.details


import com.example.movieindex.core.data.external.ReleaseDetails
import com.squareup.moshi.Json

data class ReleaseDateDetailsResponse(
    @Json(name = "certification")
    val certification: String?,
    @Json(name = "iso_639_1")
    val iso6391: String?,
    @Json(name = "note")
    val note: String?,
    @Json(name = "release_date")
    val releaseDate: String?,
    @Json(name = "type")
    val type: Int?,
)

fun ReleaseDateDetailsResponse.toReleaseDetails() = ReleaseDetails(
    certification = certification,
    iso6391 = iso6391,
    note = note,
    releaseDate = releaseDate,
    type = type
)