package com.example.movieindex.core.data.remote.model.details


import com.example.movieindex.core.data.external.Crew
import com.squareup.moshi.Json

data class CrewResponse(
    @Json(name = "adult") val adult: Boolean,
    @Json(name = "credit_id") val creditId: String,
    @Json(name = "department") val department: String,
    @Json(name = "gender") val gender: Int,
    @Json(name = "id") val id: Int,
    @Json(name = "job") val job: String,
    @Json(name = "known_for_department") val knownForDepartment: String,
    @Json(name = "name") val name: String,
    @Json(name = "original_name") val originalName: String,
    @Json(name = "popularity") val popularity: Double,
    @Json(name = "profile_path") val profilePath: String?,
)

fun CrewResponse.toCrew() = Crew(
    adult = adult,
    creditId = creditId,
    department = department,
    gender = gender,
    id = id,
    job = job,
    knownForDepartment = knownForDepartment,
    name = name,
    originalName = originalName,
    popularity = popularity,
    profilePath = profilePath
)