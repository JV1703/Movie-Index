@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.movieindex.feature.main.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieindex.core.data.external.model.AccountDetails
import com.example.movieindex.core.data.external.model.Resource
import com.example.movieindex.feature.auth.domain.abstraction.AuthUseCase
import com.example.movieindex.feature.common.domain.abstraction.AccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountUseCase: AccountUseCase,
    private val authUseCase: AuthUseCase,
) :
    ViewModel() {

    private var job: Job? = null

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState = _uiState.asStateFlow()

    init {

        authUseCase.getSessionId()
            .flatMapLatest { sessionId: String ->
                accountUseCase.getAccountDetails(sessionId)
            }.map { resource ->
                when (resource) {
                    is Resource.Success -> {
                        if (resource.data == null) {
                            _uiState.update { it.copy(isLoggedIn = false) }
                        } else {
                            _uiState.update { it.copy(accountDetails = resource.data) }
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(userMsg = resource.errMsg) }
                    }
                    is Resource.Empty -> {
                        _uiState.update { it.copy(userMsg = "Empty Response") }
                    }
                }
            }.launchIn(viewModelScope)

    }

    fun logout() {

        _uiState.update { it.copy(isLoading = true) }

        if (job != null) return

        job = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val resource = authUseCase.logout()) {
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
                is Resource.Empty -> {
                    _uiState.update { it.copy(isLoading = false) }
                    updateUserMsg("Unknown Error")
                }
            }
        }
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