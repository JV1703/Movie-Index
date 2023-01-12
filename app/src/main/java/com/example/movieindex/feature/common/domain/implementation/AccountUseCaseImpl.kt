package com.example.movieindex.feature.common.domain.implementation

import com.example.movieindex.core.data.external.model.AccountDetails
import com.example.movieindex.core.data.external.model.Resource
import com.example.movieindex.core.repository.abstraction.AccountRepository
import com.example.movieindex.feature.common.domain.abstraction.AccountUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AccountUseCaseImpl @Inject constructor(
    private val accountRepository: AccountRepository,
) : AccountUseCase {

    override fun getAccountId(): Flow<Int?> = accountRepository.getAccountIdCache()

    override fun getAccountDetails(sessionId: String): Flow<Resource<AccountDetails?>> =
        accountRepository.getAccountDetails(sessionId = sessionId)

}