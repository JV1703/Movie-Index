package com.example.movieindex.core.data.external.model

data class ReleaseDetails(
    val certification: String?,
    val iso6391: String?,
    val note: String?,
    val releaseDate: String?,
    val type: Int?,
)

// type
fun parseReleaseType() = mapOf(
    1 to "Premiere",
    2 to "Theatrical(limited)",
    3 to "Theatrical",
    4 to "Digital",
    5 to "Physical",
    6 to "TV"
)