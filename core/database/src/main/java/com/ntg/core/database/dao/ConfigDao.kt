package com.ntg.core.database.dao

import androidx.room.Dao
import androidx.room.Upsert
import com.ntg.core.database.model.ConfigEntity

@Dao
interface ConfigDao {

    @Upsert
    suspend fun upsert(config: List<ConfigEntity>)


}