package com.ntg.core.data.repository

import com.ntg.core.model.Wallet
import com.ntg.core.model.SourceWithDetail
import kotlinx.coroutines.flow.Flow

interface WalletsRepository {

    suspend fun insert(wallet: Wallet)

    suspend fun update(wallet: Wallet)

    suspend fun delete(sourceExpenditure: Wallet)

    suspend fun tempRemove(sourceId: Int)

    suspend fun updateSelectedSources(sourceIds: List<Int>)

    suspend fun selectWalletFronDefault()

    fun getAll(): Flow<List<Wallet>>

    fun getSourcesByAccount(accountId: Int): Flow<List<SourceWithDetail>>

    fun getSourcesById(accountId: Int): Flow<Wallet?>

    fun getSourceDetails(id: Int): Flow<Wallet?>

    suspend fun getSelectedWalletIds(): Flow<List<Int>>

    suspend fun getSelectedSources(): Flow<List<Wallet>>

    suspend fun syncSources()

    suspend fun needToSync(id: Int)

}