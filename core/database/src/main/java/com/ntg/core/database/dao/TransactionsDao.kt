package com.ntg.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ntg.core.database.model.TransactionEntity

@Dao
interface TransactionsDao {

    @Insert
    suspend fun insert(transactionEntity: TransactionEntity)

    @Delete
    suspend fun delete(transactionEntity: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE sourceId IN (:ids)")
    suspend fun getBySourceIds(ids: List<Int>): List<TransactionEntity>


    @Query("DELETE FROM transactions WHERE accountId = :accountId")
    suspend fun deleteByAccount(accountId: Int)

}