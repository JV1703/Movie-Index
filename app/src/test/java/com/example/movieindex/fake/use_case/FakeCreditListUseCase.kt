package com.example.movieindex.fake.use_case

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.example.movieindex.core.common.extensions.fromJson
import com.example.movieindex.core.data.external.model.Cast
import com.example.movieindex.core.data.external.model.Crew
import com.example.movieindex.core.data.local.CacheConstants
import com.example.movieindex.feature.list.credit_list.domain.abstraction.CreditListUseCase
import com.example.movieindex.util.TestDataFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.TestDispatcher
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class FakeCreditListUseCase(
    private val testDispatcher: TestDispatcher,
    private val dataStore: DataStore<Preferences>,
    private val testDataFactory: TestDataFactory,
) : CreditListUseCase {

    private val gson = Gson()

    override fun getCasts(): Flow<List<Cast>> {
        return dataStore.data.catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            val data = preferences[CacheConstants.CASTS] ?: ""

            val listType = object : TypeToken<List<Cast>>() {}.type
            gson.fromJson<List<Cast>>(data, listType)
        }.flowOn(testDispatcher)
    }

    override fun getCrews(): Flow<List<Crew>> {
        return dataStore.data.catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            val data = preferences[CacheConstants.CREWS] ?: ""

            val listType = object : TypeToken<List<Crew>>() {}.type
            gson.fromJson<List<Crew>>(data, listType)
        }.flowOn(testDispatcher)
    }
}