package com.ntg.core.model.res

import com.google.gson.annotations.SerializedName

data class ServerAccount(
    val id: String? = null,
    val name: String? = null,
    @SerializedName("owner_id")
    val ownerId:String,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    @SerializedName("deleted_at")
    val deletedAt: String? = null,

)
