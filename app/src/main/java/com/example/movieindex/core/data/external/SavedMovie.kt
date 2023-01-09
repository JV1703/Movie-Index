package com.example.movieindex.core.data.external

data class SavedMovie(
    val movieId: Int,
    val title: String,
    val releaseDate: String?,
    val overview: String?,
    val posterPath: String?,
    val isFavorite: Boolean,
    val isBookmark: Boolean,
)