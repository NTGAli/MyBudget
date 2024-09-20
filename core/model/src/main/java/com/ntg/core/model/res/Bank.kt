package com.ntg.core.model.res

import com.google.gson.annotations.SerializedName

data class Bank(
    val id: Int,
    @SerializedName("en_name")
    val enName: String,
    @SerializedName("native_name")
    val nativeName: String,
    @SerializedName("logo_name")
    val logoName: String,
    @SerializedName("country_alpha2")
    val countryAlpha2: String,
    @SerializedName("country_alpha3")
    val countryAlpha3: String,
    val bin: List<String>,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)
