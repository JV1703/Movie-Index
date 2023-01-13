package com.example.movieindex.fake.use_case

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.movieindex.core.data.external.model.AccountDetails
import com.example.movieindex.core.data.external.model.Resource
import com.example.movieindex.core.data.remote.NetworkResource
import com.example.movieindex.core.data.remote.model.account.toAccountDetails
import com.example.movieindex.core.data.remote.safeNetworkCall
import com.example.movieindex.feature.common.domain.abstraction.AccountUseCase
import com.example.movieindex.util.TestDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestDispatcher
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class FakeAccountUseCase(
    private val testDispatcher: TestDispatcher,
    private val dataStore: DataStore<Preferences>,
    private val testDataFactory: TestDataFactory,
) : AccountUseCase {

    var isSuccess = true
    var isBodyEmpty = false
    private val accountDetailsList =
        arrayListOf(testDataFactory.generateAccountDetailsResponseTestData().toAccountDetails())

    override fun getAccountDetails(sessionId: String): Flow<Resource<AccountDetails?>> = flow {
        val networkData = testDataFactory.generateAccountDetailsResponseTestData()
        val response = testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(networkData) }
        val networkResource = safeNetworkCall(dispatcher = testDispatcher,
            networkCall = { response },
            conversion = { it })

        val cachedAccountDetails = if (accountDetailsList.isEmpty()) {
            null
        } else {
            accountDetailsList.first()
        }

        emit(Resource.Success(data = cachedAccountDetails))

        when (networkResource) {
            is NetworkResource.Success -> {
                val networkAccountDetails = networkResource.data
                val convertedNetworkAccountDetails = networkAccountDetails.toAccountDetails()

                if (convertedNetworkAccountDetails != cachedAccountDetails) {
                    accountDetailsList.add(convertedNetworkAccountDetails)
                    emit(Resource.Success(convertedNetworkAccountDetails))
                }
            }

            is NetworkResource.Error -> {
                emit(Resource.Error(errCode = networkResource.errCode,
                    errMsg = networkResource.errMsg))
            }
            is NetworkResource.Empty -> {
                emit(Resource.Empty)
            }
        }
    }

    override fun getAccountId(): Flow<Int?> {
        val data = if (accountDetailsList.isEmpty()) null else accountDetailsList.first().id
        return flow { emit(data) }
    }
}