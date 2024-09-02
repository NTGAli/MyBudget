package com.ntg.core.data.repository.transaction

import com.ntg.core.model.Transaction

interface TransactionsRepository {

    suspend fun insertNewTransaction(transaction: Transaction)

}