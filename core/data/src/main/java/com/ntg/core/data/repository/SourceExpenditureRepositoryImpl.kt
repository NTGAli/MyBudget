package com.ntg.core.data.repository

import com.ntg.core.database.dao.BankCardDao
import com.ntg.core.database.dao.SourceExpenditureDao
import com.ntg.core.database.model.SourceExpenditureEntity
import com.ntg.core.database.model.asSource
import com.ntg.core.database.model.toEntity
import com.ntg.core.model.SourceExpenditure
import com.ntg.core.model.SourceType
import com.ntg.core.model.SourceWithDetail
import com.ntg.core.mybudget.common.BudgetDispatchers
import com.ntg.core.mybudget.common.Dispatcher
import com.ntg.core.mybudget.common.logd
import com.ntg.core.network.BudgetNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SourceExpenditureRepositoryImpl@Inject constructor(
    @Dispatcher(BudgetDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val sourceExpenditureDao: SourceExpenditureDao,
    private val cardDao: BankCardDao,
    private val network: BudgetNetworkDataSource
): SourceExpenditureRepository{

    override suspend fun insert(sourceExpenditure: SourceExpenditure) {
        sourceExpenditureDao.insert(sourceExpenditure.toEntity())
    }

    override suspend fun delete(sourceExpenditure: SourceExpenditure) {
        sourceExpenditureDao.delete(sourceExpenditure.toEntity())
    }

    override suspend fun tempRemove(sourceId: Int) {
        val source = sourceExpenditureDao.getSource(sourceId)
        sourceExpenditureDao.tempRemove(sourceId)
        if (source?.type == 0){
            cardDao.tempRemove(sourceId)
        }
    }

    override fun getAll(): Flow<List<SourceExpenditure>> =
        flow {
            emit(
                sourceExpenditureDao.getAll()
                    .map(SourceExpenditureEntity::asSource)
            )
        }
            .flowOn(ioDispatcher)

    override fun getSourcesByAccount(accountId: Int): Flow<List<SourceWithDetail>> =
        flow {
            emit(
                sourceExpenditureDao.getSourcesWithDetails(accountId)
            )
        }.flowOn(ioDispatcher)

    override fun getSourcesById(accountId: Int): Flow<SourceExpenditure?> =
        flow {
            emit(
                sourceExpenditureDao.getSource(accountId)
                    ?.asSource()
            )
        }.flowOn(ioDispatcher)

    override fun getSourceDetails(id: Int): Flow<SourceWithDetail?> =
        flow {
            emit(
                sourceExpenditureDao.getSourceDetails(id)
            )
        }.flowOn(ioDispatcher)

    override suspend fun syncSources() {
        sourceExpenditureDao.unSynced().collect{
            it.forEach {row ->
                val sourceType = when (row.type) {
                    1 -> SourceType.BankCard(
                        id = -1,
                        number = row.number ?: "",
                        cvv = row.cvv ?: "",
                        date = row.expire ?: "",
                        name = row.cardName.orEmpty(),
                        sheba = row.sheba.orEmpty(),
                        accountNumber = row.accountNumber.orEmpty(),
                        nativeName = row.bankName.orEmpty(),
                        bankId = row.bankId
                    )

                    2 -> SourceType.Gold(
                        value = row.value ?: 0.0,
                        weight = row.weight ?: 0.0
                    )

                    else -> throw IllegalArgumentException("Unknown type")
                }

                if (row.sId == null){
                    network.syncSources(
                        SourceWithDetail(
                            id = row.id,
                            accountId = row.accountId,
                            accountSId = row.accountSId,
                            currencyId = row.currencyId,
                            type = row.type,
                            name = row.name,
                            sourceType = sourceType
                        )
                    ).collect{
                        logd("syncSources ::: $it")
                    }
                }

            }
        }
    }
}