package com.ntg.core.data.repository

import com.ntg.core.database.dao.CurrencyDao
import com.ntg.core.database.model.toCurrency
import com.ntg.core.database.model.toCurrencyEntity
import com.ntg.core.model.res.Currency
import com.ntg.core.mybudget.common.BudgetDispatchers
import com.ntg.core.mybudget.common.Dispatcher
import com.ntg.core.network.BudgetNetworkDataSource
import com.ntg.core.network.model.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(
    private val network: BudgetNetworkDataSource,
    private val currencyDao: CurrencyDao,
    @Dispatcher(BudgetDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
): CurrencyRepository {
    override suspend fun upsert() {
        network.currencies().collect{
            if (it is Result.Success){
                currencyDao.upsert(it.data.orEmpty().map { it.toCurrencyEntity() })
            }
        }
    }

    override suspend fun getCurrencies(): Flow<List<Currency>> =
        flow {
            emit(
                currencyDao.currencies().orEmpty().map { it.toCurrency() }
            )
        }
            .flowOn(ioDispatcher)
}