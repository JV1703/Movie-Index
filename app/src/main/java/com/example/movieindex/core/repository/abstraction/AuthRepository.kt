package com.example.movieindex.core.repository.abstraction

import com.example.movieindex.core.data.external.model.Resource
import com.example.movieindex.core.data.remote.model.auth.response.DeleteSessionResponse
import com.example.movieindex.core.data.remote.model.auth.response.SessionIdResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun login(username: String, password: String): Resource<SessionIdResponse>

    suspend fun saveSessionId(sessionId: String)
    fun getSessionId(): Flow<String>
    suspend fun clearDataStore()
    suspend fun deleteSession(): Resource<DeleteSessionResponse>
}