package com.example.movieindex.feature.main.ui.account.domain.implementation

import com.example.movieindex.core.data.external.model.AccountDetails
import com.example.movieindex.core.repository.abstraction.AuthRepository
import com.example.movieindex.core.repository.abstraction.MovieRepository
import com.example.movieindex.feature.main.ui.account.domain.abstraction.AccountUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AccountUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val movieRepository: MovieRepository,
) : AccountUseCase {

    override fun getAccountDetailsCache(): Flow<AccountDetails?> =
        movieRepository.getAccountDetailsCache()

    override fun getFavoriteCountCache(): Flow<Int> = movieRepository.getFavoriteCountCache()

    override fun getWatchlistCountCache(): Flow<Int> = movieRepository.getWatchlistCountCache()

}