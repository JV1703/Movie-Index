package com.example.movieindex.test.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.test.core.app.ApplicationProvider
import com.example.movieindex.core.data.external.Resource
import com.example.movieindex.core.data.local.abstraction.CacheDataSource
import com.example.movieindex.core.repository.abstraction.AuthRepository
import com.example.movieindex.core.repository.implementation.AuthRepositoryImpl
import com.example.movieindex.fake.data_source.FakeCacheDataSource
import com.example.movieindex.fake.data_source.FakeNetworkDataSource
import com.example.movieindex.fake.datastore.FakePreferenceDataStore
import com.example.movieindex.util.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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

    private lateinit var dataStore: DataStore<Preferences>

    private lateinit var repository: AuthRepository

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var testScope: TestScope

    @Before
    fun setup() {
        testDispatcher = mainDispatcherRule.testDispatcher
        testScope = TestScope(testDispatcher)
        dataStore = FakePreferenceDataStore(testContext = ApplicationProvider.getApplicationContext(),
            testCoroutineScope = testScope).testDataStore
        network = FakeNetworkDataSource(testDispatcher)
        cache = FakeCacheDataSource(testDispatcher = testDispatcher, dataStore = dataStore)

        repository = AuthRepositoryImpl(network = network, cache = cache)
    }

    @Test
    fun saveSessionId_getSessionId_cache() = testScope.runTest {
        val sessionId = UUID.randomUUID().toString()
        repository.saveSessionId(sessionId = sessionId)

        val cachedData = repository.getSessionId().first()

        assertEquals(sessionId, cachedData)
    }

    @Test
    fun login_loading() = runTest {
        val actualResult = repository.login(username = "", password = "").first()
        assertTrue(actualResult is Resource.Loading)
    }

    @Test
    fun login_success() = runTest {
        val actualResult = repository.login(username = "", password = "").toList()

        assertEquals(2, actualResult.size)
        assertTrue(actualResult[1] is Resource.Success)
    }

    @Test
    fun login_error() = runTest {
        network.isSuccess = false

        val actualResult = repository.login(username = "", password = "").toList()

        assertEquals(2, actualResult.size)
        assertTrue(actualResult[1] is Resource.Error)
    }

    @Test
    fun login_empty() = runTest {
        network.isBodyEmpty = true

        val actualResult = repository.login(username = "", password = "").toList()

        assertEquals(2, actualResult.size)
        assertTrue(actualResult[1] is Resource.Empty)
    }

}