package com.example.movieindex.fake.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.paging.PagingData
import com.example.movieindex.core.data.external.model.*
import com.example.movieindex.core.data.remote.NetworkResource
import com.example.movieindex.core.data.remote.model.account.toAccountDetails
import com.example.movieindex.core.data.remote.model.account.toMovieAccountState
import com.example.movieindex.core.data.remote.model.common.PostResponse
import com.example.movieindex.core.data.remote.safeNetworkCall
import com.example.movieindex.core.repository.abstraction.AccountRepository
import com.example.movieindex.util.TestDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestDispatcher
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class FakeAccountRepository(
    private val testDispatcher: TestDispatcher,
    private val dataStore: DataStore<Preferences>,
    private val testDataFactory: TestDataFactory,
) : AccountRepository {

    var isSuccess = true
    var isBodyEmpty = false
    private val accountDetailsList = arrayListOf<AccountDetails>()

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

    override suspend fun addToFavorite(
        sessionId: String,
        accountId: Int,
        favorite: Boolean,
        mediaId: Int,
        mediaType: String,
    ): Resource<PostResponse> {
        val data = testDataFactory.generatePostResponseTestData()
        val response = testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(data) }
        val networkResource =
            safeNetworkCall(dispatcher = testDispatcher,
                networkCall = { response },
                conversion = { it })
        return networkResourceHandler(networkResource = networkResource, conversion = { it })
    }

    override suspend fun addToWatchList(
        sessionId: String,
        accountId: Int,
        watchlist: Boolean,
        mediaId: Int,
        mediaType: String,
    ): Resource<PostResponse> {
        val data = testDataFactory.generatePostResponseTestData()
        val response = testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(data) }
        val networkResource =
            safeNetworkCall(dispatcher = testDispatcher,
                networkCall = { response },
                conversion = { it })
        return networkResourceHandler(networkResource = networkResource, conversion = { it })
    }

    override fun getFavoriteListRemoteMediator(
        accountId: Int,
        sessionId: String,
        loadSinglePage: Boolean,
        language: String?,
        sortBy: String?,
    ): Flow<PagingData<Result>> {
        TODO("Not yet implemented")
    }

    override fun getWatchListRemoteMediator(
        accountId: Int,
        sessionId: String,
        loadSinglePage: Boolean,
        language: String?,
        sortBy: String?,
    ): Flow<PagingData<Result>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAccountDetailsCache() {
        accountDetailsList.clear()
    }

    override suspend fun getMovieAccountState(
        movieId: Int,
        sessionId: String,
    ): Resource<MovieAccountState> {
        val data = testDataFactory.generateMovieAccountStateResponseTestData()
        val response = testDataFactory.generateResponse(isSuccess = isSuccess,
            isBodyEmpty = isBodyEmpty) { Response.success(data) }
        val networkResource = safeNetworkCall(dispatcher = testDispatcher,
            networkCall = { response },
            conversion = { it })
        return networkResourceHandler(networkResource = networkResource,
            conversion = { it.toMovieAccountState() })
    }

    override fun getAccountIdCache(): Flow<Int?> = flow {
        emit(if (accountDetailsList.isEmpty()) null else accountDetailsList.first().id)
    }

    fun insertAccountDetails(accountDetails: AccountDetails) {
        accountDetailsList.add(accountDetails)
    }
}