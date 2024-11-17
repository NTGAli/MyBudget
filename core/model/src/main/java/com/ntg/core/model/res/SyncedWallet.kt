package com.ntg.core.model.res

import com.google.gson.annotations.SerializedName

data class SyncedWallet(
    val id: String? = null,
    @SerializedName("wallet_type_id")
    val walletTypeId: Int?=null,
    @SerializedName("currency_id")
    val currencyId: Int?=null,
    @SerializedName("account_id")
    val accountId: String?=null,
    val details: WalletDetails?=null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)


data class WalletDetails(
    @SerializedName("bank_id")
    val bankId: String?=null,
    @SerializedName("bank_name")
    val bankName: String?=null,
    @SerializedName("cart_number")
    val cartNumber: String?=null,
    @SerializedName("cart_owner_name")
    val ownerName: String?=null,
    val cvv2: String?=null,
    )