package com.example.movieindex.feature.auth.domain.abstraction

import com.example.movieindex.core.data.external.model.Resource
import com.example.movieindex.core.data.remote.model.auth.response.DeleteSessionResponse
import com.example.movieindex.core.data.remote.model.auth.response.SessionIdResponse
import kotlinx.coroutines.flow.Flow

interface AuthUseCase {

    suspend fun login(username: String, password: String): Resource<SessionIdResponse>
    fun isUserLoggedIn(): Flow<Boolean>

    fun getSessionId(): Flow<String>

    suspend fun logout(): Resource<DeleteSessionResponse>
}