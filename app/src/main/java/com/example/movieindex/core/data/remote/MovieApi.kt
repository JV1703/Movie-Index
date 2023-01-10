package com.example.movieindex.core.data.remote

import com.example.movieindex.core.data.remote.model.account.AccountDetailsResponse
import com.example.movieindex.core.data.remote.model.account.MovieAccountStateResponse
import com.example.movieindex.core.data.remote.model.auth.body.DeleteSessionBody
import com.example.movieindex.core.data.remote.model.auth.body.LoginBody
import com.example.movieindex.core.data.remote.model.auth.response.DeleteSessionResponse
import com.example.movieindex.core.data.remote.model.auth.response.LoginResponse
import com.example.movieindex.core.data.remote.model.auth.response.RequestTokenResponse
import com.example.movieindex.core.data.remote.model.auth.response.SessionIdResponse
import com.example.movieindex.core.data.remote.model.common.MoviesResponse
import com.example.movieindex.core.data.remote.model.common.PostResponse
import com.example.movieindex.core.data.remote.model.details.MovieDetailsResponse
import com.example.movieindex.core.data.remote.model.favorite.body.FavoriteBody
import com.example.movieindex.core.data.remote.model.watchlist.body.WatchListBody
import retrofit2.Response
import retrofit2.http.*

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

    @GET("movie/{movie_id}/recommendations")
    suspend fun getMovieRecommendations(
        @Path("movie_id") movieId: Int,
        @Query("page") page: Int = 1,
        @Query("language") language: String? = null,
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

    @GET("authentication/token/new")
    suspend fun requestToken(): Response<RequestTokenResponse>

    @POST("authentication/token/validate_with_login")
    @Headers("Accept: application/json")
    suspend fun login(
        @Body loginBody: LoginBody,
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST("authentication/session/new")
    suspend fun createSession(
        @Field("request_token") requestToken: String,
    ): Response<SessionIdResponse>

    @GET("account")
    suspend fun getAccountDetails(
        @Query("session_id") sessionId: String,
    ): Response<AccountDetailsResponse>

    @POST("account/{account_id}/favorite")
    suspend fun addToFavorite(
        @Path("account_id") accountId: Int,
        @Query("session_id") sessionId: String,
        @Body body: FavoriteBody,
    ): Response<PostResponse>

    @POST("account/{account_id}/watchlist")
    suspend fun addToWatchList(
        @Path("account_id") accountId: Int,
        @Query("session_id") sessionId: String,
        @Body body: WatchListBody,
    ): Response<PostResponse>

    @GET("account/{account_id}/favorite/movies")
    suspend fun getFavoriteList(
        @Path("account_id") accountId: Int,
        @Query("session_id") sessionId: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String? = null,
        @Query("sort_by") sortBy: String? = null,
    ):Response<MoviesResponse>

    @GET("account/{account_id}/watchlist/movies")
    suspend fun getWatchList(
        @Path("account_id") accountId: Int,
        @Query("session_id") sessionId: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String? = null,
        @Query("sort_by") sortBy: String? = null,
    ):Response<MoviesResponse>

    @HTTP(method = "DELETE", path = "authentication/session", hasBody = true)
    suspend fun deleteSession(
        @Body body: DeleteSessionBody,
    ): Response<DeleteSessionResponse>

    @GET("movie/{movie_id}/account_states")
    suspend fun getMovieAccountState(
        @Path("movie_id")movieId: Int,
        @Query("session_id")sessionId: String
    ): Response<MovieAccountStateResponse>
}