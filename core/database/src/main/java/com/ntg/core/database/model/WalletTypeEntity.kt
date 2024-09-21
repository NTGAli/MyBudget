package com.ntg.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ntg.core.model.res.WalletType

@Entity(tableName = "wallet_types")
data class WalletTypeEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int? = null,
    val enName: String? = null,
    val faName: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

fun WalletTypeEntity.asWalletType() =
    WalletType(
        id = id,
        enName = enName,
        faName = faName,
        createdAt  = createdAt,
        updatedAt = updatedAt
    )

fun WalletType.toEntity() =
    WalletTypeEntity(
        id = id,
        enName = enName,
        faName = faName,
        createdAt  = createdAt,
        updatedAt = updatedAt
    )