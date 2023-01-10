package com.example.movieindex.core.data.remote.model.details


import com.example.movieindex.core.data.external.model.Crew
import com.squareup.moshi.Json

data class CrewResponse(
    @Json(name = "adult") val adult: Boolean,
    @Json(name = "credit_id") val credit_id: String,
    @Json(name = "department") val department: String,
    @Json(name = "gender") val gender: Int,
    @Json(name = "id") val id: Int,
    @Json(name = "job") val job: String,
    @Json(name = "known_for_department") val known_for_department: String,
    @Json(name = "name") val name: String,
    @Json(name = "original_name") val original_name: String,
    @Json(name = "popularity") val popularity: Double,
    @Json(name = "profile_path") val profile_path: String?,
)

fun CrewResponse.toCrew() = Crew(
    adult = adult,
    creditId = credit_id,
    department = department,
    gender = gender,
    id = id,
    job = job,
    knownForDepartment = known_for_department,
    name = name,
    originalName = original_name,
    popularity = popularity,
    profilePath = profile_path
)