package com.ntg.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ntg.core.model.Account

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


fun AccountEntity?.adAccount() = Account(
    id = this?.id ?: -1,
    sId = this?.sId,
    name = this?.name ?: "",
    isSynced = this?.isSynced ?: false,
    isSelected = this?.isSelected ?: false,
    dateCreated = this?.dateCreated ?: ""
)

fun Account?.toEntity() = AccountEntity(
    id = this?.id ?: -1,
    sId = this?.sId,
    name = this?.name ?: "",
    isSynced = this?.isSynced ?: false,
    isSelected = this?.isSelected ?: false,
    dateCreated = this?.dateCreated ?: ""
)