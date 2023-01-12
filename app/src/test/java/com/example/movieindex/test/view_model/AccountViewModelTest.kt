package com.example.movieindex.test.view_model

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.test.core.app.ApplicationProvider
import com.example.movieindex.fake.datastore.FakePreferenceDataStore
import com.example.movieindex.fake.use_case.FakeAccountUseCase
import com.example.movieindex.fake.use_case.FakeAuthUseCase
import com.example.movieindex.fake.use_case.FakeCreditListUseCase
import com.example.movieindex.fake.use_case.FakeMovieDetailsUseCase
import com.example.movieindex.feature.main.ui.account.AccountViewModel
import com.example.movieindex.util.MainCoroutineRule
import com.example.movieindex.util.TestDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class AccountViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainCoroutineRule()

    private lateinit var testDataFactory: TestDataFactory
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var testScope: TestScope
    private lateinit var authUseCase: FakeAuthUseCase
    private lateinit var accountUseCase: FakeAccountUseCase

    private lateinit var viewModel: AccountViewModel

    @Before
    fun setup() {
        testScope = TestScope()
        testDataFactory = TestDataFactory()
        dataStore =
            FakePreferenceDataStore(testContext = ApplicationProvider.getApplicationContext(),
                testCoroutineScope = testScope).testDataStore
        FakeMovieDetailsUseCase(mainDispatcherRule.testDispatcher, dataStore, testDataFactory)
        authUseCase = FakeAuthUseCase(mainDispatcherRule.testDispatcher, dataStore, testDataFactory)
        accountUseCase =
            FakeAccountUseCase(mainDispatcherRule.testDispatcher, dataStore, testDataFactory)
        FakeCreditListUseCase(mainDispatcherRule.testDispatcher, dataStore, testDataFactory)
        viewModel =
            AccountViewModel(
                authUseCase = authUseCase,
                accountUseCase = accountUseCase)

    }

    @Test
    fun uiState_loggedIn() = runTest {
        authUseCase.login(username = "", password = "")
        val uiStateList = arrayListOf<AccountViewModel.AccountUiState>()
        val job = launch(mainDispatcherRule.testDispatcher) {
            viewModel.uiState.toList(uiStateList)
        }

        assertTrue(uiStateList.first().isLoggedIn)
        assertNotNull(uiStateList.first().accountDetails)

        job.cancel()
    }

    // can't test
    // https://github.com/googlecodelabs/android-datastore/issues/48
    fun uiState_logOut() = runTest {
        authUseCase.login(username = "", password = "")
        val uiStateList = arrayListOf<AccountViewModel.AccountUiState>()
        val job = launch(mainDispatcherRule.testDispatcher) {
            viewModel.uiState.toList(uiStateList)
        }

        assertTrue(uiStateList.first().isLoggedIn)
        assertNotNull(uiStateList.first().accountDetails)
        assertTrue(uiStateList.first().isLoggedIn)

        viewModel.logout()
        assertTrue(uiStateList[1].isLoading)
        assertFalse(uiStateList.last().isLoading)
        assertFalse(uiStateList.last().isLoggedIn)
        assertNull(uiStateList.last().accountDetails)
        assertEquals("", uiStateList)

        job.cancel()
    }

}