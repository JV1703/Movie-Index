package com.example.movieindex.core.data.remote.model.details


import com.example.movieapp_v2.core.data.remote.model.details.*
import com.example.movieindex.core.data.external.MovieDetails
import com.example.movieindex.core.data.remote.model.common.MoviesResponse
import com.example.movieindex.core.data.remote.model.common.toMovies
import com.squareup.moshi.Json

data class MovieDetailsResponse(
    @Json(name = "adult") val adult: Boolean,
    @Json(name = "backdrop_path") val backdropPath: String?,
    @Json(name = "belongs_to_collection") val belongsToCollection: BelongsToCollectionResponse?,
    @Json(name = "budget") val budget: Int?,
    @Json(name = "credits") val credits: CreditsResponse?,
    @Json(name = "genres") val genres: List<GenreResponse>,
    @Json(name = "homepage") val homepage: String?,
    @Json(name = "id") val id: Int,
    @Json(name = "imdb_id") val imdbId: String?,
    @Json(name = "original_language") val originalLanguage: String?,
    @Json(name = "original_title") val originalTitle: String?,
    @Json(name = "overview") val overview: String?,
    @Json(name = "popularity") val popularity: Double?,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "production_companies") val productionCompanies: List<ProductionCompanyResponse>,
    @Json(name = "production_countries") val productionCountries: List<ProductionCountryResponse>,
    @Json(name = "release_dates") val releaseDates: ReleaseDatesResponse?,
    @Json(name = "recommendations") val recommendations: MoviesResponse?,
    @Json(name = "release_date") val releaseDate: String?,
    @Json(name = "revenue") val revenue: Long?,
    @Json(name = "reviews") val reviews: ReviewsResponse?,
    @Json(name = "runtime") val runtime: Int?,
    @Json(name = "spoken_languages") val spokenLanguages: List<SpokenLanguageResponse>,
    @Json(name = "status") val status: String?,
    @Json(name = "tagline") val tagline: String?,
    @Json(name = "title") val title: String?,
    @Json(name = "video") val video: Boolean?,
    @Json(name = "videos") val videos: VideosResponse?,
    @Json(name = "vote_average") val voteAverage: Double?,
    @Json(name = "vote_count") val voteCount: Int?,
)

fun MovieDetailsResponse.toMovieDetails() = MovieDetails(
    id = id,
    imdbId = imdbId,
    adult = adult,
    backdropPath = backdropPath,
    posterPath = posterPath,
    casts = credits?.castResponse?.map { it.toCast() } ?: emptyList(),
    crews = credits?.crewResponse?.map { it.toCrew() } ?: emptyList(),
    genres = genres,
    overview = overview,
    recommendations = recommendations?.toMovies(),
    releaseDate = releaseDate,
    runtime = runtime,
    tagline = tagline,
    title = title,
    videos = videos?.results?.map { it.toVideosResult() }?.filter { it.site == "YouTube" }
        ?: emptyList(),
    voteAverage = voteAverage,
    reviews = reviews?.results?.map { it.toReviewResult() } ?: emptyList(),
    mpaaRating = releaseDates?.results?.find { it.iso31661 == "US" }?.releaseDates?.find { it.type == 1 }?.certification
)