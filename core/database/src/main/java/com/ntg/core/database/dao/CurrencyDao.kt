package com.ntg.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ntg.core.database.model.CurrencyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {

    @Upsert
    suspend fun upsert(currencies: List<CurrencyEntity>)

    @Query("SELECT * FROM currencies")
    suspend fun currencies(): List<CurrencyEntity>?

}