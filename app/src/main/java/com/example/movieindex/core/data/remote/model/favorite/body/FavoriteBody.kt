package com.example.movieindex.core.data.remote.model.favorite.body


import com.squareup.moshi.Json

data class FavoriteBody(
    @Json(name = "favorite")
    val favorite: Boolean,
    @Json(name = "media_id")
    val mediaId: Int,
    @Json(name = "media_type")
    val mediaType: String
)