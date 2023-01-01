package com.example.movieindex.core.data.remote.model.common

import com.example.movieindex.core.data.external.Movies
import com.example.movieindex.core.data.remote.model.now_playing.Dates
import com.squareup.moshi.Json

data class MoviesResponse(
    @Json(name = "dates") val dates: Dates?,
    @Json(name = "page") val page: Int,
    @Json(name = "results") val results: List<ResultResponse>,
    @Json(name = "total_pages") val total_pages: Int,
    @Json(name = "total_results") val total_results: Int,
)

fun MoviesResponse.toMovies() = Movies(dates = dates,
    page = page,
    results = results.map { it.toResult() },
    totalPages = total_pages,
    totalResults = total_results)