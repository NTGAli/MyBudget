package com.ntg.core.data.repository.transaction

import com.ntg.core.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionsRepository {

    suspend fun insertNewTransaction(transaction: Transaction)

    fun getTransactionsBySourceIds(sourceIds: List<Int>): Flow<List<Transaction>>

    fun transactionById(id: Int): Flow<Transaction?>
}