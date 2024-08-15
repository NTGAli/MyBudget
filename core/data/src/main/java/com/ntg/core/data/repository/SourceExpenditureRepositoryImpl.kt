package com.ntg.core.data.repository

import com.ntg.core.database.dao.SourceExpenditureDao
import com.ntg.core.database.model.SourceExpenditureEntity
import com.ntg.core.database.model.asSource
import com.ntg.core.database.model.toEntity
import com.ntg.core.model.SourceExpenditure
import com.ntg.core.model.SourceWithDetail
import com.ntg.core.mybudget.common.BudgetDispatchers
import com.ntg.core.mybudget.common.Dispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SourceExpenditureRepositoryImpl@Inject constructor(
    @Dispatcher(BudgetDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val sourceExpenditureDao: SourceExpenditureDao
): SourceExpenditureRepository{

    override suspend fun insert(sourceExpenditure: SourceExpenditure) {
        sourceExpenditureDao.insert(sourceExpenditure.toEntity())
    }

    override suspend fun delete(sourceExpenditure: SourceExpenditure) {
        sourceExpenditureDao.delete(sourceExpenditure.toEntity())
    }

    override suspend fun getAll(): Flow<List<SourceExpenditure>> =
        flow {
            emit(
                sourceExpenditureDao.getAll()
                    .map(SourceExpenditureEntity::asSource)
            )
        }
            .flowOn(ioDispatcher)

    override suspend fun getSourcesByAccount(accountId: Int): Flow<List<SourceWithDetail>> =
        flow {
            emit(
                sourceExpenditureDao.getSourceWithDetails(accountId)
            )
        }.flowOn(ioDispatcher)
}