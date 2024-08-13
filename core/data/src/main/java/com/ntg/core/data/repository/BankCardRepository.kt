package com.ntg.core.data.repository

import com.ntg.core.model.BankCard
import kotlinx.coroutines.flow.Flow

interface BankCardRepository {

    suspend fun insert(card: BankCard)

    suspend fun delete(card: BankCard)

    fun getAll(): Flow<List<BankCard>>

}