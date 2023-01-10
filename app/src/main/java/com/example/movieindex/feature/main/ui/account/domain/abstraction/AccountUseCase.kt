package com.example.movieindex.feature.main.ui.account.domain.abstraction

import com.example.movieindex.core.data.external.model.AccountDetails
import kotlinx.coroutines.flow.Flow

interface AccountUseCase {
    fun getAccountDetailsCache(): Flow<AccountDetails?>
    fun getWatchlistCountCache(): Flow<Int>
    fun getFavoriteCountCache(): Flow<Int>
}