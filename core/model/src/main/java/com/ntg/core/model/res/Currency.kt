package com.ntg.core.model.res

import com.google.gson.annotations.SerializedName

data class Currency(
    val id: Int,
    @SerializedName("native_name")
    val nativeName: String? = null,
    @SerializedName("en_name")
    val enName: String? = null,
    @SerializedName("fa_name")
    val faName: String? = null,
    val symbol: String? = null,
    val country: String? = null,
    @SerializedName("country_alpha2")
    val countryAlpha2: String? = null,
    @SerializedName("country_alpha3")
    val countryAlpha3: String? = null,
    @SerializedName("iso_code")
    val isoCode: Int? = null,
    val precision: String? = null,
    @SerializedName("is_crypto")
    val isCrypto: Int? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)
