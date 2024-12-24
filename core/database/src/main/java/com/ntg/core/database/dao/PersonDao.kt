package com.ntg.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Upsert
import com.ntg.core.database.model.PersonEntity

@Dao
interface PersonDao {

    @Insert
    suspend fun insert(person: PersonEntity)

    @Update
    suspend fun update(person: PersonEntity)

    @Delete
    suspend fun delete(person: PersonEntity)

    @Upsert
    suspend fun upsert(person: PersonEntity)

}