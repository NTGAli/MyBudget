package com.ntg.core.model

data class TransactionFilter(
    val type: Int? = null,
    val dateFrom: Long? = null,
    val dateTo: Long? = null,
    val categoryIds: List<Int> = emptyList(),
    val tags: List<String> = emptyList(),
    val hasImage: Boolean = false,
    val amountMin: Long? = null,
    val amountMax: Long? = null,
    val contactNames: List<String> = emptyList(),
)