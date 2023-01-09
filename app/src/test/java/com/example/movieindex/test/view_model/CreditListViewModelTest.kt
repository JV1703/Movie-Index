package com.example.movieindex.test.view_model

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import com.example.movieindex.core.data.remote.model.details.toMovieDetails
import com.example.movieindex.fake.datastore.FakePreferenceDataStore
import com.example.movieindex.fake.use_case.FakeMovieUseCase
import com.example.movieindex.feature.list.credit_list.CreditListViewModel
import com.example.movieindex.util.MainCoroutineRule
import com.example.movieindex.util.TestDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class CreditListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainCoroutineRule()

    private lateinit var testDataFactory: TestDataFactory
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var testScope: TestScope
    private lateinit var movieUseCase: FakeMovieUseCase
    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var viewModel: CreditListViewModel

    @Before
    fun setup() {
        testScope = TestScope()
        testDataFactory = TestDataFactory()
        dataStore =
            FakePreferenceDataStore(testContext = ApplicationProvider.getApplicationContext(),
                testCoroutineScope = testScope).testDataStore
        savedStateHandle = SavedStateHandle()
        movieUseCase =
            FakeMovieUseCase(mainDispatcherRule.testDispatcher, dataStore, testDataFactory)
        viewModel =
            CreditListViewModel(movieUseCase = movieUseCase)
    }


    // unable to test due to
    // This likely means that there are multiple instances of DataStore for this file. Ensure that you are only creating a single instance of datastore for this file.
    // not resolved yet, tracker: https://github.com/googlecodelabs/android-datastore/issues/48
//    @Test
    fun credits_returnTheCorrectDataType() = runTest {
        val castData = testDataFactory.generateMovieDetailsTestData().toMovieDetails().casts
        val crewData = testDataFactory.generateMovieDetailsTestData().toMovieDetails().crews

        movieUseCase.saveCasts(castData)
        movieUseCase.saveCrews(crewData)

        val cachedCastData = movieUseCase.getCasts().first()
        val cachedCrewData = movieUseCase.getCrews().first()

        assertEquals(castData, cachedCastData)
        assertEquals(crewData, cachedCrewData)

    }

}