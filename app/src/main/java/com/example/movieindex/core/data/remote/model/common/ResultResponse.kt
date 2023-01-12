package com.example.movieindex.core.data.remote.model.common


import com.example.movieindex.core.data.external.model.Result
import com.example.movieindex.core.data.external.model.movieGenreMapper
import com.example.movieindex.core.data.local.model.MoviePagingCategory
import com.example.movieindex.core.data.local.model.MoviePagingEntity
import com.squareup.moshi.Json

data class ResultResponse(
    @Json(name = "adult") val adult: Boolean,
    @Json(name = "backdrop_path") val backdrop_path: String?,
    @Json(name = "genre_ids") val genre_ids: List<Int>,
    @Json(name = "id") val id: Int,
    @Json(name = "media_type") val media_type: String?,
    @Json(name = "original_language") val original_language: String?,
    @Json(name = "original_title") val original_title: String?,
    @Json(name = "overview") val overview: String?,
    @Json(name = "popularity") val popularity: Double?,
    @Json(name = "poster_path") val poster_path: String?,
    @Json(name = "release_date") val release_date: String?,
    @Json(name = "title") val title: String,
    @Json(name = "video") val video: Boolean,
    @Json(name = "vote_average") val vote_average: Double?,
    @Json(name = "vote_count") val vote_count: Int?,
)

fun ResultResponse.toResult() = Result(
    movieId = id,
    title = title,
    genres = movieGenreMapper(genre_ids),
    popularity = popularity,
    posterPath = poster_path,
    backdropPath = backdrop_path,
    adult = adult,
    voteAverage = vote_average,
    releaseDate = release_date,
    overview = overview
)

fun ResultResponse.toMoviePagingEntity(pagingCategory: MoviePagingCategory) = MoviePagingEntity(
    id = "$id/$pagingCategory",
    movieId = id,
    title = title,
    overview = overview,
    genreIds = movieGenreMapper(genre_ids),
    popularity = popularity,
    posterPath = poster_path,
    backdropPath = backdrop_path,
    adult = adult,
    voteAverage = vote_average,
    releaseDate = release_date,
    pagingCategory = pagingCategory
)