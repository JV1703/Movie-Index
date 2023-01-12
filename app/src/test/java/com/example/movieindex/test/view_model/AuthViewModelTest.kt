package com.example.movieindex.test.view_model

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.test.core.app.ApplicationProvider
import com.example.movieindex.fake.datastore.FakePreferenceDataStore
import com.example.movieindex.fake.use_case.FakeAuthUseCase
import com.example.movieindex.feature.auth.AuthViewModel
import com.example.movieindex.util.MainCoroutineRule
import com.example.movieindex.util.TestDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
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
        val uiStateList = arrayListOf<AuthViewModel.UiState>()

        val job = launch(mainDispatcherRule.testDispatcher) {
            viewModel.uiState.toList(uiStateList)
        }

        viewModel.validateUserLogin(username = "", password = "")
        advanceUntilIdle()

        assertEquals(2,uiStateList.size)
        assertNotNull(uiStateList.last().userMsg)
        assertFalse(uiStateList.last().isLoggedIn)

        job.cancel()
    }

    @Test
    fun validateUserLogin_usernameFieldEmpty() = runTest {
        val uiStateList = arrayListOf<AuthViewModel.UiState>()

        val job = launch(mainDispatcherRule.testDispatcher) {
            viewModel.uiState.toList(uiStateList)
        }

        viewModel.validateUserLogin(username = "", password = "Banana")
        advanceUntilIdle()

        assertEquals(2,uiStateList.size)
        assertNotNull(uiStateList.last().userMsg)
        assertFalse(uiStateList.last().isLoggedIn)

        job.cancel()
    }

    @Test
    fun validateUserLogin_passwordFieldEmpty() = runTest {
        val uiStateList = arrayListOf<AuthViewModel.UiState>()

        val job = launch(mainDispatcherRule.testDispatcher) {
            viewModel.uiState.toList(uiStateList)
        }

        viewModel.validateUserLogin(username = "Banana", password = "")
        advanceUntilIdle()

        assertEquals(2,uiStateList.size)
        assertNotNull(uiStateList.last().userMsg)
        assertFalse(uiStateList.last().isLoggedIn)

        job.cancel()
    }

    @Test
    fun validateUserLogin_success() = runTest {
        val uiStateList = arrayListOf<AuthViewModel.UiState>()

        val job = launch(mainDispatcherRule.testDispatcher) {
            viewModel.uiState.toList(uiStateList)
        }

        viewModel.validateUserLogin(username = "Banana", password = "Banana")
        advanceUntilIdle()

        assertTrue(uiStateList[1].isLoading)
        assertTrue(uiStateList.last().isLoggedIn)
        assertFalse(uiStateList.last().isLoading)

        job.cancel()
    }

    @Test
    fun validateUserLogin_empty() = runTest {
        authUseCase.isBodyEmpty = true
        val uiStateList = arrayListOf<AuthViewModel.UiState>()

        val job = launch(mainDispatcherRule.testDispatcher) {
            viewModel.uiState.toList(uiStateList)
        }

        viewModel.validateUserLogin(username = "Banana", password = "Banana")
        advanceUntilIdle()

        assertFalse(uiStateList.last().isLoggedIn)
        assertNotNull(uiStateList.last().userMsg)

        job.cancel()
    }

}