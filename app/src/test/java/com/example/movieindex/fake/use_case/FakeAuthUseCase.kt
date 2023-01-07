package com.example.movieindex.fake.use_case

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.example.movieindex.core.data.external.Resource
import com.example.movieindex.core.data.local.CacheConstants
import com.example.movieindex.core.data.remote.NetworkResource
import com.example.movieindex.core.data.remote.model.auth.response.SessionIdResponse
import com.example.movieindex.core.data.remote.safeNetworkCall
import com.example.movieindex.core.repository.implementation.networkResourceHandler
import com.example.movieindex.feature.auth.domain.abstraction.AuthUseCase
import com.example.movieindex.util.TestDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class FakeAuthUseCase(
    private val testDispatcher: TestDispatcher,
    private val dataStore: DataStore<Preferences>,
    private val testDataFactory: TestDataFactory,
): AuthUseCase {

    var isSuccess = true
    var isBodyEmpty = false

    override fun login(username: String, password: String): Flow<Resource<SessionIdResponse>> {
        val data = testDataFactory.generateSessionIdTestData()
        val response = testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(data) }
        return flow {
            emit(Resource.Loading)
            val networkResource =
                safeNetworkCall(testDispatcher, networkCall = { response }, conversion = { it })
            if(networkResource is NetworkResource.Success){
                saveSessionId(sessionId = networkResource.data.session_id)
            }
            emit(networkResourceHandler(networkResource = networkResource, conversion = { it }))
        }
    }

    override fun isUserLoggedIn(): Flow<Boolean> {
        return getSessionId().map { it.isNotEmpty() }
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