package com.ntg.core.data.repository.transaction

import com.ntg.core.database.dao.ContactDao
import com.ntg.core.database.dao.TransactionsDao
import com.ntg.core.database.model.asWallet
import com.ntg.core.database.model.toContactEntity
import com.ntg.core.database.model.toEntity
import com.ntg.core.model.Contact
import com.ntg.core.model.Transaction
import com.ntg.core.model.Wallet
import com.ntg.core.mybudget.common.BudgetDispatchers
import com.ntg.core.mybudget.common.Dispatcher
import com.ntg.core.mybudget.common.logd
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionsRepositoryImpl @Inject constructor(
    private val transactionsDao: TransactionsDao,
    private val contactDao: ContactDao,
    @Dispatcher(BudgetDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : TransactionsRepository {

    override suspend fun insertNewTransaction(transaction: Transaction) {
        val transactionId = transactionsDao.insert(transaction.toEntity())
//        contactDao.insertAll(transaction.contacts.orEmpty().map { it.toContactEntity(transactionId.toInt()) })
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        val transactionId = transactionsDao.update(transaction.toEntity())
//        contactDao.upsertAll(transaction.contacts.orEmpty().map { it.toContactEntity(transactionId.toInt()) })
    }

    override suspend fun deleteTransaction(id: Int) {
        transactionsDao.delete(id)
//        contactDao.deleteContactByTransaction(id)
    }

    override fun getTransactionsBySourceIds(sourceIds: List<Int>): Flow<List<Transaction>> =
        flow {
            emit(transactionsDao.getBySourceIds(sourceIds))
        }
            .flowOn(ioDispatcher)

    override fun getSelectedWalletTransactions(): Flow<List<Transaction>> =
        transactionsDao.getSelectedWalletTransactions()
            .map { entities ->
                entities.map { it }
            }
            .flowOn(ioDispatcher)

    override fun transactionById(id: Int): Flow<Transaction?> =
        flow {
            emit(transactionsDao.transactionById(id))
        }
            .flowOn(ioDispatcher)
}