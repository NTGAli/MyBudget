package com.ntg.core.model.res

import com.google.gson.annotations.SerializedName

data class SyncedAccount(
    val name: String? = null,
    @SerializedName("owner_id")
    val ownerId: String? = null,
    val id: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)
