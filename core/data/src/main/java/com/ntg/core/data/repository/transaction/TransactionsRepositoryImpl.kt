package com.ntg.core.data.repository.transaction

import com.ntg.core.database.dao.ContactDao
import com.ntg.core.database.dao.TransactionsDao
import com.ntg.core.database.model.toContactEntity
import com.ntg.core.database.model.toEntity
import com.ntg.core.model.Transaction
import com.ntg.core.mybudget.common.BudgetDispatchers
import com.ntg.core.mybudget.common.Dispatcher
import com.ntg.core.mybudget.common.logd
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class TransactionsRepositoryImpl @Inject constructor(
    private val transactionsDao: TransactionsDao,
    private val contactDao: ContactDao,
    @Dispatcher(BudgetDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : TransactionsRepository {

    override suspend fun insertNewTransaction(transaction: Transaction) {
        logd("contacts :::: ${transaction.contacts}")
        val transactionId = transactionsDao.insert(transaction.toEntity())
        contactDao.insertAll(transaction.contacts.orEmpty().map { it.toContactEntity(transactionId.toInt()) })
    }

    override fun getTransactionsBySourceIds(sourceIds: List<Int>): Flow<List<Transaction>> =
        flow {
            emit(transactionsDao.getBySourceIds(sourceIds))
        }
            .flowOn(ioDispatcher)
}