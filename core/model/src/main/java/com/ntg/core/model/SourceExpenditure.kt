package com.ntg.core.model

data class SourceExpenditure(
    val id: Int,
    val sId: String? = null,
    var accountId: Int,
    val name: String,
    val icon: String?=null,
    val symbol: String?=null,
    val isoCode: String?=null,
    val precision: Int = 0,
    val isSelected: Boolean,
    val isCrypto: Boolean = false,
    val isSynced: Boolean = false,
    val dateCreated: String,
)
