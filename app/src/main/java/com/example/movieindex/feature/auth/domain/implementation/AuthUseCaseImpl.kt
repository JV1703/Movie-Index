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

    override fun login(username: String, password: String): Flow<Resource<SessionIdResponse>> =
        repository.login(username = username,
            password = password)

    override fun isUserLoggedIn(): Flow<Boolean> = getSessionId().map {
        Timber.i("cached sessionId - $it")
        it.isNotEmpty()
    }

    override suspend fun saveSessionId(sessionId: String) {
        repository.saveSessionId(sessionId = sessionId)
    }

    override fun getSessionId(): Flow<String> = repository.getSessionId()

    override fun logout(): Flow<Resource<DeleteSessionResponse>> = repository.deleteSession()

}