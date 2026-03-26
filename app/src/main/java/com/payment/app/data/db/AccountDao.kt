package com.payment.app.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.payment.app.data.db.entity.BankAccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM bank_accounts ORDER BY accountId ASC")
    fun getAllAccounts(): Flow<List<BankAccountEntity>>

    @Query("SELECT * FROM bank_accounts ORDER BY accountId ASC")
    suspend fun getAllAccountsOnce(): List<BankAccountEntity>

    @Query("SELECT accountId FROM bank_accounts ORDER BY accountId ASC LIMIT 1")
    suspend fun getFirstAccountId(): Long?

    @Insert
    suspend fun insertAccount(account: BankAccountEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(accounts: List<BankAccountEntity>)

    @Delete
    suspend fun deleteAccount(account: BankAccountEntity)

    @Query("SELECT COUNT(*) FROM bank_accounts")
    suspend fun getAccountCount(): Int

    @Query("DELETE FROM bank_accounts")
    suspend fun clearAll()
}
