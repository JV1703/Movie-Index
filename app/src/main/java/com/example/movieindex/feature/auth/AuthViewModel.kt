package com.example.movieindex.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieindex.core.data.external.Resource
import com.example.movieindex.core.data.remote.NetworkConstants.TMDB_RESET_PASSWORD
import com.example.movieindex.core.data.remote.NetworkConstants.TMDB_SIGN_UP_URL
import com.example.movieindex.feature.auth.domain.abstraction.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authUseCase: AuthUseCase) : ViewModel() {

    private val _loginEvents = MutableSharedFlow<AuthEvents>()
    val loginEvents = _loginEvents.asSharedFlow()

    private val _authUiState = MutableStateFlow<AuthUiState>(AuthUiState.IsNotLoggedIn())
    val authUiState = _authUiState.asStateFlow()

    init {
        authUseCase.isUserLoggedIn().map { isLoggedIn: Boolean ->
            if (isLoggedIn) {
                _authUiState.value = AuthUiState.IsLoggedIn
            }
        }.launchIn(viewModelScope)
    }

    fun validateUserLogin(username: String, password: String) {
        val trimmedUsername = username.trim()
        viewModelScope.launch {

            if (username.isEmpty() && password.isEmpty()) {
                _loginEvents.emit(AuthEvents.InvalidInput(errMsg = "empty email & password field"))
                return@launch
            }

            if (username.isEmpty()) {
                _loginEvents.emit(AuthEvents.InvalidInput(errMsg = "empty username field"))
                return@launch
            }

            if (password.isEmpty()) {
                _loginEvents.emit(AuthEvents.InvalidInput(errMsg = "empty password field"))
                return@launch
            }

            if (!(username.isEmpty() && password.isEmpty())) {
                login(username = trimmedUsername, password = password)
            }
        }
    }

    private fun login(username: String, password: String) {

        authUseCase.login(username = username, password = password).map { loginResource ->
            when (loginResource) {
                is Resource.Loading -> {
                    _authUiState.value = AuthUiState.IsNotLoggedIn(isLoading = true)
                }
                is Resource.Empty -> {
                    _authUiState.value = AuthUiState.IsNotLoggedIn(isLoading = false)
                    _loginEvents.emit(AuthEvents.AuthError(errMsg = "Unknown Error"))
                }
                is Resource.Success -> {
                    _authUiState.value = AuthUiState.IsNotLoggedIn(isLoading = false)
                    _loginEvents.emit(AuthEvents.Success)

                }
                is Resource.Error -> {
                    _authUiState.value = AuthUiState.IsNotLoggedIn(isLoading = false)
                    _loginEvents.emit(AuthEvents.AuthError(errMsg = loginResource.errMsg
                        ?: "Unknown Error"))
                }
            }
        }.launchIn(viewModelScope)


    }

    sealed interface AuthEvents {
        data class InvalidInput(val errMsg: String) : AuthEvents
        data class AuthError(val errMsg: String) : AuthEvents
        object Success : AuthEvents
    }

    sealed interface AuthUiState {
        object IsLoggedIn : AuthUiState
        data class IsNotLoggedIn(val isLoading: Boolean = false) : AuthUiState
    }

    enum class AuthNavigationArgs(val url: String) {
        Register(TMDB_SIGN_UP_URL),
        ForgetPassword(TMDB_RESET_PASSWORD)
    }

}