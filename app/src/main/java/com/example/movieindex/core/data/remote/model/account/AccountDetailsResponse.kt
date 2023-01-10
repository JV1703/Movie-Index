package com.example.movieindex.core.data.remote.model.account


import com.example.movieindex.core.data.external.AccountDetails
import com.squareup.moshi.Json

data class AccountDetailsResponse(
    @Json(name = "avatar")
    val avatar: Avatar,
    @Json(name = "id")
    val id: Int,
    @Json(name = "include_adult")
    val include_adult: Boolean,
    @Json(name = "iso_3166_1")
    val iso_3166_1: String,
    @Json(name = "iso_639_1")
    val iso_639_1: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "username")
    val username: String
)

fun AccountDetailsResponse.toAccountDetails() = AccountDetails(avatar, id, include_adult, iso_3166_1, iso_639_1, name, username)