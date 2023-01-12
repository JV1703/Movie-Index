package com.example.movieindex.core.data.external.model

import com.example.movieindex.core.data.local.model.AccountEntity

data class AccountDetails(
    val avatarPath: String,
    val id: Int,
    val includeAdult: Boolean,
    val iso31661: String,
    val iso6391: String,
    val name: String,
    val username: String,
)

fun AccountDetails.toAccountEntity() = AccountEntity(
    avatarPath = avatarPath,
    id = id,
    include_adult = includeAdult,
    iso_3166_1 = iso31661,
    iso_639_1 = iso6391,
    name = name,
    username = username)