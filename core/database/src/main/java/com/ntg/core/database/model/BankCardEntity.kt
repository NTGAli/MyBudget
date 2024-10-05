package com.ntg.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ntg.core.model.SourceType

@Entity(
    tableName = "bank_card_entity"
)
data class BankCardEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val sourceId: Int? = null,
    val bankId: Int? = null,
    val number: String,
    val date: String,
    val cvv: String? = null,
    val sheba: String? = null,
    val accountNumber: String? = null,
    val name: String,
    val isDeleted: Boolean = false,
    val createdAt: String = System.currentTimeMillis().toString(),
    val updatedAt: String= System.currentTimeMillis().toString(),
)


fun BankCardEntity.asBank() =
    SourceType.BankCard(
        id = id,
        sourceId = sourceId,
        number = number,
        date = date,
        cvv = cvv,
        sheba = sheba,
        accountNumber = accountNumber,
        name = name,
        isDeleted = isDeleted,
        updatedAt = updatedAt
    )


fun SourceType.BankCard.toEntity() =
    BankCardEntity(
        id = id,
        sourceId = sourceId,
        number = number,
        date = date.orEmpty(),
        cvv = cvv,
        sheba = sheba,
        accountNumber = accountNumber,
        name = name,
        isDeleted = isDeleted,
        updatedAt = updatedAt.orEmpty(),
    )
