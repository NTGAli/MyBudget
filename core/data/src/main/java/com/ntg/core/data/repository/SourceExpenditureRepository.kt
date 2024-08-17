package com.ntg.core.data.repository

import com.ntg.core.model.SourceExpenditure
import com.ntg.core.model.SourceWithDetail
import kotlinx.coroutines.flow.Flow

interface SourceExpenditureRepository {

    suspend fun insert(sourceExpenditure: SourceExpenditure)

    suspend fun delete(sourceExpenditure: SourceExpenditure)

    fun getAll(): Flow<List<SourceExpenditure>>

    fun getSourcesByAccount(accountId: Int): Flow<List<SourceWithDetail>>

    fun getSourcesById(accountId: Int): Flow<SourceExpenditure?>

    fun getSourceDetails(id: Int): Flow<SourceWithDetail?>
}