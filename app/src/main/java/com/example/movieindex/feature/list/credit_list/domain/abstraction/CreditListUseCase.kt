package com.example.movieindex.feature.list.credit_list.domain.abstraction

import com.example.movieindex.core.data.external.model.Cast
import com.example.movieindex.core.data.external.model.Crew
import kotlinx.coroutines.flow.Flow

interface CreditListUseCase {

    fun getCasts(): Flow<List<Cast>>

    fun getCrews(): Flow<List<Crew>>

}