package com.example.movieindex.test.use_case

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.test.core.app.ApplicationProvider
import com.example.movieindex.core.data.external.model.Resource
import com.example.movieindex.fake.datastore.FakePreferenceDataStore
import com.example.movieindex.fake.repository.FakeAuthRepository
import com.example.movieindex.feature.auth.domain.abstraction.AuthUseCase
import com.example.movieindex.feature.auth.domain.implementation.AuthUseCaseImpl
import com.example.movieindex.util.MainCoroutineRule
import com.example.movieindex.util.TestDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class AuthUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainCoroutineRule()

    private lateinit var testDataFactory: TestDataFactory
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var testScope: TestScope
    private lateinit var authRepository: FakeAuthRepository

    private lateinit var authUseCase: AuthUseCase

    @Before
    fun setup() {
        testScope = TestScope()
        testDataFactory = TestDataFactory()
        dataStore =
            FakePreferenceDataStore(testContext = ApplicationProvider.getApplicationContext(),
                testCoroutineScope = testScope).testDataStore
        authRepository = FakeAuthRepository(testDispatcher = mainDispatcherRule.testDispatcher,
            dataStore = dataStore, testDataFactory = testDataFactory)

        authUseCase = AuthUseCaseImpl(repository = authRepository)
    }

    @Test
    fun login_success() = runTest {
        val apiCall = authUseCase.login(username = "", password = "")
        assertTrue(apiCall is Resource.Success)
    }

    @Test
    fun login_empty() = runTest {
        authRepository.isBodyEmpty = true
        val apiCall = authUseCase.login(username = "", password = "")
        assertTrue(apiCall is Resource.Empty)
    }

    @Test
    fun login_error() = runTest {
        authRepository.isSuccess = false
        val apiCall = authUseCase.login(username = "", password = "")
        assertTrue(apiCall is Resource.Error)
    }

    @Test
    fun isUserLoggedIn() = runTest {
        val sessionId = UUID.randomUUID().toString()
        authRepository.saveSessionId(sessionId = sessionId)
        assertTrue(authUseCase.isUserLoggedIn().first())
    }

    @Test
    fun getSessionId() = runTest {
        val sessionId = UUID.randomUUID().toString()
        authRepository.saveSessionId(sessionId = sessionId)
        val cachedData = authRepository.getSessionId().first()
        assertEquals(sessionId, cachedData)
    }


}