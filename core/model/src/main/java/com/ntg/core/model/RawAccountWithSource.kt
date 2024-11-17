package com.ntg.core.model

data class RawAccountWithSource(
    val accountId: Int,
    val accountName: String,
    val isDefaultAccount: Boolean,
    val bankId: Int? = null,
    val sourceId: Int?,
    val type: Int?,
    val data: SourceType? = null
)