package com.ntg.core.model.res

import com.google.gson.annotations.SerializedName

data class Bank(
    val id: Int? = null,
    @SerializedName("en_name")
    val enName: String? = null,
    @SerializedName("native_name")
    val nativeName: String? = null,
    @SerializedName("logo_name")
    val logoName: String? = null,
    @SerializedName("country_alpha2")
    val countryAlpha2: String? = null,
    @SerializedName("country_alpha3")
    val countryAlpha3: String? = null,
    val bin: List<String>? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)

data class BankRes(
    val data: List<Bank>? = null,
)