package com.example.movieindex.fake.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.example.movieindex.core.data.external.model.AccountDetails
import com.example.movieindex.core.data.external.model.Resource
import com.example.movieindex.core.data.external.model.networkResourceHandler
import com.example.movieindex.core.data.local.CacheConstants
import com.example.movieindex.core.data.remote.NetworkResource
import com.example.movieindex.core.data.remote.model.auth.response.DeleteSessionResponse
import com.example.movieindex.core.data.remote.model.auth.response.SessionIdResponse
import com.example.movieindex.core.data.remote.safeNetworkCall
import com.example.movieindex.core.repository.abstraction.AuthRepository
import com.example.movieindex.util.TestDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class FakeAuthRepository(
    private val testDispatcher: TestDispatcher,
    private val dataStore: DataStore<Preferences>,
    private val testDataFactory: TestDataFactory,
) : AuthRepository {

    var isSuccess = true
    var isBodyEmpty = false
    private val accountDetailsList = arrayListOf<AccountDetails>()

    override suspend fun login(
        username: String,
        password: String,
    ): Resource<SessionIdResponse> {
        val data = testDataFactory.generateSessionIdTestData()
        val response = testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(data) }
        val networkResource = safeNetworkCall(dispatcher = testDispatcher,
            networkCall = { response },
            conversion = { it })
        return networkResourceHandler(networkResource = networkResource, conversion = { it })
    }

    override suspend fun deleteSession(): Resource<DeleteSessionResponse> {
        val data = testDataFactory.generateDeleteSessionResponseTestData()
        val response = testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(data) }
        val networkResource = safeNetworkCall(dispatcher = testDispatcher,
            networkCall = { response },
            conversion = { it })

        return when (networkResource) {
            is NetworkResource.Success -> {
                if (networkResource.data.success) {
                    withContext(testDispatcher) {
                        accountDetailsList.clear()
                        dataStore.edit { preferences ->
                            preferences.clear()
                        }
                    }
                }
                Resource.Success(networkResource.data)
            }
            is NetworkResource.Error -> {
                Resource.Error(errCode = networkResource.errCode, errMsg = networkResource.errMsg)
            }
            is NetworkResource.Empty -> {
                Resource.Empty
            }
        }
    }

    override suspend fun saveSessionId(sessionId: String) {
        withContext(testDispatcher) {
            dataStore.edit { preferences ->
                preferences[CacheConstants.SESSION_ID] = sessionId
            }
        }
    }

    override fun getSessionId(): Flow<String> {
        return dataStore.data.catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            preferences[CacheConstants.SESSION_ID] ?: ""
        }.flowOn(testDispatcher)
    }

    override suspend fun clearDataStore() {
        withContext(testDispatcher) {
            dataStore.edit {
                it.clear()
            }
        }
    }
}