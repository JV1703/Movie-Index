package com.example.movieindex.feature.main.ui.search.domain.implementation

import com.example.movieindex.core.repository.abstraction.MovieRepository
import com.example.movieindex.feature.main.ui.search.domain.abstraction.SearchUseCase
import javax.inject.Inject

class SearchUseCaseImpl @Inject constructor(private val movieRepository: MovieRepository) :
    SearchUseCase {

    override fun searchMoviesPagingSource(
        loadSinglePage: Boolean,
        query: String,
        language: String?,
        includeAdult: Boolean?,
        region: String?,
        year: Int?,
        primaryReleaseYear: Int?,
    ) = movieRepository.searchMoviesPagingSource(
        loadSinglePage = loadSinglePage,
        query = query,
        language = language,
        includeAdult = includeAdult,
        region = region,
        year = year)

}