package com.ntg.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ntg.core.database.model.WalletEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {

    @Upsert
    suspend fun upsert(walletEntities: List<WalletEntity>)

    @Query("SELECT * FROM wallet_types")
    fun wallets(): Flow<List<WalletEntity>?>

}