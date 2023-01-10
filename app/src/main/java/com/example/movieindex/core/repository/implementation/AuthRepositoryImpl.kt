package com.example.movieindex.core.repository.implementation

import com.example.movieindex.core.data.external.Resource
import com.example.movieindex.core.data.local.abstraction.CacheDataSource
import com.example.movieindex.core.data.remote.NetworkResource
import com.example.movieindex.core.data.remote.abstraction.NetworkDataSource
import com.example.movieindex.core.data.remote.model.auth.body.LoginBody
import com.example.movieindex.core.data.remote.model.auth.response.SessionIdResponse
import com.example.movieindex.core.repository.abstraction.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
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

//    override fun requestToken(): Flow<Resource<RequestTokenResponse>> = flow {
//        emit(Resource.Loading)
//        val networkResource = network.requestToken()
//        emit(networkResourceHandler(networkResource = networkResource, conversion = { it }))
//    }.catch { t -> Timber.e("requestToken - ${t.message}") }
//
//    override fun loginUser(
//        username: String,
//        password: String,
//        requestToken: String,
//    ): Flow<Resource<LoginResponse>> = flow {
//        emit(Resource.Loading)
//        val loginBody = LoginBody(username = username,
//            password = password,
//            request_token = requestToken)
//        val networkResource = network.login(loginBody = loginBody)
//        emit(networkResourceHandler(networkResource = networkResource, conversion = {it}))
//    }.catch { t -> Timber.e("loginUser - ${t.message}") }
//
//    override fun createSession(requestToken: String): Flow<Resource<SessionIdResponse>> = flow {
//        emit(Resource.Loading)
//        val networkResource = network.createSession(requestToken = requestToken)
//        emit(networkResourceHandler(networkResource = networkResource, conversion = {it}))
//    }.catch { t -> Timber.e("createSession - ${t.message}") }

    override fun login(username: String, password: String): Flow<Resource<SessionIdResponse>> =
        flow {
            emit(Resource.Loading)
            when (val requestToken = network.requestToken()) {
                is NetworkResource.Success -> {
                    Timber.i("login request - token request success - requestToken: ${requestToken.data.request_token}")
                    val uploadData = LoginBody(
                        username = username,
                        password = password,
                        request_token = requestToken.data.request_token
                    )
                    val loginRequest = network.login(
                        uploadData
                    )
                    Timber.i("login request - login requesting... - username: $username, password: ${password}, ${requestToken.data.request_token}")
                    when (loginRequest) {
                        is NetworkResource.Success -> {
                            Timber.i("login request - login request success - username: $username, password: ${password}, ${requestToken.data.request_token}")
                            val sessionIdRequest =
                                network.createSession(requestToken = requestToken.data.request_token)
                            Timber.i("login request - sessionId requesting... - requestToken: ${requestToken.data.request_token}")
                            when (sessionIdRequest) {
                                is NetworkResource.Success -> {
                                    Timber.i("login request - sessionId request success - sessionId ${sessionIdRequest.data.session_id}")
                                    val accountDetailsRequest =
                                        network.getAccountDetails(sessionId = sessionIdRequest.data.session_id)
                                    Timber.i("login request - accountDetails requesting... - sessionId ${sessionIdRequest.data.session_id}")
                                    when (accountDetailsRequest) {
                                        is NetworkResource.Success -> {
                                            Timber.i("login request - accountDetails request success - accountId ${accountDetailsRequest.data.id}")
                                            cache.saveAccountId(accountId = accountDetailsRequest.data.id)
                                            emit(Resource.Success(sessionIdRequest.data))
                                            saveSessionId(sessionId = sessionIdRequest.data.session_id)
                                        }
                                        is NetworkResource.Error -> {
                                            Timber.i("login request - accountDetails request error - errMsg ${accountDetailsRequest.errMessage}")
                                            emit(Resource.Error(errMsg = accountDetailsRequest.errMessage,
                                                errCode = accountDetailsRequest.errCode))
                                        }
                                        is NetworkResource.Empty -> {
                                            Timber.i("login request - accountDetails request empty")
                                            emit(Resource.Empty)
                                        }
                                    }
                                }
                                is NetworkResource.Error -> {
                                    Timber.i("login request - sessionId request error - errMsg ${sessionIdRequest.errMessage}")
                                    emit(Resource.Error(errMsg = sessionIdRequest.errMessage,
                                        errCode = sessionIdRequest.errCode))
                                }
                                is NetworkResource.Empty -> {
                                    Timber.i("login request - sessionId request empty")
                                    emit(Resource.Empty)
                                }
                            }
                        }
                        is NetworkResource.Error -> {
                            Timber.i("login request - login request error - errMsg ${loginRequest.errMessage}")
                            emit(Resource.Error(errMsg = loginRequest.errMessage,
                                errCode = loginRequest.errCode))
                        }
                        is NetworkResource.Empty -> {
                            Timber.i("login request - login request empty")
                            emit(Resource.Empty)
                        }
                    }
                }
                is NetworkResource.Error -> {
                    Timber.i("login request - token request error - errMsg ${requestToken.errMessage}")
                    emit(Resource.Error(errMsg = requestToken.errMessage,
                        errCode = requestToken.errCode))
                }
                is NetworkResource.Empty -> {
                    Timber.i("login request - token request empty")
                    emit(Resource.Empty)
                }
            }
        }
}