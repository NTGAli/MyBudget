package com.ntg.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ntg.core.database.model.TransactionEntity
import com.ntg.core.model.Transaction

@Dao
interface TransactionsDao {

    @Insert
    suspend fun insert(transactionEntity: TransactionEntity)

    @Delete
    suspend fun delete(transactionEntity: TransactionEntity)

    @Query("""
        SELECT * FROM transactions t
        INNER JOIN category_table c
        ON c.id = t.categoryId
        WHERE t.sourceId IN (:ids)
        ORDER BY t.createdAt DESC
    """)
    suspend fun getBySourceIds(ids: List<Int>): List<Transaction>


    @Query("DELETE FROM transactions WHERE accountId = :accountId")
    suspend fun deleteByAccount(accountId: Int)

}