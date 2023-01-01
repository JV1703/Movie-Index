package com.example.movieindex.core.data.remote.implementation

import com.example.movieindex.core.data.remote.MovieApi
import com.example.movieindex.core.data.remote.NetworkResource
import com.example.movieindex.core.data.remote.abstraction.NetworkDataSource
import com.example.movieindex.core.data.remote.model.common.MoviesResponse
import com.example.movieindex.core.data.remote.model.details.MovieDetailsResponse
import com.example.movieindex.core.data.remote.safeNetworkCall
import com.example.movieindex.core.di.CoroutinesQualifiers
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class NetworkDataSourceImpl @Inject constructor(
    private val movieApi: MovieApi,
    @CoroutinesQualifiers.IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : NetworkDataSource {

    override suspend fun getNowPlaying(
        page: Int,
        language: String?,
        region: String?,
    ): NetworkResource<MoviesResponse> =
        safeNetworkCall(dispatcher = ioDispatcher,
            networkCall = {
                movieApi.getNowPlaying(page = page,
                    language = language,
                    region = region)
            },
            conversion = { data: MoviesResponse -> data })

    override suspend fun getPopularMovies(
        page: Int,
        language: String?,
        region: String?,
    ): NetworkResource<MoviesResponse> =
        safeNetworkCall(dispatcher = ioDispatcher, networkCall = {
            movieApi.getPopularMovies(page = page, language = language, region = region)
        }, conversion = { data: MoviesResponse -> data })

    override suspend fun getTrendingMovies(
        page: Int,
        mediaType: String,
        timeWindow: String,
    ): NetworkResource<MoviesResponse> =
        safeNetworkCall(dispatcher = ioDispatcher, networkCall = {
            movieApi.getTrendingMovies(page = page, mediaType = mediaType, timeWindow = timeWindow)
        }, conversion = { data: MoviesResponse -> data })

    override suspend fun getMovieDetails(
        movieId: Int,
        language: String?,
        appendToResponse: String?,
    ): NetworkResource<MovieDetailsResponse> =
        safeNetworkCall(dispatcher = ioDispatcher, networkCall = {
            movieApi.getMovieDetails(movieId, language, appendToResponse)
        }, conversion = { data: MovieDetailsResponse -> data })

    override suspend fun searchMovies(
        query: String,
        language: String?,
        page: Int,
        includeAdult: Boolean?,
        region: String?,
        year: Int?,
        primaryReleaseYear: Int?,
    ): NetworkResource<MoviesResponse> = safeNetworkCall(dispatcher = ioDispatcher,
        networkCall = { movieApi.searchMovies(query = query) },
        conversion = { it })

}