package com.example.movieindex.core.repository.abstraction

import com.example.movieindex.core.data.external.Resource
import com.example.movieindex.core.data.remote.model.auth.response.SessionIdResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun login(username: String, password: String): Flow<Resource<SessionIdResponse>>

    suspend fun saveSessionId(sessionId: String)
    fun getSessionId(): Flow<String>
    suspend fun clearDataStore()

//    fun requestToken(): Flow<Resource<RequestTokenResponse>>
//    fun loginUser(
//        username: String,
//        password: String,
//        requestToken: String
//    ): Flow<Resource<LoginResponse>>
//
//    fun createSession(requestToken: String): Flow<Resource<SessionIdResponse>>
}