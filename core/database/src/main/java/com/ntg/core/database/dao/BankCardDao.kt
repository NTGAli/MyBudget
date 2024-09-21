package com.ntg.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ntg.core.database.model.BankCardEntity

@Dao
interface BankCardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cardEntity: BankCardEntity)

    @Delete
    suspend fun delete(cardEntity: BankCardEntity)

    @Query("DELETE FROM bank_card_entity WHERE sourceId = :sourceId")
    suspend fun forceDelete(sourceId: Int)

    @Update
    suspend fun update(cardEntity: BankCardEntity)

    @Query("SELECT * FROM bank_card_entity")
    suspend fun getAll(): List<BankCardEntity>

    @Query("SELECT * FROM bank_card_entity WHERE id = :id")
    suspend fun getById(id: Long): BankCardEntity

    @Query("UPDATE bank_card_entity SET isDeleted=1 WHERE sourceId = :sourceId")
    suspend fun tempRemove(sourceId: Int)

}