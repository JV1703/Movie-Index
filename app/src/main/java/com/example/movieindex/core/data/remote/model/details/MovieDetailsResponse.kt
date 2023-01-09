package com.example.movieindex.core.data.remote.model.details

import com.example.movieindex.core.data.external.MovieDetails
import com.example.movieindex.core.data.remote.model.common.MoviesResponse
import com.example.movieindex.core.data.remote.model.common.toResult
import com.squareup.moshi.Json

data class MovieDetailsResponse(
    @Json(name = "adult") val adult: Boolean,
    @Json(name = "backdrop_path") val backdrop_path: String?,
    @Json(name = "belongs_to_collection") val belongs_to_collection: BelongsToCollectionResponse?,
    @Json(name = "budget") val budget: Int?,
    @Json(name = "credits") val credits: CreditsResponse?,
    @Json(name = "genres") val genres: List<GenreResponse>,
    @Json(name = "homepage") val homepage: String?,
    @Json(name = "id") val id: Int,
    @Json(name = "imdb_id") val imdb_id: String?,
    @Json(name = "original_language") val original_language: String?,
    @Json(name = "original_title") val original_title: String?,
    @Json(name = "overview") val overview: String?,
    @Json(name = "popularity") val popularity: Double?,
    @Json(name = "poster_path") val poster_path: String?,
    @Json(name = "production_companies") val production_companies: List<ProductionCompanyResponse>,
    @Json(name = "production_countries") val production_countries: List<ProductionCountryResponse>,
    @Json(name = "release_dates") val release_dates: ReleaseDatesResponse?,
    @Json(name = "recommendations") val recommendations: MoviesResponse?,
    @Json(name = "release_date") val release_date: String?,
    @Json(name = "revenue") val revenue: Long?,
    @Json(name = "reviews") val reviews: ReviewsResponse?,
    @Json(name = "runtime") val runtime: Int?,
    @Json(name = "spoken_languages") val spoken_languages: List<SpokenLanguageResponse>,
    @Json(name = "status") val status: String?,
    @Json(name = "tagline") val tagline: String?,
    @Json(name = "title") val title: String,
    @Json(name = "video") val video: Boolean?,
    @Json(name = "videos") val videos: VideosResponse?,
    @Json(name = "vote_average") val vote_average: Double?,
    @Json(name = "vote_count") val vote_count: Int?,
)

fun MovieDetailsResponse.toMovieDetails() = MovieDetails(
    id = id,
    imdbId = imdb_id,
    adult = adult,
    backdropPath = backdrop_path,
    posterPath = poster_path,
    casts = credits?.cast?.map { it.toCast() } ?: emptyList(),
    crews = credits?.crew?.map { it.toCrew() } ?: emptyList(),
    genres = genres,
    overview = overview,
    recommendations = recommendations?.results?.map { it.toResult() },
    releaseDate = release_date,
    runtime = runtime,
    tagline = tagline,
    title = title,
    videos = videos?.results?.map { it.toVideosResult() }?.filter { it.site == "YouTube" }
        ?: emptyList(),
    voteAverage = vote_average,
    reviews = reviews?.results?.map { it.toReviewResult() } ?: emptyList(),
    mpaaRating = release_dates?.results?.find { it.iso_3166_1 == "US" }?.release_dates?.find { it.certification != null && it.certification.isNotEmpty() }?.certification
        ?: ""
)