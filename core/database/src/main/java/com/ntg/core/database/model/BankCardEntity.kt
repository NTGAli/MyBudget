package com.ntg.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "bank_card_entity"
)
data class BankCardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val sId: String? = null,
    val number: String,
    val date: String,
    val cvv: String? = null,
    val name: String,
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,
    val createdAt: String = System.currentTimeMillis().toString(),
    val updatedAt: String= System.currentTimeMillis().toString(),
)
