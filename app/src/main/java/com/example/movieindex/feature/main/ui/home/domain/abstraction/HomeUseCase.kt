package com.example.movieindex.feature.main.ui.home.domain.abstraction

import com.example.movieindex.core.data.external.model.Resource
import com.example.movieindex.core.data.external.model.Result

interface HomeUseCase {

    suspend fun getNowPlaying(
        page: Int = 1,
        language: String? = null,
        region: String? = null,

        ): Resource<List<Result>>

    suspend fun getPopularMovies(
        page: Int = 1,
        language: String? = null,
        region: String? = null,

        ): Resource<List<Result>>

    suspend fun getTrendingMovies(
        page: Int = 1,
        mediaType: String = "movie",
        timeWindow: String = "week",

        ): Resource<List<Result>>

}