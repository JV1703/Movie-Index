package com.example.movieindex.feature.auth.domain.abstraction

import com.example.movieindex.core.data.external.model.Resource
import com.example.movieindex.core.data.remote.model.auth.response.DeleteSessionResponse
import com.example.movieindex.core.data.remote.model.auth.response.SessionIdResponse
import kotlinx.coroutines.flow.Flow

interface AuthUseCase {

    fun login(username: String, password: String): Flow<Resource<SessionIdResponse>>
    fun isUserLoggedIn(): Flow<Boolean>

    suspend fun saveSessionId(sessionId: String)
    fun getSessionId(): Flow<String>

    fun logout(): Flow<Resource<DeleteSessionResponse>>
}