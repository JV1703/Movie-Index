package com.example.movieindex.core.data.remote.model.common


import com.example.movieindex.core.data.external.Result
import com.example.movieindex.core.data.external.movieGenreMapper
import com.example.movieindex.core.data.local.model.MovieEntity
import com.example.movieindex.core.data.local.model.MoviePagingCategory
import com.squareup.moshi.Json

data class ResultResponse(
    @Json(name = "adult") val adult: Boolean,
    @Json(name = "backdrop_path") val backdropPath: String?,
    @Json(name = "genre_ids") val genreIds: List<Int>,
    @Json(name = "id") val id: Int,
    @Json(name = "media_type") val mediaType: String?,
    @Json(name = "original_language") val originalLanguage: String?,
    @Json(name = "original_title") val originalTitle: String?,
    @Json(name = "overview") val overview: String?,
    @Json(name = "popularity") val popularity: Double?,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "release_date") val releaseDate: String?,
    @Json(name = "title") val title: String,
    @Json(name = "video") val video: Boolean,
    @Json(name = "vote_average") val voteAverage: Double?,
    @Json(name = "vote_count") val voteCount: Int?,
)

fun ResultResponse.toResult(pagingCategory: MoviePagingCategory? = null) = Result(
    id = id,
    movieId = id,
    title = title,
    genres = movieGenreMapper(genreIds),
    popularity = popularity,
    posterPath = posterPath,
    backdropPath = backdropPath,
    adult = adult,
    voteAverage = voteAverage,
    releaseDate = releaseDate,
    overview = overview,
    pagingCategory = pagingCategory
)

fun ResultResponse.toMovieEntity(pagingCategory: MoviePagingCategory) = MovieEntity(
    movieId = id,
    title = title,
    genreIds = movieGenreMapper(genreIds),
    overview = overview,
    popularity = popularity,
    posterPath = posterPath,
    backdropPath = backdropPath,
    adult = adult,
    voteAverage = voteAverage,
    releaseDate = releaseDate,
    pagingCategory = pagingCategory
)