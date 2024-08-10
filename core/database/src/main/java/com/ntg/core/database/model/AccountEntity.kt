package com.ntg.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val sId: String? = null,
    val name: String,
    val isSelected: Boolean = false,
    val isSynced: Boolean = false,
    val dateCreated: String
)
