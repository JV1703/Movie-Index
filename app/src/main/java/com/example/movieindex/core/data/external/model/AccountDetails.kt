package com.example.movieindex.core.data.external.model

data class AccountDetails(
    val avatarPath: String,
    val id: Int,
    val includeAdult: Boolean,
    val iso31661: String,
    val iso6391: String,
    val name: String,
    val username: String,
)