package com.example.movieindex.feature.main.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieindex.core.data.external.model.AccountDetails
import com.example.movieindex.core.data.external.model.Resource
import com.example.movieindex.feature.auth.domain.abstraction.AuthUseCase
import com.example.movieindex.feature.main.ui.account.domain.abstraction.AccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountUseCase: AccountUseCase,
    private val authUseCase: AuthUseCase,
) :
    ViewModel() {

    private var job: Job? = null

    private val accountDetails = accountUseCase.getAccountDetailsCache()
    private val favoriteMoviesCount = accountUseCase.getFavoriteCountCache()
    private val watchlistMoviesCount = accountUseCase.getWatchlistCountCache()

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState = _uiState.asStateFlow()

    init {
        combine(accountDetails,
            favoriteMoviesCount,
            watchlistMoviesCount) { accountDetails, favoriteCount, watchlistCount ->
            if (accountDetails == null) {
                _uiState.update { it.copy(isLoggedIn = false) }
            } else {
                Timber.i("accountViewModel - accountDetails: $accountDetails")
                _uiState.update {
                    it.copy(
                        accountDetails = accountDetails,
                        favoriteMoviesCount = favoriteCount,
                        watchlistMoviesCount = watchlistCount)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun logout() {
        job = authUseCase.logout().map { resource ->
            when (resource) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false,
                            isLoggedIn = !resource.data.success)
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    updateUserMsg(msg = resource.errMsg ?: "Unknown Error")
                }
                is Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
                is Resource.Empty -> {
                    _uiState.update { it.copy(isLoading = false) }
                    updateUserMsg("Unknown Error")
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun updateUserMsg(msg: String) {
        _uiState.update { it.copy(userMsg = msg) }
    }

    fun userMsgShown() {
        _uiState.update { it.copy(userMsg = null) }
    }

    data class AccountUiState(
        val isLoading: Boolean = false,
        val isLoggedIn: Boolean = true,
        val accountDetails: AccountDetails? = null,
        val favoriteMoviesCount: Int? = null,
        val watchlistMoviesCount: Int? = null,
        val userMsg: String? = null,
    )

}