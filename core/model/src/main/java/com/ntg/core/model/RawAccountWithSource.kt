package com.ntg.core.model

data class RawAccountWithSource(
    val accountId: Int,
    val accountName: String,
    val isDefaultAccount: Boolean,
    val bankId: Int? = null,
    val sourceId: Int?,
    val type: Int?,
    val name: String?,
    val sheba: String?,
    val accountNumber: String?,
    val number: String?,
    val cvv: String?,
    val expire: String?,
    val value: Double?,
    val weight: Double?
)