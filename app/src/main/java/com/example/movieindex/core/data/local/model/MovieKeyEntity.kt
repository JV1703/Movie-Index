package com.example.movieindex.core.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_key_table")
data class MovieKeyEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val movieId: Int,
    val prevKey: Int?,
    val nextKey: Int?,
    val pagingCategory: MoviePagingCategory,
)