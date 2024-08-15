package com.ntg.core.model

data class BankCard (
    val id: Int,
    val sId: String? = null,
    var sourceId: Int? = null,
    val number: String,
    val date: String,
    val cvv: String? = null,
    val name: String,
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,
    val updatedAt: String
)