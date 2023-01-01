package com.example.movieindex.core.data.remote.model.details


import com.example.movieindex.core.data.external.AuthorDetails
import com.squareup.moshi.Json

data class AuthorDetailsResponse(
    @Json(name = "avatar_path") val avatarPath: String?,
    @Json(name = "name") val name: String,
    @Json(name = "rating") val rating: Double?,
    @Json(name = "username") val username: String,
)

fun AuthorDetailsResponse.toAuthorDetails() =
    AuthorDetails(avatarPath = avatarPath, name = name, rating = rating, username = username)