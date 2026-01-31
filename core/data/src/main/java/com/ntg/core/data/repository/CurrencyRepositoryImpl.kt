package com.ntg.core.data.repository

import com.ntg.core.database.dao.CurrencyDao
import com.ntg.core.database.model.CurrencyEntity
import com.ntg.core.database.model.toCurrency
import com.ntg.core.database.model.toCurrencyEntity
import com.ntg.core.model.res.Currency
import com.ntg.core.mybudget.common.BudgetDispatchers
import com.ntg.core.mybudget.common.Dispatcher
import com.ntg.core.network.BudgetNetworkDataSource
import com.ntg.core.network.model.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(
    private val network: BudgetNetworkDataSource,
    private val currencyDao: CurrencyDao,
    @Dispatcher(BudgetDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
): CurrencyRepository {

    private val defaultCurrencies = listOf(
        CurrencyEntity(id = 1, faName = "تومان", enName = "Iranian Toman", symbol = "ت", countryAlpha2 = "IR", isoCode = "IRR", isCrypto = 0),
        CurrencyEntity(id = 2, faName = "دلار آمریکا", enName = "US Dollar", symbol = "$", countryAlpha2 = "US", isoCode = "USD", isCrypto = 0),
        CurrencyEntity(id = 3, faName = "یورو", enName = "Euro", symbol = "€", countryAlpha2 = "EU", isoCode = "EUR", isCrypto = 0),
        CurrencyEntity(id = 4, faName = "افغانی", enName = "Afghan Afghani", symbol = "؋", countryAlpha2 = "AF", isoCode = "AFN", isCrypto = 0),
        CurrencyEntity(id = 5, faName = "دینار عراق", enName = "Iraqi Dinar", symbol = "ع.د", countryAlpha2 = "IQ", isoCode = "IQD", isCrypto = 0),
        CurrencyEntity(id = 6, faName = "پوند بریتانیا", enName = "British Pound", symbol = "£", countryAlpha2 = "GB", isoCode = "GBP", isCrypto = 0),
        CurrencyEntity(id = 7, faName = "لیر ترکیه", enName = "Turkish Lira", symbol = "₺", countryAlpha2 = "TR", isoCode = "TRY", isCrypto = 0),
        CurrencyEntity(id = 8, faName = "درهم امارات", enName = "UAE Dirham", symbol = "د.إ", countryAlpha2 = "AE", isoCode = "AED", isCrypto = 0),
        CurrencyEntity(id = 9, faName = "روپیه هند", enName = "Indian Rupee", symbol = "₹", countryAlpha2 = "IN", isoCode = "INR", isCrypto = 0),
        CurrencyEntity(id = 10, faName = "یوان چین", enName = "Chinese Yuan", symbol = "¥", countryAlpha2 = "CN", isoCode = "CNY", isCrypto = 0),
    )

    override suspend fun upsert() {
        val existing = currencyDao.currencies()
        if (existing.isNullOrEmpty()) {
            currencyDao.upsert(defaultCurrencies)
        }

        CoroutineScope(ioDispatcher).launch {
            try {
                network.currencies().collect {
                    if (it is Result.Success) {
                        currencyDao.upsert(it.data.orEmpty().map { it.toCurrencyEntity() })
                    }
                }
            } catch (_: Exception) {
                // Fail silently — defaults are already seeded
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