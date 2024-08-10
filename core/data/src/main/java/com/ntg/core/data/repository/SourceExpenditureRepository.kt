package com.ntg.core.data.repository

import com.ntg.core.model.SourceExpenditure
import kotlinx.coroutines.flow.Flow

interface SourceExpenditureRepository {

    suspend fun insert(sourceExpenditure: SourceExpenditure)

    suspend fun delete(sourceExpenditure: SourceExpenditure)

    suspend fun getAll(): Flow<List<SourceExpenditure>>

}