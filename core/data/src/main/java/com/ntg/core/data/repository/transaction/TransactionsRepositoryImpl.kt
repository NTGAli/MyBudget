package com.ntg.core.data.repository.transaction

import com.ntg.core.database.dao.TransactionsDao
import com.ntg.core.database.model.toEntity
import com.ntg.core.model.Transaction
import javax.inject.Inject

class TransactionsRepositoryImpl @Inject constructor(
    private val transactionsDao: TransactionsDao
) : TransactionsRepository {

    override suspend fun insertNewTransaction(transaction: Transaction) {
        transactionsDao.insert(transaction.toEntity())
    }
}