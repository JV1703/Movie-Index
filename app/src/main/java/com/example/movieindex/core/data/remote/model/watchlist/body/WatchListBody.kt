package com.example.movieindex.core.data.remote.model.watchlist.body


import com.squareup.moshi.Json

data class WatchListBody(
    @Json(name = "media_id")
    val mediaId: Int,
    @Json(name = "media_type")
    val mediaType: String,
    @Json(name = "watchlist")
    val watchlist: Boolean
)