package com.ntg.core.model.res

import com.google.gson.annotations.SerializedName

data class CodeVerification(
    @SerializedName("default_account_id")
    val defaultAccountId: String? = null,
    @SerializedName("access_token")
    val accessToken: String? = null,
    @SerializedName("token_type")
    val tokenType: String? = null,
    @SerializedName("expires_at")
    val expiresAt: String? = null

)
