package com.ntg.core.model

data class AccountWithSources(
    val accountId: Int,
    val accountName: String,
    val isDefault: Boolean,
    val sources: List<Wallet?> = emptyList()
)