package com.example.movieindex.feature.common.domain.abstraction

import com.example.movieindex.core.data.external.model.AccountDetails
import com.example.movieindex.core.data.external.model.Resource
import kotlinx.coroutines.flow.Flow

interface AccountUseCase {
    fun getAccountDetails(sessionId: String): Flow<Resource<AccountDetails?>>
    fun getAccountId(): Flow<Int?>
}