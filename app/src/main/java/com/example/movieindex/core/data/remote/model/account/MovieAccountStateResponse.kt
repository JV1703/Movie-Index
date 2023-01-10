package com.example.movieindex.core.data.remote.model.account


import com.example.movieindex.core.data.external.model.MovieAccountState
import com.squareup.moshi.Json

data class MovieAccountStateResponse(
    @Json(name = "favorite")
    val favorite: Boolean,
    @Json(name = "id")
    val id: Int,
    @Json(name = "rated")
    val rated: Boolean,
    @Json(name = "watchlist")
    val watchlist: Boolean,
)

fun MovieAccountStateResponse.toMovieAccountState() = MovieAccountState(
    favorite = favorite, id = id, rated = rated, watchlist = watchlist
)