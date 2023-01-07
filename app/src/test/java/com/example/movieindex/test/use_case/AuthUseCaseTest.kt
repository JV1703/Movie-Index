package com.example.movieindex.test.use_case

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.test.core.app.ApplicationProvider
import com.example.movieindex.core.data.external.Resource
import com.example.movieindex.fake.datastore.FakePreferenceDataStore
import com.example.movieindex.fake.repository.FakeAuthRepository
import com.example.movieindex.feature.auth.domain.abstraction.AuthUseCase
import com.example.movieindex.feature.auth.domain.implementation.AuthUseCaseImpl
import com.example.movieindex.util.MainCoroutineRule
import com.example.movieindex.util.TestDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
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
    fun login_loading() = runTest {
        val actualResult = authUseCase.login(username = "", password = "").first()
        val sessionId = authUseCase.getSessionId().first()

        assertTrue(actualResult is Resource.Loading)
        assertTrue(sessionId.isEmpty())
    }

    @Test
    fun login_success() = runTest {
        val actualResult = authUseCase.login(username = "", password = "").toList()
        val sessionId = authUseCase.getSessionId().first()

        assertEquals(2, actualResult.size)
        assertTrue(actualResult[1] is Resource.Success)
        assertTrue(sessionId.isNotEmpty())
    }

    @Test
    fun login_error() = runTest {

        authRepository.isSuccess = false

        val actualResult = authUseCase.login(username = "", password = "").toList()
        val sessionId = authUseCase.getSessionId().first()

        assertEquals(2, actualResult.size)
        assertTrue(actualResult[1] is Resource.Error)
        assertTrue(sessionId.isEmpty())
    }

    @Test
    fun login_empty() = runTest {

        authRepository.isBodyEmpty = true

        val actualResult = authUseCase.login(username = "", password = "").toList()
        val sessionId = authUseCase.getSessionId().first()

        assertEquals(2, actualResult.size)
        assertTrue(actualResult[1] is Resource.Empty)
        assertTrue(sessionId.isEmpty())

    }

    @Test
    fun isUserLoggedIn_testUsingLogin() = runTest {

        val actualResult = authUseCase.login(username = "", password = "").toList()
        val sessionId = authUseCase.getSessionId().first()

        assertEquals(2, actualResult.size)
        assertTrue(actualResult[1] is Resource.Success)
        assertTrue(sessionId.isNotEmpty())

        val isUserLoggedIn = authUseCase.isUserLoggedIn().first()
        assertTrue(isUserLoggedIn)

    }

    @Test
    fun isUserLoggedIn_testUsingDataStore() = runTest {
        assertFalse(authUseCase.isUserLoggedIn().first())

        val sessionId = UUID.randomUUID().toString()
        authRepository.saveSessionId(sessionId = sessionId)
        assertEquals(sessionId, authRepository.getSessionId().first())

        val isUserLoggedIn = authUseCase.isUserLoggedIn().first()
        assertTrue(isUserLoggedIn)
    }

    @Test
    fun saveSessionId_getSessionId() = runTest {
        val sessionId = UUID.randomUUID().toString()

        authUseCase.saveSessionId(sessionId = sessionId)
        val cachedData = authUseCase.getSessionId().first()

        assertEquals(sessionId, cachedData)
    }

}