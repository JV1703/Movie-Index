package com.example.movieindex.core.data.external.model

import com.example.movieindex.core.data.local.model.MoviePagingCategory
import com.example.movieindex.core.data.local.model.MoviePagingEntity
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
    val popularity: Double?,
)

fun MovieDetails.toMoviePagingEntity(pagingCategory: MoviePagingCategory) = MoviePagingEntity(
    id = "$id/$pagingCategory",
    movieId = id,
    title = title,
    overview = overview,
    genreIds = genres.map { it.name },
    popularity = popularity,
    posterPath = posterPath,
    backdropPath = backdropPath,
    adult = adult,
    voteAverage = voteAverage,
    releaseDate = releaseDate,
    pagingCategory = pagingCategory
)