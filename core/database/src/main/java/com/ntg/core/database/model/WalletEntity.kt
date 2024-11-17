package com.ntg.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.ntg.core.model.SourceType
import com.ntg.core.model.Wallet

@Entity("wallets")
data class WalletEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val sId: String? = null,
    val accountId: Int,
    val type: Int? = null,
    val icon: String? = null,
    val currencyId: Int? = null,
    val isSelected: Boolean,
    val isSynced: Boolean = false,
    val isRemoved: Boolean = false,
    val data: SourceType? = null,
    val dateCreated: String,
)

fun WalletEntity.asWallet() = Wallet(
    id = id,
    sId = sId,
    isSynced = isSynced,
    dateCreated = dateCreated,
    isSelected = isSelected,
    accountId = accountId,
    currencyId = currencyId,
    icon = icon,
    type = type,
    data = data,
    isRemoved = isRemoved
)


fun Wallet.toEntity() = WalletEntity(
    id = id,
    sId = sId,
    isSynced = isSynced,
    dateCreated = dateCreated,
    isSelected = isSelected,
    accountId = accountId,
    currencyId = currencyId,
    icon = icon,
    type = type,
    data = data
)