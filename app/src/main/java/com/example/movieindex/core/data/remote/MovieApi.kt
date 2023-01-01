package com.example.movieindex.core.data.remote

import com.example.movieindex.core.data.remote.model.common.MoviesResponse
import com.example.movieindex.core.data.remote.model.details.MovieDetailsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {

    @GET("movie/now_playing")
    suspend fun getNowPlaying(
        @Query("page") page: Int = 1,
        @Query("language") language: String? = null,
        @Query("region") region: String? = null,
    ): Response<MoviesResponse>

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int = 1,
        @Query("language") language: String? = null,
        @Query("region") region: String? = null,
    ): Response<MoviesResponse>

    @GET("trending/{media_type}/{time_window}")
    suspend fun getTrendingMovies(
        @Path("media_type") mediaType: String = "movie",
        @Path("time_window") timeWindow: String = "week",
        @Query("page") page: Int = 1,
    ): Response<MoviesResponse>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String? = null,
        @Query("append_to_response") appendToResponse: String? = "videos,recommendations,credits,reviews,release_dates",
    ): Response<MovieDetailsResponse>

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("language") language: String? = null,
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean? = null,
        @Query("region") region: String? = null,
        @Query("year") year: Int? = null,
        @Query("primary_release_year") primaryReleaseYear: Int? = null,
    ): Response<MoviesResponse>

}