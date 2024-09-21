package com.ntg.core.data.repository

import com.ntg.core.database.model.ConfigEntity
import com.ntg.core.model.res.ServerConfig
import kotlinx.coroutines.flow.Flow

interface ConfigRepository {

    suspend fun updateConfigs()

    fun get(): Flow<List<ServerConfig>>

    fun get(key: String): Flow<ServerConfig?>

}