package com.ntg.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ntg.core.database.model.SourceExpenditureEntity

@Dao
interface SourceExpenditureDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(sourceExpenditure: SourceExpenditureEntity)

    @Delete
    suspend fun delete(sourceExpenditure: SourceExpenditureEntity)

    @Query("SELECT * FROM sourceExpenditures")
    suspend fun getAll(): List<SourceExpenditureEntity>

}