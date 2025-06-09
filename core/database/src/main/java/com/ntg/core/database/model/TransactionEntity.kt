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
    val toSourceId: Int? = null,
    val type: Int? = null,
    val categoryId: Int? = null,
    val amount: Long,
    val fee: Long? = null,
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
        fee = fee,
        note = note,
        sourceId = sourceId,
        toSourceId = toSourceId,
        date = date,
        images = images,
        tags = tags,
        walletData = null,
        destWalletData = null
    )

fun Transaction.toEntity() =
    TransactionEntity(
        id = id,
        sId = sId,
        accountId = accountId,
        sourceId = sourceId,
        toSourceId = toSourceId,
        type = type,
        categoryId = categoryId,
        amount = amount,
        fee = fee,
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