package com.example.movieindex.feature.auth.domain.implementation

import com.example.movieindex.core.data.external.model.Resource
import com.example.movieindex.core.data.remote.model.auth.response.DeleteSessionResponse
import com.example.movieindex.core.data.remote.model.auth.response.SessionIdResponse
import com.example.movieindex.core.repository.abstraction.AuthRepository
import com.example.movieindex.feature.auth.domain.abstraction.AuthUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class AuthUseCaseImpl @Inject constructor(private val repository: AuthRepository) :
    AuthUseCase {

    override suspend fun login(username: String, password: String): Resource<SessionIdResponse> =
        repository.login(username = username,
            password = password)

    override fun isUserLoggedIn(): Flow<Boolean> = repository.getSessionId().map {
        Timber.i("cached sessionId - $it")
        it.isNotEmpty()
    }

    override fun getSessionId(): Flow<String> = repository.getSessionId()

    override suspend fun logout(): Resource<DeleteSessionResponse> = repository.deleteSession()

}