package com.ntg.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ntg.core.model.BankCard

@Entity(
    tableName = "bank_card_entity"
)
data class BankCardEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val sId: String? = null,
    val sourceId: Int? = null,
    val number: String,
    val date: String,
    val cvv: String? = null,
    val name: String,
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,
    val createdAt: String = System.currentTimeMillis().toString(),
    val updatedAt: String= System.currentTimeMillis().toString(),
)


fun BankCardEntity.asBank() =
    BankCard(
        id = id,
        sId = sId,
        sourceId = sourceId,
        number = number,
        date = date,
        cvv = cvv,
        name = name,
        isSynced = isSynced,
        isDeleted = isDeleted,
        updatedAt = updatedAt
    )


fun BankCard.toEntity() =
    BankCardEntity(
        id = id,
        sId = sId,
        sourceId = sourceId,
        number = number,
        date = date,
        cvv = cvv,
        name = name,
        isSynced = isSynced,
        isDeleted = isDeleted,
        updatedAt = updatedAt,
    )
