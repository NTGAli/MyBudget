package com.ntg.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.ntg.core.database.model.BankEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BankDao {

    @Insert
    suspend fun updateBank(bank: BankEntity)

    @Update
    suspend fun updateBanks(banks: List<BankEntity>)

    @Upsert
    suspend fun upsertBanks(banks: List<BankEntity>)

    @Query("SELECT * FROM banks")
    suspend fun getBanks(): List<BankEntity>

    @Query("UPDATE banks SET colorLogo = :colorLogo")
    suspend fun updateColorLogo(colorLogo: String)

    @Query("UPDATE banks SET monoLogo = :logo")
    suspend fun updateMonoLogo(logo: String)

}