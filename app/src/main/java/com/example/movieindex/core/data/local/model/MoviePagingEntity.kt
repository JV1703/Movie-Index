package com.example.movieindex.core.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.movieindex.core.data.external.model.Result

@Entity(tableName = "movie_paging_table")
data class MoviePagingEntity(
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

fun MoviePagingEntity.toResult() = Result(
    movieId = movieId,
    title = title,
    genres = genreIds,
    overview = overview,
    popularity = popularity,
    posterPath = posterPath,
    backdropPath = backdropPath,
    adult = adult,
    voteAverage = voteAverage,
    releaseDate = releaseDate
)