package com.example.movieindex.util

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.movieindex.core.common.extensions.fromJson
import com.example.movieindex.core.data.external.model.MovieDetails
import com.example.movieindex.core.data.external.model.Result
import com.example.movieindex.core.data.remote.model.account.AccountDetailsResponse
import com.example.movieindex.core.data.remote.model.account.MovieAccountStateResponse
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
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.random.Random.Default.nextBoolean
import kotlin.random.Random.Default.nextInt

class TestDataFactory {

    private val gson = Gson()

    fun <T> generateResponse(
        isSuccess: Boolean = true, isBodyEmpty: Boolean = false, successResponse: () -> Response<T>,
    ): Response<T> {
        return if (!isSuccess) {
            val errorResponse =
                "{\"status_code\": 34,\"status_message\": \"The resource you requested could not be found.\",\"success\": false }"
            Response.error(404,
                errorResponse.toResponseBody("application/json".toMediaTypeOrNull()))
        } else {
            if (!isBodyEmpty) {
                successResponse()
            } else {
                Response.success(null)
            }
        }
    }

//    private fun readFile(fileName: String): String {
//        var inputStream: InputStream? = null
//        try {
//            inputStream = javaClass.classLoader?.getResourceAsStream(fileName)
//            val builder = StringBuilder()
//            val reader = BufferedReader(InputStreamReader(inputStream))
//
//            var str = reader.readLine()
//            while (str != null) {
//                builder.append(str)
//                str = reader.readLine()
//            }
//            return builder.toString()
//        } finally {
//            inputStream?.close()
//        }
//    }

    fun readFile(fileName: String): String {
        var inputStream: InputStream? = null
        try {
            val applicationContext = ApplicationProvider.getApplicationContext<Context>()
            inputStream = applicationContext.assets.open(fileName)
            val builder = StringBuilder()
            val reader = InputStreamReader(inputStream, "UTF-8")
            reader.readLines().forEach {
                builder.append(it)
            }
            return builder.toString()
        } catch (e: IOException) {
            throw e
        } finally {
            inputStream?.close()
        }
    }

    fun generateMovieDetailsTestData(): MovieDetailsResponse {
        val json = readFile("movie_details.json")
        return gson.fromJson(json)
    }

    fun generateNowPlayingMoviesTestData(): MoviesResponse {
        val json = readFile("now_playing.json")
        return gson.fromJson(json)
    }

    fun generatePopularMoviesTestData(): MoviesResponse {
        val json = readFile("popular_movies.json")
        return gson.fromJson(json)
    }

    fun generateTrendingMoviesTestData(): MoviesResponse {
        val json = readFile("trending_movies.json")
        return gson.fromJson(json)
    }

    fun generateSearchMoviesTestData(): MoviesResponse {
        val json = readFile("search_movies.json")
        return gson.fromJson(json)
    }

    fun generateRecommendationsTestData(): MoviesResponse {
        val json = readFile("recommendations.json")
        return gson.fromJson(json)
    }

    private fun getTokenExpireTime(): String {
        val localDateTime = LocalDateTime.ofEpochSecond(10_000, 0, ZoneOffset.UTC).plusHours(1)
        return "$localDateTime UTC"
    }

    fun generateRequestTokenTestData(): RequestTokenResponse {
        return RequestTokenResponse(
            expires_at = getTokenExpireTime(),
            request_token = UUID.randomUUID().toString(),
            success = true)
    }

    fun generateLoginTestData(loginBody: LoginBody): LoginResponse {
        return LoginResponse(
            expires_at = getTokenExpireTime(),
            request_token = loginBody.request_token,
            success = true)
    }

    fun generateSessionIdTestData(): SessionIdResponse {
        return SessionIdResponse(session_id = UUID.randomUUID().toString(), success = true)
    }

    fun generateAccountDetailsResponseTestData(): AccountDetailsResponse {
        val json = readFile("account_details.json")
        return gson.fromJson(json)
    }

    fun generatePostResponseTestData(): PostResponse {
        val json = readFile("post_response.json")
        return gson.fromJson(json)
    }

    fun generateFavoriteMoviesListResponseTestData(): MoviesResponse {
        val json = readFile("popular_movies.json")
        return gson.fromJson(json)
    }

    fun generateWatchListResponseTestData(): MoviesResponse {
        val json = readFile("popular_movies.json")
        return gson.fromJson(json)
    }

    fun generateDeleteSessionResponseTestData(): DeleteSessionResponse {
        return DeleteSessionResponse(success = true)
    }

    fun generateMovieAccountStateResponseTestData(): MovieAccountStateResponse {
        return MovieAccountStateResponse(favorite = true,
            id = nextInt(99_999),
            rated = nextBoolean(),
            watchlist = true)
    }

    fun generateLoginBody() = LoginBody(username = "", password = "", request_token = "")
    fun generateFavoriteBody(favorite: Boolean = true) = FavoriteBody(favorite = favorite,
        mediaId = 0,
        mediaType = "")

    fun generateWatchListBody(watchList: Boolean = true) = WatchListBody(watchlist = watchList,
        mediaId = 0,
        mediaType = "")

    fun toMovieDetails(
        movie: Result,
    ) = MovieDetails(
        id = movie.movieId,
        imdbId = null,
        adult = movie.adult,
        movie.backdropPath,
        movie.posterPath,
        casts = emptyList(),
        crews = emptyList(),
        genres = emptyList(),
        overview = movie.overview,
        recommendations = null,
        releaseDate = movie.releaseDate,
        runtime = null,
        tagline = null,
        title = movie.title,
        videos = emptyList(),
        voteAverage = movie.voteAverage,
        reviews = null,
        mpaaRating = null,
        popularity = null
    )

}