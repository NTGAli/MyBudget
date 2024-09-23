package com.ntg.core.model

data class SourceExpenditure(
    val id: Int,
    val sId: String? = null,
    val type: Int? = null,
    var accountId: Int,
    val icon: String?=null,
    val currencyId:Int?= null,
    val isSelected: Boolean,
    val isSynced: Boolean = false,
    val dateCreated: String,
)
