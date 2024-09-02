package com.ntg.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val sId: String?=null,
    val walletId: Int,
    val type: String,
    val categoryId: Int,
    val amount: Long,
    val createdAt: Long,
    val updatedAt: Long,
)
