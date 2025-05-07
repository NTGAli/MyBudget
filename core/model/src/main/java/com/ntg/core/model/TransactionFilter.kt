package com.ntg.core.model

data class TransactionFilter(
    val type: Int? = null,
    val dateFrom: Long? = null,
    val dateTo: Long? = null,
    val categoryId: Int? = null,
    val tags: List<String> = emptyList(),
    val hasImage: Boolean = false
)