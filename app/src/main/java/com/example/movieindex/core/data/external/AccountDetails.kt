package com.example.movieindex.core.data.external

import com.example.movieindex.core.data.remote.model.account.Avatar
import com.squareup.moshi.Json

data class AccountDetails(
    val avatar: Avatar,
    val id: Int,
    val includeAdult: Boolean,
    val iso31661: String,
    val iso6391: String,
    val name: String,
    val username: String
)