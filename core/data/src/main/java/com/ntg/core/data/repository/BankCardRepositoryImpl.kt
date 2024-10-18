package com.ntg.core.data.repository

import android.util.Log
import com.ntg.core.database.dao.BankCardDao
import com.ntg.core.database.dao.BankDao
import com.ntg.core.database.model.BankCardEntity
import com.ntg.core.database.model.BankEntity
import com.ntg.core.database.model.asBank
import com.ntg.core.database.model.toBank
import com.ntg.core.database.model.toEntity
import com.ntg.core.model.SourceType
import com.ntg.core.model.res.Bank
import com.ntg.core.mybudget.common.BudgetDispatchers
import com.ntg.core.mybudget.common.Dispatcher
import com.ntg.core.network.BudgetNetworkDataSource
import com.ntg.core.network.model.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BankCardRepositoryImpl @Inject constructor(
    @Dispatcher(BudgetDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val bankCardDao: BankCardDao,
    private val bankDao: BankDao,
    private val network: BudgetNetworkDataSource
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

    override suspend fun tempRemove(id: Int) {
        TODO("Not yet implemented")
    }

    override fun getAll(): Flow<List<SourceType.BankCard>> =
        flow {
            emit(
                bankCardDao.getAll()
                    .map(BankCardEntity::asBank)
            )
        }
            .flowOn(ioDispatcher)

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