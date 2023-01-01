package com.example.movieindex.core.data.external

import com.example.movieindex.core.data.local.model.MovieEntity
import com.example.movieindex.core.data.local.model.MoviePagingCategory

data class Result(
    val id: Int?,
    val movieId: Int,
    val title: String,
    val genres: List<String>,
    val overview: String?,
    val popularity: Double?,
    val posterPath: String?,
    val backdropPath: String?,
    val adult: Boolean,
    val voteAverage: Double?,
    val releaseDate: String?,
    val pagingCategory: MoviePagingCategory?,
)

fun Result.toMovieEntity(pagingCategory: MoviePagingCategory) = MovieEntity(
    id = id,
    movieId = movieId,
    title = title,
    overview = overview,
    genreIds = genres,
    popularity = popularity,
    posterPath = posterPath,
    backdropPath = backdropPath,
    adult = adult,
    voteAverage = voteAverage,
    releaseDate = releaseDate,
    pagingCategory = pagingCategory
)

val genreList = mapOf(
    28 to "Action",
    12 to "Adventure",
    16 to "Animation",
    35 to "Comedy",
    80 to "Crime",
    99 to "Documentary",
    18 to "Drama",
    10751 to "Family",
    14 to "Fantasy",
    36 to "History",
    27 to "Horror",
    10402 to "Music",
    9648 to "Mystery",
    10749 to "Romance",
    878 to "Science Fiction",
    10770 to "TV Movie",
    53 to "Thriller",
    10752 to "War",
    37 to "Western")

fun movieGenreMapper(genreIds: List<Int>?): List<String> {
    val output = arrayListOf<String>()
    genreIds?.forEach { genreId ->
        output.add(genreList[genreId] ?: "Unknown genre")
    }
    return output
}


/*

{"genres":[{"id":28,"name":"Action"},
{"id":12,"name":"Adventure"},
{"id":16,"name":"Animation"},
{"id":35,"name":"Comedy"},
{"id":80,"name":"Crime"},
{"id":99,"name":"Documentary"},
{"id":18,"name":"Drama"},
{"id":10751,"name":"Family"},
{"id":14,"name":"Fantasy"},
{"id":36,"name":"History"},
{"id":27,"name":"Horror"},
{"id":10402,"name":"Music"},
{"id":9648,"name":"Mystery"},
{"id":10749,"name":"Romance"},
{"id":878,"name":"Science Fiction"},
{"id":10770,"name":"TV Movie"},
{"id":53,"name":"Thriller"},
{"id":10752,"name":"War"},
{"id":37,"name":"Western"}]}

*/