package com.example.movieindex.core.data.remote

object NetworkConstants {

    const val CACHE_SIZE = (50 * 1024 * 1024).toLong()

    const val BASE_URL = "https://api.themoviedb.org/3/"
    const val BASE_IMG_URL = "https://image.tmdb.org/t/p/"
    const val AVATAR_URL = "https://secure.gravatar.com/avatar/"
    const val TMDB_SIGN_UP_URL = "https://www.themoviedb.org/signup"
    const val TMDB_RESET_PASSWORD = "https://www.themoviedb.org/reset-password"

    const val POSTER_SIZE_LARGE = "w342/"
    const val POSTER_SIZE_SMALL = "w185/"
    const val BACKDROP_SIZE = "w780/"
    const val CREDIT_IMG_SIZE = "w185/"

    const val YT_BASE_URL = "https://www.youtube.com/watch?v="

    fun getYtThumbnail(key: String?): String? {
        return key?.let { "https://img.youtube.com/vi/${it}/maxresdefault.jpg" }
    }

    const val TMDB_RESET_REGISTER_SUCCESS_CHECKER = "Account verification required"
    const val TMDB_RESET_PASSWORD_SUCCESS_CHECKER =
        "The reset instructions have been sent to this email address if it belongs to a registered account on TMDB."

}