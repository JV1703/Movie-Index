package com.example.movieindex.feature.main.ui.search.domain.abstraction

import androidx.paging.PagingData
import com.example.movieindex.core.data.external.model.Result
import kotlinx.coroutines.flow.Flow

interface SearchUseCase {

    fun searchMoviesPagingSource(
        loadSinglePage: Boolean = false,
        query: String,
        language: String? = null,
        includeAdult: Boolean? = null,
        region: String? = null,
        year: Int? = null,
        primaryReleaseYear: Int? = null,
    ): Flow<PagingData<Result>>

}