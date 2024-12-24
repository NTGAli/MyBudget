package com.ntg.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ntg.core.model.Transaction

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val sId: String?=null,
    val accountId: Int,
    val sourceId: Int,
    val type: Int? = null,
    val categoryId: Int? = null,
    val amount: Long,
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,
    val note: String? = null,
    val date: Long,
    val tags: List<String>? = null,
    val images: List<String>? = null,
    val contactIds: List<String>? = null,
    val createdAt: Long,
    val updatedAt: Long,
)

fun TransactionEntity.asTransaction() =
    Transaction(
        id = id,
        sId = sId,
        accountId = accountId,
        type = type,
        categoryId = categoryId,
        amount = amount,
        note = note,
        sourceId = sourceId,
        date = date,
        images = images,
        tags = tags,
    )

fun Transaction.toEntity() =
    TransactionEntity(
        id = id,
        sId = sId,
        accountId = accountId,
        sourceId = sourceId,
        type = type,
        categoryId = categoryId,
        amount = amount,
        note = note,
        date = date,
        isSynced = false,
        isDeleted = false,
        images = images,
        tags = tags,
        contactIds = contactIds,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )
