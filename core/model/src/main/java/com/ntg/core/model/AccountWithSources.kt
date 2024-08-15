package com.ntg.core.model

data class AccountWithSources(
    val accountId: Int,
    val accountName: String,
    val sources: List<SourceWithDetail> = emptyList()
)