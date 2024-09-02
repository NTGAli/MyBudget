package com.ntg.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val sId: String?=null,
    val walletId: Int,
    val type: String? = null,
    val categoryId: Int? = null,
    val amount: Long,
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long,
)
