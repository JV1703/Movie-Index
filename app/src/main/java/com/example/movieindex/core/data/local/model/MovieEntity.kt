package com.example.movieindex.core.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.movieindex.core.data.external.MovieDetails
import com.example.movieindex.core.data.external.SavedMovie

@Entity(tableName = "movies_table")
data class MovieEntity(
    @PrimaryKey(autoGenerate = false)
    val movieId: Int,
    val title: String,
    val releaseDate: String?,
    val overview: String?,
    val posterPath: String?,
    val isFavorite: Boolean,
    val isBookmark: Boolean,
)

fun MovieEntity.toSavedMovie() = SavedMovie(
    movieId = movieId,
    title = title,
    releaseDate = releaseDate,
    overview = overview,
    posterPath = posterPath,
    isFavorite = isFavorite,
    isBookmark = isBookmark
)