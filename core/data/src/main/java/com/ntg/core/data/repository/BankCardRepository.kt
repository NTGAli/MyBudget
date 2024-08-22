package com.ntg.core.data.repository

import com.ntg.core.model.SourceType
import kotlinx.coroutines.flow.Flow

interface BankCardRepository {

    suspend fun insert(card: SourceType.BankCard)

    suspend fun update(card: SourceType.BankCard)

    suspend fun delete(card: SourceType.BankCard)

    suspend fun tempRemove(id: Int)

    fun getAll(): Flow<List<SourceType.BankCard>>

}