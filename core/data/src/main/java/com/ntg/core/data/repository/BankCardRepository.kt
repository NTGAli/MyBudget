package com.ntg.core.data.repository

import com.ntg.core.model.SourceType
import com.ntg.core.model.res.Bank
import kotlinx.coroutines.flow.Flow

interface BankCardRepository {


    suspend fun getUserLocalBanks(): Flow<List<Bank>>

    suspend fun getLocalBankCount(): Int

}