package com.ntg.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ntg.core.database.model.BankCardEntity

@Dao
interface BankCardEntityDao {

    @Insert
    suspend fun insert(cardEntity: BankCardEntity)

    @Delete
    suspend fun delete(cardEntity: BankCardEntity)

    @Query("SELECT * FROM bank_card_entity")
    suspend fun getAll(): List<BankCardEntity>

    @Query("SELECT * FROM bank_card_entity WHERE id = :id")
    suspend fun getById(id: Long): BankCardEntity

}