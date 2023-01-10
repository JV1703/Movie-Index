package com.example.movieindex.core.data.external.model

data class MovieAccountState(
    val favorite: Boolean,
    val id: Int,
    val rated: Boolean,
    val watchlist: Boolean
)
