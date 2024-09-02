package com.ntg.core.database.dao

import androidx.room.Delete
import androidx.room.Insert
import com.ntg.core.database.model.TransactionEntity

interface TransactionsDao {

    @Insert
    suspend fun insert(transactionEntity: TransactionEntity)

    @Delete
    suspend fun delete(transactionEntity: TransactionEntity)

}