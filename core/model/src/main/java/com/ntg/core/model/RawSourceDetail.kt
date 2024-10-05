package com.ntg.core.model

data class RawSourceDetail(
    val id: Int,
    val sId: String? = null,
    val accountSId: String?=null,
    val accountId: Int,
    val currencyId: Int? = null,
    val bankName: String? = null,
    val type: Int,
    val name: String?=null,
    val number: String?,
    val cvv: String?,
    val date: String?,
    val value: Double?,
    val weight: Double?,
    val bankId: Int?=null,
    val expire: String? = null,
    val cardName: String? = null,
    val accountNumber: String? = null,
    val sheba: String? = null,
    val isRemoved: Boolean? = false
)