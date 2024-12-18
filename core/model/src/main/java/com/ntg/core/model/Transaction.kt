package com.ntg.core.model

data class Transaction(
    val id: Int,
    val sId: String? = null,
    val accountId: Int,
    val sourceId: Int,
    val categoryId: Int? = null,
    val name: String? = null,
    val faName: String? = null,
    val amount: Long,
    val type: Int? = null,
    val date: Long,
    val note: String? = null,
    val images: List<String>? = null,
    val tags: List<String>? = null,
    val contacts: List<Contact>? = null,
    val walletData: SourceType? = null
)
