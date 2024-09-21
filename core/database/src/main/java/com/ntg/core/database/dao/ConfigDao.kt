package com.ntg.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ntg.core.database.model.ConfigEntity

@Dao
interface ConfigDao {

    @Upsert
    suspend fun upsert(config: List<ConfigEntity>)

    @Query("SELECT * FROM configs")
    suspend fun getAll(): List<ConfigEntity>

    @Query("SELECT * FROM configs WHERE key = :key")
    suspend fun get(key: String): ConfigEntity?

}