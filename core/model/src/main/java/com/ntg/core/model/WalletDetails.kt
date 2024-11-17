package com.ntg.core.model

data class WalletDetails(
    val id: Int,
    val sId: String? = null,
    val type: Int? = null,
    var accountId: Int,
    var accountSId: String? = null,
    val icon: String?=null,
    val currencyId:Int?= null,
    val isSelected: Boolean,
    val isSynced: Boolean = false,
    val data: SourceType? = null,
    val isRemoved: Boolean? = null,
    val dateCreated: String = "",
    val nativeName: String? = null,
    val enName: String? = null,
    val faName: String? = null,
    val symbol: String? = null,
    val country: String? = null,
    val countryAlpha2: String? = null,
    val countryAlpha3: String? = null,
    val isoCode: String? = null,
    val precision: String? = null,
    val isCrypto: Int? = null
)