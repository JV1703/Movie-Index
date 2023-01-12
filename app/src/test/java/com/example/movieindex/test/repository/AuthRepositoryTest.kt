package com.example.movieindex.test.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.test.core.app.ApplicationProvider
import com.example.movieindex.core.data.external.model.Resource
import com.example.movieindex.core.data.local.abstraction.CacheDataSource
import com.example.movieindex.core.data.remote.NetworkResource
import com.example.movieindex.core.repository.abstraction.AuthRepository
import com.example.movieindex.core.repository.implementation.AuthRepositoryImpl
import com.example.movieindex.fake.data_source.FakeCacheDataSource
import com.example.movieindex.fake.data_source.FakeNetworkDataSource
import com.example.movieindex.fake.datastore.FakePreferenceDataStore
import com.example.movieindex.util.MainCoroutineRule
import com.example.movieindex.util.TestDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestDispatcher
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
class AuthRepositoryTest {

    @get:Rule
    val mainDispatcherRule = MainCoroutineRule()

    private lateinit var network: FakeNetworkDataSource
    private lateinit var cache: CacheDataSource
    private lateinit var testDataFactory: TestDataFactory

    private lateinit var dataStore: DataStore<Preferences>

    private lateinit var repository: AuthRepository

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var testScope: TestScope

    @Before
    fun setup() {
        testDispatcher = mainDispatcherRule.testDispatcher
        testScope = TestScope(testDispatcher)
        testDataFactory = TestDataFactory()
        dataStore =
            FakePreferenceDataStore(testContext = ApplicationProvider.getApplicationContext(),
                testCoroutineScope = testScope).testDataStore
        network = FakeNetworkDataSource(testDataFactory, testDispatcher)
        cache = FakeCacheDataSource(testDispatcher = testDispatcher, dataStore = dataStore)

        repository = AuthRepositoryImpl(network = network, cache = cache)
    }

    @Test
    fun saveSessionId_getSessionId_cache() = runTest {
        val sessionId = UUID.randomUUID().toString()
        repository.saveSessionId(sessionId = sessionId)

        val cachedData = repository.getSessionId().first()

        assertEquals(sessionId, cachedData)
    }

    @Test
    fun login_success() = runTest {
        val apiCall = repository.login(username = "Banana", password = "Banana")
        val cachedAccountDetails = cache.getAccountDetails().first()
        val cachedSessionId = cache.getSessionId().first()

        assertTrue(apiCall is Resource.Success)
        assertNotNull(cachedAccountDetails)
        assertTrue(cachedSessionId.isNotEmpty())
    }

    @Test
    fun login_empty() = runTest {
        network.isBodyEmpty = true
        val apiCall = repository.login(username = "Banana", password = "Banana")

        assertTrue(apiCall is Resource.Empty)
    }

    @Test
    fun login_error() = runTest {
        network.isSuccess = false
        val apiCall = repository.login(username = "Banana", password = "Banana")

        assertTrue(apiCall is Resource.Error)
    }

    // Manual test, crash when trying to clear fakeDataStore
//    @Test
//    fun deleteSession() = runTest {
//        val login = repository.login(username = "Banana", password = "Banana")
//        val cachedAccountDetails = cache.getAccountDetails().first()
//        val cachedSessionId = cache.getSessionId().first()
//
//        assertTrue(login is Resource.Success)
//        assertNotNull(cachedAccountDetails)
//        assertTrue(cachedSessionId.isNotEmpty())
//
//        val logout = repository.deleteSession()
//        val updatedCachedAccountDetails = cache.getAccountDetails().first()
//        val updatedCachedSessionId = cache.getSessionId().first()
//
//        assertTrue(logout is Resource.Success)
//        assertNull(updatedCachedAccountDetails)
//        assertTrue(updatedCachedSessionId.isEmpty())
//    }

}