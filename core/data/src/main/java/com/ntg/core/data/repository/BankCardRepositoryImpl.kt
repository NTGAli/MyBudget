package com.ntg.core.data.repository

import com.ntg.core.database.dao.BankCardDao
import com.ntg.core.database.model.BankCardEntity
import com.ntg.core.database.model.asBank
import com.ntg.core.database.model.toEntity
import com.ntg.core.model.SourceType
import com.ntg.core.mybudget.common.BudgetDispatchers
import com.ntg.core.mybudget.common.Dispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class BankCardRepositoryImpl @Inject constructor(
    @Dispatcher(BudgetDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val bankCardDao: BankCardDao
): BankCardRepository {
    override suspend fun insert(card: SourceType.BankCard) {
        bankCardDao.insert(card.toEntity())
    }

    override suspend fun update(card: SourceType.BankCard) {
        bankCardDao.update(card.toEntity())
    }

    override suspend fun delete(card: SourceType.BankCard) {
        bankCardDao.delete(card.toEntity())
    }

    override fun getAll(): Flow<List<SourceType.BankCard>> =
        flow {
            emit(
                bankCardDao.getAll()
                    .map(BankCardEntity::asBank)
            )
        }
            .flowOn(ioDispatcher)
}