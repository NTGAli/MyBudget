package com.ntg.core.model.res

import com.google.gson.annotations.SerializedName

data class ServerConfig(
    val id: Int? = null,
    val platform: String? = null,
    val key: String? = null,
    val value: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
)
