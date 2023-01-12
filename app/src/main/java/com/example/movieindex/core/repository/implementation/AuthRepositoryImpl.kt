package com.example.movieindex.core.repository.implementation

import com.example.movieindex.core.data.external.model.Resource
import com.example.movieindex.core.data.local.abstraction.CacheDataSource
import com.example.movieindex.core.data.remote.NetworkResource
import com.example.movieindex.core.data.remote.abstraction.NetworkDataSource
import com.example.movieindex.core.data.remote.model.account.toAccountEntity
import com.example.movieindex.core.data.remote.model.auth.body.DeleteSessionBody
import com.example.movieindex.core.data.remote.model.auth.body.LoginBody
import com.example.movieindex.core.data.remote.model.auth.response.DeleteSessionResponse
import com.example.movieindex.core.data.remote.model.auth.response.SessionIdResponse
import com.example.movieindex.core.repository.abstraction.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val network: NetworkDataSource,
    private val cache: CacheDataSource,
) : AuthRepository {

    override suspend fun saveSessionId(sessionId: String) {
        cache.saveSessionId(sessionId)
    }

    override fun getSessionId(): Flow<String> = cache.getSessionId()

    override suspend fun clearDataStore() {
        cache.clearDataStore()
    }

    override suspend fun login(username: String, password: String): Resource<SessionIdResponse> {
        return when (val requestToken = network.requestToken()) {
            is NetworkResource.Success -> {
                val uploadData = LoginBody(
                    username = username,
                    password = password,
                    request_token = requestToken.data.request_token
                )
                when (val loginRequest = network.login(loginBody = uploadData)) {
                    is NetworkResource.Success -> {
                        when (val sessionIdRequest =
                            network.createSession(requestToken = loginRequest.data.request_token)) {
                            is NetworkResource.Success -> {
                                when (val accountDetailsRequest =
                                    network.getAccountDetails(sessionId = sessionIdRequest.data.session_id)) {
                                    is NetworkResource.Success -> {
                                        val data = accountDetailsRequest.data.toAccountEntity()
                                        cache.insertAccountDetails(account = data)
                                        saveSessionId(sessionId = sessionIdRequest.data.session_id)
                                        Resource.Success(data = sessionIdRequest.data)
                                    }
                                    is NetworkResource.Error -> {
                                        Resource.Error(errCode = accountDetailsRequest.errCode,
                                            errMsg = accountDetailsRequest.errMsg)
                                    }
                                    is NetworkResource.Empty -> {
                                        Resource.Error(errMsg = "Empty Response")
                                    }
                                }
                            }
                            is NetworkResource.Error -> {
                                Resource.Error(errCode = sessionIdRequest.errCode,
                                    errMsg = sessionIdRequest.errMsg)
                            }
                            is NetworkResource.Empty -> {
                                Resource.Empty
                            }
                        }
                    }
                    is NetworkResource.Error -> {
                        Resource.Error(errCode = loginRequest.errCode, errMsg = loginRequest.errMsg)
                    }
                    is NetworkResource.Empty -> {
                        Resource.Empty
                    }
                }
            }
            is NetworkResource.Error -> {
                Resource.Error(errCode = requestToken.errCode, errMsg = requestToken.errMsg)
            }
            is NetworkResource.Empty -> {
                Resource.Empty
            }
        }
    }

    override suspend fun deleteSession(): Resource<DeleteSessionResponse> {
        val sessionId = cache.getSessionId().first()
        val body = DeleteSessionBody(sessionId = sessionId)
        return when (val networkResource = network.deleteSession(body = body)) {
            is NetworkResource.Success -> {

                if (networkResource.data.success) {
                    cache.deleteAccountDetails()
                    cache.clearDataStore()
                }
                Resource.Success(data = networkResource.data)

            }
            is NetworkResource.Error -> {
                Resource.Error(errCode = networkResource.errCode,
                    errMsg = networkResource.errMsg)
            }
            is NetworkResource.Empty -> {
                Resource.Error(errMsg = "empty request body")
            }
        }
    }

}