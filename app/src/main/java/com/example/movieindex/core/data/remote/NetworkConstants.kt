package com.example.movieindex.core.data.remote

object NetworkConstants {

    const val BASE_URL = "https://api.themoviedb.org/3/"
    const val BASE_IMG_URL = "https://image.tmdb.org/t/p/"
    const val AVATAR_URL = "https://secure.gravatar.com/avatar/"

    const val POSTER_SIZE = "original/"
    const val BACKDROP_SIZE = "original/"

    const val YT_BASE_URL = "https://www.youtube.com/watch?v="

    fun getYtThumbnail(key: String?): String? {
        return key?.let { "https://img.youtube.com/vi/${it}/maxresdefault.jpg" }
    }

}