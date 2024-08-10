package com.ntg.core.model

data class SourceExpenditure(
    val id: Int,
    val sId: String,
    val accountId: Int,
    val name: String,
    val symbol: String,
    val isoCode: String,
    val precision: Int = 0,
    val isSelected: Boolean,
    val isCrypto: Boolean = false,
    val isSynced: Boolean = false,
    val dateCreated: String,
)
