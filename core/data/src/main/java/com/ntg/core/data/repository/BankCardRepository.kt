package com.ntg.core.data.repository

import com.ntg.core.model.SourceType
import com.ntg.core.model.res.Bank
import kotlinx.coroutines.flow.Flow

interface BankCardRepository {

    suspend fun insert(card: SourceType.BankCard)

    suspend fun update(card: SourceType.BankCard)

    suspend fun delete(card: SourceType.BankCard)

    suspend fun tempRemove(id: Int)

    fun getAll(): Flow<List<SourceType.BankCard>>

    suspend fun getUserLocalBanks(): Flow<List<Bank>>

    suspend fun getLocalBankCount(): Int

}