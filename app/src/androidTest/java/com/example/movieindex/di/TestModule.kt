package com.example.movieindex.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.navigation.testing.TestNavHostController
import androidx.room.Room
import com.example.movieindex.core.data.local.MovieDatabase
import com.example.movieindex.core.data.remote.NetworkConstants.BASE_URL
import com.example.movieindex.core.di.ProductionModule
import com.example.movieindex.util.TestDataFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import org.mockito.Mock
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@OptIn(ExperimentalCoroutinesApi::class)
@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [ProductionModule::class])
object TestModule {

    private const val ANDROID_TEST_DATASTORE_NAME = "androidTestDataStore"

    @Provides
    fun provideInMemoryTodoDb(@ApplicationContext context: Context): MovieDatabase {
        return Room.inMemoryDatabaseBuilder(context, MovieDatabase::class.java)
            .allowMainThreadQueries().build()
    }

    @Provides
    fun provideRetrofit(
        moshiConverterFactory: MoshiConverterFactory,
        okHttpClient: OkHttpClient,
    ): Retrofit = Retrofit.Builder().addConverterFactory(moshiConverterFactory).client(okHttpClient)
        .baseUrl("https://1bd12f56-663f-448f-a4ae-c4d7628300a7.mock.pstmn.io").build()
//        .baseUrl(BASE_URL).build()

    @Provides
    fun provideTestScope(): TestScope = TestScope()

    @Provides
    fun provideDataStorePreferences(
        @ApplicationContext context: Context,
        scope: TestScope,
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory
            .create(
                scope = scope,
                produceFile = {
                    // creating a new file for every test case and finally
                    // deleting them all
                    context.preferencesDataStoreFile(ANDROID_TEST_DATASTORE_NAME)
                }
            )
    }

    @Provides
    fun provideTestNavHostController(@ApplicationContext context: Context): TestNavHostController =
        TestNavHostController(context = context)

//    @Provides
//    fun provideMockWebServer(): MockWebServer = MockWebServer()

    @Provides
    fun provideTestDataFactory(): TestDataFactory = TestDataFactory()

}