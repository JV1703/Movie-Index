package com.example.movieindex.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieindex.core.data.external.model.Resource
import com.example.movieindex.core.data.remote.NetworkConstants.TMDB_RESET_PASSWORD
import com.example.movieindex.core.data.remote.NetworkConstants.TMDB_SIGN_UP_URL
import com.example.movieindex.feature.auth.domain.abstraction.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authUseCase: AuthUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private var job: Job? = null

    init {
        authUseCase.isUserLoggedIn().map { isLoggedIn: Boolean ->
            if (isLoggedIn) {
                _uiState.update { it.copy(isLoggedIn = isLoggedIn) }
            }
        }.launchIn(viewModelScope)
    }

    fun validateUserLogin(username: String, password: String) {
        val trimmedUsername = username.trim()
        viewModelScope.launch {

            if (username.isEmpty() && password.isEmpty()) {
                _uiState.update { it.copy(userMsg = "empty email & password field") }
                return@launch
            }

            if (username.isEmpty()) {
                _uiState.update { it.copy(userMsg = "empty username field") }
                return@launch
            }

            if (password.isEmpty()) {
                _uiState.update { it.copy(userMsg = "empty password field") }
                return@launch
            }

            if (!(username.isEmpty() && password.isEmpty())) {
                login(username = trimmedUsername, password = password)
            }
        }
    }

    private fun login(username: String, password: String) {

        if (job != null) return

        job = viewModelScope.launch {

            _uiState.update { it.copy(isLoading = true) }

            when (val resource = authUseCase.login(username = username, password = password)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
                    Timber.i("AuthViewModel - success, $resource")
                }
                is Resource.Error -> {
                    Timber.i("AuthViewModel - error, $resource")
                    _uiState.update { it.copy(isLoading = false, userMsg = resource.errMsg) }
                }
                is Resource.Empty -> {
                    Timber.i("AuthViewModel - empty, $resource")
                    _uiState.update { it.copy(isLoading = false, userMsg = "Empty Response") }
                }
            }

            job = null
        }
    }

    fun userMsgShown() {
        _uiState.update { it.copy(userMsg = null) }
    }

    enum class AuthNavigationArgs(val url: String) {
        Register(TMDB_SIGN_UP_URL),
        ForgetPassword(TMDB_RESET_PASSWORD)
    }

    data class UiState(
        val isLoading: Boolean = false,
        val isLoggedIn: Boolean = false,
        val userMsg: String? = null,
    )

}