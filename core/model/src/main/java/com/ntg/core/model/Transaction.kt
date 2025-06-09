package com.ntg.core.model

data class Transaction(
    val id: Int,
    val sId: String? = null,
    val accountId: Int,
    val sourceId: Int,
    val toSourceId: Int? = null,
    val categoryId: Int? = null,
    val name: String? = null,
    val faName: String? = null,
    val amount: Long,
    val fee: Long? = null,
    val type: Int? = null,
    val date: Long,
    val note: String? = null,
    val images: List<String>? = null,
    val tags: List<String>? = null,
    var contactIds: List<String>? = null,
    var contacts: List<Contact>? = null,
    val contactsJson: String? = null,
    val walletData: SourceType? = null,
    val destWalletData: SourceType? = null
)