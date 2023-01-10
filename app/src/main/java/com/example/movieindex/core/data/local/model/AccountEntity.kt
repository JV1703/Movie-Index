package com.example.movieindex.core.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.movieindex.core.data.external.model.AccountDetails

@Entity(tableName = "account_table")
data class AccountEntity(
    val avatarPath: String,
    @PrimaryKey
    val id: Int,
    val include_adult: Boolean,
    val iso_3166_1: String,
    val iso_639_1: String,
    val name: String,
    val username: String,
)

fun AccountEntity.toAccountDetails() = AccountDetails(
    avatarPath = avatarPath,
    id = id,
    includeAdult = include_adult,
    iso31661 = iso_3166_1,
    iso6391 = iso_639_1,
    name = name,
    username = username
)