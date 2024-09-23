package com.ntg.core.data.repository

import com.ntg.core.model.res.Currency
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {

    suspend fun upsert()

    suspend fun getCurrencies(): Flow<List<Currency>>
}