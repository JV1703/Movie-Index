package com.example.movieindex.core.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.movieindex.core.data.external.Result

@Entity(tableName = "movies_table")
data class MovieEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val movieId: Int,
    val title: String,
    val overview: String?,
    val genreIds: List<String>,
    val popularity: Double?,
    val posterPath: String?,
    val backdropPath: String?,
    val adult: Boolean,
    val voteAverage: Double?,
    val releaseDate: String?,
    val pagingCategory: MoviePagingCategory,
)

fun MovieEntity.toResult() = Result(
    id = id,
    movieId = movieId,
    title = title,
    overview = overview,
    genres = genreIds,
    popularity = popularity,
    posterPath = posterPath,
    backdropPath = backdropPath,
    adult = adult,
    voteAverage = voteAverage,
    releaseDate = releaseDate,
    pagingCategory = pagingCategory
)

enum class MoviePagingCategory(val categoryName: String) {
    NOW_PLAYING("Now Playing"), POPULAR("Popular"), TRENDING("Trending")
}