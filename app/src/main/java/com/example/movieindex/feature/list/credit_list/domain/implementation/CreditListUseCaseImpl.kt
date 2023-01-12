package com.example.movieindex.feature.list.credit_list.domain.implementation

import com.example.movieindex.core.data.external.model.Cast
import com.example.movieindex.core.data.external.model.Crew
import com.example.movieindex.core.repository.abstraction.MovieRepository
import com.example.movieindex.feature.list.credit_list.domain.abstraction.CreditListUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreditListUseCaseImpl @Inject constructor(private val movieRepository: MovieRepository) :
    CreditListUseCase {

    override fun getCrews(): Flow<List<Crew>> = movieRepository.getCrews()

    override fun getCasts(): Flow<List<Cast>> = movieRepository.getCasts()

}