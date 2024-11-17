package com.ntg.core.database.dao

import android.util.Log
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.ntg.core.database.model.AccountEntity
import com.ntg.core.model.AccountWithSources
import com.ntg.core.model.RawAccountWithSource
import com.ntg.core.model.SourceType
import com.ntg.core.model.SourceWithDetail
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: AccountEntity)

    @Delete
    suspend fun delete(account: AccountEntity)

    @Query("UPDATE accounts SET isRemoved=1 WHERE id=:accountId")
    suspend fun delete(accountId: Int)

    @Query("DELETE FROM accounts WHERE id=:accountId")
    suspend fun forceDelete(accountId: Int)

    @Upsert
    suspend fun upsert(account: AccountEntity)

    @Update
    suspend fun update(account: AccountEntity)

    @Query("SELECT * FROM accounts WHERE id=:id")
    suspend fun getAccount(id: Int): AccountEntity?

    @Query("SELECT * FROM accounts WHERE sId=:sId")
    suspend fun getAccount(sId: String): AccountEntity?

    @Query("SELECT * FROM accounts")
    suspend fun getAll(): List<AccountEntity>

    @Query("SELECT * FROM accounts WHERE isSelected = 1")
    suspend fun currentAccount(): AccountEntity?

    @Query(
        """
        SELECT ae.id as accountId, ae.name as accountName, ae.isDefault as isDefaultAccount,
               se.id as sourceId, se.type, se.data,
               cr.country, cr.nativeName as name, cr.symbol
        FROM accounts ae
        LEFT JOIN wallets se ON ae.id = se.accountId AND se.isRemoved = 0
        LEFT JOIN currencies cr ON se.currencyId = cr.id
        WHERE ae.isRemoved = 0
        """
    )
    fun getAccountBySources(): Flow<List<RawAccountWithSource>>

    @Query(
        """
        SELECT ae.id as accountId, ae.name as accountName, ae.isDefault as isDefaultAccount,
               se.id as sourceId, se.type, se.data,
               cr.country, cr.nativeName as name, cr.symbol
        FROM accounts ae
        LEFT JOIN wallets se ON ae.id = se.accountId AND se.isRemoved = 0
        LEFT JOIN currencies cr ON se.currencyId = cr.id
        WHERE ae.isRemoved = 0 AND ae.isSelected = 1
        """
    )
    fun getSelectedAccountBySources(): Flow<List<RawAccountWithSource>>

    @Query("SELECT * FROM accounts WHERE isSynced = 0 AND isRemoved = 0")
    suspend fun unSynced(): List<AccountEntity>?

    @Query("SELECT * FROM accounts WHERE isRemoved = 1")
    suspend fun removedAccounts(): List<AccountEntity>?

    @Query("UPDATE accounts SET isSynced=1, sId=:sId  WHERE id=:id")
    suspend fun synced(id: Int, sId: String)

    @Query("UPDATE accounts SET isSelected = CASE WHEN id=:id THEN 1 ELSE 0 END")
    suspend fun updateSelectedAccount(id: Int)

    @Query("UPDATE accounts SET isSelected = 1 WHERE isDefault = 1")
    suspend fun selectDeafult()
}