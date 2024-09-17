package com.ntg.core.database.model

import androidx.room.Entity

@Entity(tableName = "wallet_types")
data class WalletEntity(
    val id: Int? = null,
    val enName: String? = null,
    val faName: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
