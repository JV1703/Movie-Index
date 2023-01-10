package com.example.movieindex.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.movieindex.core.data.local.model.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccountDetails(account: AccountEntity)

    @Query("SELECT * FROM account_table")
    fun getAccountDetails(): Flow<AccountEntity?>

    @Query("DELETE FROM account_table")
    suspend fun deleteAccountDetails()

}