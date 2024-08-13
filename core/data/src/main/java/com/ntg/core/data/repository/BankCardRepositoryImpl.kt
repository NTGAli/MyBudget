package com.ntg.core.data.repository

import android.util.Printer
import com.ntg.core.database.dao.BankCardDao
import com.ntg.core.model.BankCard
import com.ntg.core.mybudget.common.BudgetDispatchers
import com.ntg.core.mybudget.common.Dispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class BankCardRepositoryImpl(
    @Dispatcher(BudgetDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,,
    private val bankCardDao: BankCardDao
): BankCardRepository {
    override suspend fun insert(card: BankCard) {
//        bankCardDao.insert(card)
    }

    override suspend fun delete(card: BankCard) {
        TODO("Not yet implemented")
    }

    override fun getAll(): Flow<List<BankCard>> {
        TODO("Not yet implemented")
    }
}