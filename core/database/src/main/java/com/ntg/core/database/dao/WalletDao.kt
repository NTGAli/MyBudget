package com.ntg.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ntg.core.database.model.WalletTypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {

    @Upsert
    suspend fun upsert(walletEntities: List<WalletTypeEntity>)

    @Query("SELECT * FROM wallet_types")
    suspend fun walletTypes(): List<WalletTypeEntity>?

    @Query("DELETE FROM wallet_types")
    suspend fun deleteAll()

}