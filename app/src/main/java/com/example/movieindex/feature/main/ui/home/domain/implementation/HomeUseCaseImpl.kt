package com.example.movieindex.feature.main.ui.home.domain.implementation

import com.example.movieindex.core.data.external.model.Resource
import com.example.movieindex.core.data.external.model.Result
import com.example.movieindex.core.repository.abstraction.MovieRepository
import com.example.movieindex.feature.main.ui.home.domain.abstraction.HomeUseCase
import javax.inject.Inject

class HomeUseCaseImpl @Inject constructor(private val movieRepository: MovieRepository) :
    HomeUseCase {

    override suspend fun getNowPlaying(
        page: Int,
        language: String?,
        region: String?,
    ): Resource<List<Result>> =
        movieRepository.getNowPlaying(
            page = page,
            language = language,
            region = region,
        )

    override suspend fun getPopularMovies(
        page: Int,
        language: String?,
        region: String?,
    ): Resource<List<Result>> = movieRepository.getPopularMovies(page = page,
        language = language,
        region = region)

    override suspend fun getTrendingMovies(
        page: Int,
        mediaType: String,
        timeWindow: String,
    ): Resource<List<Result>> = movieRepository.getTrendingMovies(page = page,
        mediaType = mediaType,
        timeWindow = timeWindow)

}