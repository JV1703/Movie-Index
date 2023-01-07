package com.example.movieindex.test.view_model

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.test.core.app.ApplicationProvider
import com.example.movieindex.core.data.external.Resource
import com.example.movieindex.fake.datastore.FakePreferenceDataStore
import com.example.movieindex.fake.use_case.FakeAuthUseCase
import com.example.movieindex.feature.auth.AuthViewModel
import com.example.movieindex.util.MainCoroutineRule
import com.example.movieindex.util.TestDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainCoroutineRule()

    private lateinit var testDataFactory: TestDataFactory
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var testScope: TestScope
    private lateinit var authUseCase: FakeAuthUseCase

    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        testScope = TestScope()
        testDataFactory = TestDataFactory()
        dataStore =
            FakePreferenceDataStore(testContext = ApplicationProvider.getApplicationContext(),
                testCoroutineScope = testScope).testDataStore
        authUseCase =
            FakeAuthUseCase(mainDispatcherRule.testDispatcher, dataStore, testDataFactory)
        viewModel = AuthViewModel(authUseCase = authUseCase)
    }

    @Test
    fun validateUserLogin_fieldsEmpty() = runTest {

        val eventList = arrayListOf<AuthViewModel.AuthEvents>()

        val job = launch(mainDispatcherRule.testDispatcher) {
            viewModel.loginEvents.toList(eventList)
        }

        viewModel.validateUserLogin(username = "", password = "")

        assertTrue(eventList.last() is AuthViewModel.AuthEvents.InvalidInput)
        assertEquals(AuthViewModel.AuthEvents.InvalidInput(errMsg = "empty email & password field"),
            eventList.last())

        job.cancel()
    }

    @Test
    fun validateUserLogin_emailFieldEmpty() = runTest {

        val eventList = arrayListOf<AuthViewModel.AuthEvents>()

        val job = launch(mainDispatcherRule.testDispatcher) {
            viewModel.loginEvents.toList(eventList)
        }

        viewModel.validateUserLogin(username = "", password = "Banana")

        assertTrue(eventList.last() is AuthViewModel.AuthEvents.InvalidInput)
        assertEquals(AuthViewModel.AuthEvents.InvalidInput(errMsg = "empty username field"),
            eventList.last())

        job.cancel()

    }

    @Test
    fun validateUserLogin_passwordFieldEmpty() = runTest {

        val eventList = arrayListOf<AuthViewModel.AuthEvents>()

        val job = launch(mainDispatcherRule.testDispatcher) {
            viewModel.loginEvents.toList(eventList)
        }

        viewModel.validateUserLogin(username = "Banana", password = "")

        assertTrue(eventList.last() is AuthViewModel.AuthEvents.InvalidInput)
        assertEquals(AuthViewModel.AuthEvents.InvalidInput(errMsg = "empty password field"),
            eventList.last())

        job.cancel()

    }

    @Test
    fun validateUserLogin_success() = runTest {

        val eventList = arrayListOf<AuthViewModel.AuthEvents>()
        val stateList = arrayListOf<AuthViewModel.AuthUiState>()

        val eventJob = launch(mainDispatcherRule.testDispatcher) {
            viewModel.loginEvents.toList(eventList)
        }

        val stateJob = launch(mainDispatcherRule.testDispatcher) {
            viewModel.authUiState.toList(stateList)
        }

        viewModel.validateUserLogin(username = "Banana", password = "Banana")

        assertTrue(stateList[1] is AuthViewModel.AuthUiState.IsNotLoggedIn)
        assertTrue((stateList[1] as AuthViewModel.AuthUiState.IsNotLoggedIn).isLoading)
        assertTrue(stateList[2] is AuthViewModel.AuthUiState.IsNotLoggedIn)
        assertFalse((stateList[2] as AuthViewModel.AuthUiState.IsNotLoggedIn).isLoading)
        assertTrue(eventList.last() is AuthViewModel.AuthEvents.Success)

        eventJob.cancel()
        stateJob.cancel()

    }

    @Test
    fun validateUserLogin_error() = runTest {

        authUseCase.isSuccess = false

        val eventList = arrayListOf<AuthViewModel.AuthEvents>()
        val stateList = arrayListOf<AuthViewModel.AuthUiState>()

        val eventJob = launch(mainDispatcherRule.testDispatcher) {
            viewModel.loginEvents.toList(eventList)
        }

        val stateJob = launch(mainDispatcherRule.testDispatcher) {
            viewModel.authUiState.toList(stateList)
        }

        viewModel.validateUserLogin(username = "Banana", password = "Banana")

        assertTrue(stateList[1] is AuthViewModel.AuthUiState.IsNotLoggedIn)
        assertTrue((stateList[1] as AuthViewModel.AuthUiState.IsNotLoggedIn).isLoading)
        assertTrue(stateList[2] is AuthViewModel.AuthUiState.IsNotLoggedIn)
        assertFalse((stateList[2] as AuthViewModel.AuthUiState.IsNotLoggedIn).isLoading)
        assertTrue(eventList.last() is AuthViewModel.AuthEvents.AuthError)

        eventJob.cancel()
        stateJob.cancel()

    }

    @Test
    fun validateUserLogin_error_emptyResponse() = runTest {

        authUseCase.isBodyEmpty = true

        val eventList = arrayListOf<AuthViewModel.AuthEvents>()
        val stateList = arrayListOf<AuthViewModel.AuthUiState>()

        val eventJob = launch(mainDispatcherRule.testDispatcher) {
            viewModel.loginEvents.toList(eventList)
        }

        val stateJob = launch(mainDispatcherRule.testDispatcher) {
            viewModel.authUiState.toList(stateList)
        }

        viewModel.validateUserLogin(username = "Banana", password = "Banana")

        assertTrue(stateList[1] is AuthViewModel.AuthUiState.IsNotLoggedIn)
        assertTrue((stateList[1] as AuthViewModel.AuthUiState.IsNotLoggedIn).isLoading)
        assertTrue(stateList[2] is AuthViewModel.AuthUiState.IsNotLoggedIn)
        assertFalse((stateList[2] as AuthViewModel.AuthUiState.IsNotLoggedIn).isLoading)
        assertTrue(eventList.last() is AuthViewModel.AuthEvents.AuthError)

        eventJob.cancel()
        stateJob.cancel()

    }

    @Test
    fun isUserLoggedIn_loggedIn() = runTest {

        val stateList = arrayListOf<AuthViewModel.AuthUiState>()

        val job = launch(mainDispatcherRule.testDispatcher) {
            viewModel.authUiState.toList(stateList)
        }

        val loginResponseList = authUseCase.login(username = "", password = "").toList()
        assertTrue(loginResponseList.last() is Resource.Success)

        val isUserLoggedIn = authUseCase.isUserLoggedIn().first()
        assertTrue(isUserLoggedIn)

        assertTrue(stateList.last() is AuthViewModel.AuthUiState.IsLoggedIn)

        job.cancel()

    }


}