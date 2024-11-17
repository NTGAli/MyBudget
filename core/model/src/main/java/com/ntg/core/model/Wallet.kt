package com.ntg.core.model

data class Wallet(
    val id: Int,
    val sId: String? = null,
    val type: Int? = null,
    var accountId: Int,
    var accountSId: String? = null,
    val icon: String?=null,
    val currencyId:Int?= null,
    val isSelected: Boolean,
    var isSynced: Boolean = false,
    var data: SourceType? = null,
    val isRemoved: Boolean? = null,
    val dateCreated: String = "",
)
