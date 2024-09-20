package com.ntg.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "configs")
data class ConfigEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val key: String,
    val value: String,
    val platform: String? = null,
    val updatedAt: String? = null,
    val createdAt: String? = null
)
