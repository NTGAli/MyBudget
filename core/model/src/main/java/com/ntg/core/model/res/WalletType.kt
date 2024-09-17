package com.ntg.core.model.res

import com.google.gson.annotations.SerializedName

data class WalletType(
    val id: Int? = null,
    @SerializedName("en_name")
    val enName: String? = null,
    @SerializedName("fa_name")
    val faName: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)
