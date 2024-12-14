package com.ntg.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.ntg.core.database.model.CurrencyEntity
import com.ntg.core.database.model.WalletEntity
import com.ntg.core.model.WalletDetails
import com.ntg.core.model.SourceWithDetail
import com.ntg.core.model.Wallet
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(wallet: WalletEntity)

    @Delete
    suspend fun delete(sourceExpenditure: WalletEntity)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(wallet: WalletEntity)

    @Query("DELETE FROM wallets WHERE id=:id")
    suspend fun forceDelete(id: Int)

    @Query("DELETE FROM wallets WHERE accountId=:accountId")
    suspend fun forceDeleteByAccountId(accountId: Int)

    @Query("SELECT * FROM wallets")
    suspend fun getAll(): List<WalletEntity>

    @Query("SELECT * FROM wallets WHERE accountId=:accountId")
    suspend fun getSources(accountId: Int): List<WalletEntity>

    @Query("SELECT * FROM wallets WHERE id=:id")
    suspend fun getSource(id: Int): WalletEntity?

    @Query("UPDATE wallets SET isRemoved=1, isSynced=0 WHERE id=:id")
    suspend fun tempRemove(id: Int)

    @Transaction
    @Query("""
        SELECT *
        FROM wallets se
        LEFT JOIN currencies cr ON se.currencyId = cr.id
        WHERE se.isRemoved != 1
    """)
    suspend fun getSourcesByAccount(): List<WalletDetails>

    @Query("""
        SELECT id
        FROM wallets 
        where isSelected = 1
    """)
    suspend fun getSelectedWalletIds(): List<Int>

    @Transaction
    suspend fun getSourcesWithDetails(accountId: Int): List<SourceWithDetail> {
        return emptyList()
    }


    @Transaction
    @Query("""
        SELECT * FROM wallets WHERE id=:id
    """)
    suspend fun getSourceWithDetails(id: Int): WalletDetails?




    @Query("UPDATE wallets SET isSelected = CASE WHEN id IN (:ids) THEN 1 ELSE 0 END")
    suspend fun updateSelectedSources(ids: List<Int>)


    @Transaction
    @Query(
        """
        SELECT * FROM wallets
        WHERE isSelected = 1
        """
    )
    suspend fun getSelectedSources(): List<WalletEntity>

    @Query(
        """
        SELECT c.* FROM wallets w 
        INNER JOIN currencies c
        ON w.currencyId = c.id
        where w.isSelected = 1 LIMIT 1
        """
    )
    suspend fun currentCurrency(): CurrencyEntity?


    @Query(
        """
        SELECT c.* FROM wallets w 
        INNER JOIN currencies c
        ON w.currencyId = c.id
        where w.accountId = :accountId = 1 LIMIT 1
        """
    )
    suspend fun currentCurrency(accountId: Int): CurrencyEntity?

    @Query("""
        SELECT w.*, a.sId as accountSId FROM wallets w
        INNER JOIN accounts a
        ON w.accountId = a.id
        WHERE w.isSynced = 0
    """)
    fun unSynced(): Flow<List<Wallet>>

    @Query("UPDATE wallets SET sId=:sId, isSynced=1 WHERE id=:id")
    suspend fun sync(id: Int, sId: String)

    @Query("UPDATE wallets SET isSynced=0 WHERE id=:id")
    suspend fun unSync(id: Int)

    @Query("UPDATE wallets SET isSelected = 1 WHERE accountId = 1")
    suspend fun slectDeafultWallet()
}