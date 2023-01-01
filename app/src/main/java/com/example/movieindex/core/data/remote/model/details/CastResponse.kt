package com.example.movieindex.core.data.remote.model.details


import com.example.movieindex.core.data.external.Cast
import com.squareup.moshi.Json

data class CastResponse(
    @Json(name = "adult") val adult: Boolean,
    @Json(name = "cast_id") val castId: Int,
    @Json(name = "character") val character: String,
    @Json(name = "credit_id") val creditId: String,
    @Json(name = "gender") val gender: Int,
    @Json(name = "id") val id: Int,
    @Json(name = "known_for_department") val knownForDepartment: String,
    @Json(name = "name") val name: String,
    @Json(name = "order") val order: Int,
    @Json(name = "original_name") val originalName: String,
    @Json(name = "popularity") val popularity: Double,
    @Json(name = "profile_path") val profilePath: String?,
)

fun CastResponse.toCast() = Cast(
    adult = adult,
    castId = castId,
    character = character,
    creditId = creditId,
    gender = gender,
    id = id,
    knownForDepartment = knownForDepartment,
    name = name,
    order = order,
    originalName = originalName,
    popularity = popularity,
    profilePath = profilePath)