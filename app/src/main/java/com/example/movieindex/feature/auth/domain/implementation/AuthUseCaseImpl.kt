package com.example.movieindex.feature.auth.domain.implementation

import com.example.movieindex.core.data.external.Resource
import com.example.movieindex.core.data.remote.model.auth.response.SessionIdResponse
import com.example.movieindex.core.repository.abstraction.AuthRepository
import com.example.movieindex.feature.auth.domain.abstraction.AuthUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthUseCaseImpl @Inject constructor(private val repository: AuthRepository) :
    AuthUseCase {

    override fun login(username: String, password: String): Flow<Resource<SessionIdResponse>> =
        repository.login(username = username,
            password = password)

    override fun isUserLoggedIn(): Flow<Boolean> = getSessionId().map {
        it.isNotEmpty()
    }

    override suspend fun saveSessionId(sessionId: String) {
        repository.saveSessionId(sessionId = sessionId)
    }

    override fun getSessionId(): Flow<String> = repository.getSessionId()

    override suspend fun clearDataStore() {
        repository.clearDataStore()
    }
}