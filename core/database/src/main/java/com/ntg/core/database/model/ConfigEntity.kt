package com.ntg.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ntg.core.model.res.ServerConfig

@Entity(tableName = "configs")
data class ConfigEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val key: String,
    val value: String,
    val platform: String? = null,
    val updatedAt: String? = null,
    val createdAt: String? = null
)


fun ConfigEntity.toConfig() =
    ServerConfig(
        id = id,
        key = key,
        value = value,
        platform = platform,
        updatedAt = updatedAt,
        createdAt = createdAt
    )

fun ServerConfig.toEntity() =
    ConfigEntity(
        id = id ?: -1,
        key = key.orEmpty(),
        value = value.orEmpty(),
        platform = platform,
        updatedAt = updatedAt,
        createdAt = createdAt
    )