package com.example.movieindex.feature.common.domain.implementation

import androidx.paging.PagingData
import com.example.movieindex.core.data.external.*
import com.example.movieindex.core.repository.abstraction.MovieRepository
import com.example.movieindex.feature.common.domain.abstraction.MovieUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MovieUseCaseImpl @Inject constructor(private val repository: MovieRepository) : MovieUseCase {

    override fun getNowPlaying(
        page: Int,
        language: String?,
        region: String?,
    ): Flow<Resource<List<Result>>> =
        repository.getNowPlaying(page = page, language = language, region = region)

    override fun getPopularMovies(
        page: Int,
        language: String?,
        region: String?,
    ): Flow<Resource<List<Result>>> = repository.getNowPlaying(page = page,
        language = language,
        region = region)

    override fun getTrendingMovies(
        page: Int,
        mediaType: String,
        timeWindow: String,
    ): Flow<Resource<List<Result>>> = repository.getTrendingMovies(page = page,
        mediaType = mediaType,
        timeWindow = timeWindow)


    override fun getMovieDetails(
        movieId: Int,
        language: String?,
        appendToResponse: String?,
    ): Flow<Resource<MovieDetails>> = repository.getMovieDetails(movieId = movieId,
        language = language,
        appendToResponse = appendToResponse)

    override fun getNowPlayingPagingSource(
        loadSinglePage: Boolean,
        region: String?,
        language: String?,
    ): Flow<PagingData<Result>> =
        repository.getNowPlayingPagingSource(loadSinglePage = loadSinglePage,
            region = region,
            language = language)

    override fun getPopularMoviesPagingSource(
        loadSinglePage: Boolean,
        region: String?,
        language: String?,
    ): Flow<PagingData<Result>> =
        repository.getPopularMoviesPagingSource(loadSinglePage = loadSinglePage,
            region = region,
            language = language)

    override fun getTrendingMoviesPagingSource(
        loadSinglePage: Boolean,
        mediaType: String,
        timeWindow: String,
    ): Flow<PagingData<Result>> =
        repository.getTrendingMoviesPagingSource(loadSinglePage = loadSinglePage,
            mediaType = mediaType,
            timeWindow = timeWindow)

    override fun getMovieRecommendationPagingSource(
        loadSinglePage: Boolean,
        movieId: Int,
        language: String?,
    ): Flow<PagingData<Result>> =
        repository.getMovieRecommendationPagingSource(loadSinglePage = loadSinglePage,
            movieId = movieId,
            language = language)

    override fun searchMoviesPagingSource(
        loadSinglePage: Boolean,
        query: String,
        language: String?,
        includeAdult: Boolean?,
        region: String?,
        year: Int?,
        primaryReleaseYear: Int?,
    ) = repository.searchMoviesPagingSource(
        loadSinglePage = loadSinglePage,
        query = query,
        language = language,
        includeAdult = includeAdult,
        region = region,
        year = year)

    override suspend fun saveCasts(casts: List<Cast>) {
        repository.saveCasts(casts)
    }

    override fun getCasts(): Flow<List<Cast>> = repository.getCasts()

    override suspend fun saveCrews(crews: List<Crew>) {
        repository.saveCrews(crews)
    }

    override fun getCrews(): Flow<List<Crew>> = repository.getCrews()
}