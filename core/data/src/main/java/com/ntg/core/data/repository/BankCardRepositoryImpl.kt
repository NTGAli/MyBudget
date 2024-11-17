package com.ntg.core.data.repository

import com.ntg.core.database.dao.BankDao
import com.ntg.core.database.model.BankEntity
import com.ntg.core.database.model.toBank
import com.ntg.core.database.model.toEntity
import com.ntg.core.model.res.Bank
import com.ntg.core.mybudget.common.BudgetDispatchers
import com.ntg.core.mybudget.common.Dispatcher
import com.ntg.core.network.BudgetNetworkDataSource
import com.ntg.core.network.model.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


class BankCardRepositoryImpl @Inject constructor(
    @Dispatcher(BudgetDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val bankDao: BankDao,
    private val network: BudgetNetworkDataSource
): BankCardRepository {

    override suspend fun getUserLocalBanks(): Flow<List<Bank>> {
        network.serverBanks().collect{
            if (it is Result.Success){
                bankDao.upsertBanks(it.data.orEmpty().map { it
                    .toEntity() })
            }
        }
        return  flow {
            emit(
                bankDao.getBanks()
                    .map(BankEntity::toBank)
            )
        }
            .flowOn(ioDispatcher)
    }

    override suspend fun getLocalBankCount(): Int {
        return bankDao.count()
    }
}