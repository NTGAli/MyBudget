package com.ntg.core.model.res

import com.google.gson.annotations.SerializedName

data class SyncContactsRes(
    @SerializedName("owner_id")
    val ownerId: String? = null,
    @SerializedName("full_name")
    val fullName: String? = null,
    @SerializedName("mobile_number")
    val mobileNumber: String? = null,
    @SerializedName("phone_number")
    val phoneNumber: String? = null,
    val email: String? = null,
    val address: String? = null,
    val note: String? = null,
    @SerializedName("bank_accounts")
    val bankAccounts: String? = null,
    val id: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("avatar_url")
    val avatarUrl: String? = null,
)