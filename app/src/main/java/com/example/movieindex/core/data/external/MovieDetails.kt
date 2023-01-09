package com.example.movieindex.core.data.external

import com.example.movieindex.core.data.local.model.MovieEntity
import com.example.movieindex.core.data.remote.model.details.GenreResponse

data class MovieDetails(
    val id: Int,
    val imdbId: String?,
    val adult: Boolean,
    val backdropPath: String?,
    val posterPath: String?,
    val casts: List<Cast>,
    val crews: List<Crew>,
    val genres: List<GenreResponse>,
    val overview: String?,
    val recommendations: List<Result>?,
    val releaseDate: String?,
    val runtime: Int?,
    val tagline: String?,
    val title: String,
    val videos: List<VideosResult>,
    val voteAverage: Double?,
    val reviews: List<ReviewResult>?,
    val mpaaRating: String?,
)

fun MovieDetails.toMovieEntity(isFavorite: Boolean = false, isBookmark: Boolean = false) = MovieEntity(
    movieId = id,
    title = title,
    overview = overview,
    posterPath = posterPath,
    releaseDate = releaseDate,
    isFavorite = isFavorite,
    isBookmark = isBookmark
)