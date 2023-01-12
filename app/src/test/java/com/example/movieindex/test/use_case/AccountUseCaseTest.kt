package com.example.movieindex.test.use_case

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.test.core.app.ApplicationProvider
import com.example.movieindex.core.data.external.model.AccountDetails
import com.example.movieindex.core.data.external.model.Resource
import com.example.movieindex.core.data.remote.model.account.toAccountDetails
import com.example.movieindex.fake.datastore.FakePreferenceDataStore
import com.example.movieindex.fake.repository.FakeAccountRepository
import com.example.movieindex.feature.common.domain.abstraction.AccountUseCase
import com.example.movieindex.feature.common.domain.implementation.AccountUseCaseImpl
import com.example.movieindex.util.MainCoroutineRule
import com.example.movieindex.util.TestDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class AccountUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainCoroutineRule()

    private lateinit var testDataFactory: TestDataFactory
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var testScope: TestScope
    private lateinit var accountRepository: FakeAccountRepository

    private lateinit var useCase: AccountUseCase

    @Before
    fun setup() {
        testScope = TestScope()
        testDataFactory = TestDataFactory()
        dataStore =
            FakePreferenceDataStore(testContext = ApplicationProvider.getApplicationContext(),
                testCoroutineScope = testScope).testDataStore
        accountRepository =
            FakeAccountRepository(testDispatcher = mainDispatcherRule.testDispatcher,
                dataStore = dataStore, testDataFactory = testDataFactory)

        useCase = AccountUseCaseImpl(accountRepository = accountRepository)
    }

    @Test
    fun getAccountId() = runTest {
        val data = testDataFactory.generateAccountDetailsResponseTestData().toAccountDetails()
        accountRepository.insertAccountDetails(accountDetails = data)

        val cachedData = useCase.getAccountId().first()
        assertEquals(data.id, cachedData)
    }

    @Test
    fun getAccountDetails_success() = runTest{
        val accountDetailsData = AccountDetails(avatarPath = "",
            id = 1,
            includeAdult = Random.nextBoolean(),
            iso6391 = "",
            iso31661 = "",
            name = "Banana",
            username = "Banana")
        accountRepository.insertAccountDetails(accountDetails = accountDetailsData)
        assertEquals(accountDetailsData.id, useCase.getAccountId().first())

        val apiCall = useCase.getAccountDetails(sessionId = "").toList()
        val lastExpectedResult = testDataFactory.generateAccountDetailsResponseTestData().toAccountDetails()

        assertEquals(accountDetailsData, (apiCall[0] as Resource.Success).data)
        assertEquals(lastExpectedResult, (apiCall[1] as Resource.Success).data)
    }

    @Test
    fun getAccountDetails_empty() = runTest{

        accountRepository.isBodyEmpty = true

        val accountDetailsData = AccountDetails(avatarPath = "",
            id = 1,
            includeAdult = Random.nextBoolean(),
            iso6391 = "",
            iso31661 = "",
            name = "Banana",
            username = "Banana")
        accountRepository.insertAccountDetails(accountDetails = accountDetailsData)
        assertEquals(accountDetailsData.id, useCase.getAccountId().first())

        val apiCall = useCase.getAccountDetails(sessionId = "").toList()

        assertEquals(accountDetailsData, (apiCall[0] as Resource.Success).data)
        Assert.assertTrue(apiCall[1] is Resource.Empty)
    }

    @Test
    fun getAccountDetails_error() = runTest{

        accountRepository.isSuccess = false

        val accountDetailsData = AccountDetails(avatarPath = "",
            id = 1,
            includeAdult = Random.nextBoolean(),
            iso6391 = "",
            iso31661 = "",
            name = "Banana",
            username = "Banana")
        accountRepository.insertAccountDetails(accountDetails = accountDetailsData)
        assertEquals(accountDetailsData.id, useCase.getAccountId().first())

        val apiCall = useCase.getAccountDetails(sessionId = "").toList()

        assertEquals(accountDetailsData, (apiCall[0] as Resource.Success).data)
        Assert.assertTrue(apiCall[1] is Resource.Error)
    }

}