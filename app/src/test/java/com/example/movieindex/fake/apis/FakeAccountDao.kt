package com.example.movieindex.fake.apis

import com.example.movieindex.core.data.local.dao.AccountDao
import com.example.movieindex.core.data.local.model.AccountEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeAccountDao : AccountDao {

    private val accountList = arrayListOf<AccountEntity>()

    override suspend fun insertAccountDetails(account: AccountEntity) {
        accountList.add(account)
    }

    override fun getAccountDetails(): Flow<AccountEntity?> =
        flow { emit(if (accountList.isEmpty()) null else accountList.first()) }

    override fun getAccountId(): Flow<Int?> =
        flow { emit(if (accountList.isEmpty()) null else accountList.first().id) }

    override suspend fun deleteAccountDetails() {
        accountList.clear()
    }
}