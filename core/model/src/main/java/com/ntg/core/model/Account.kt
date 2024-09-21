package com.ntg.core.model

data class Account(
    var id: Int,
    var sId: String? = null,
    var name: String,
    val isDefault: Boolean = false,
    var isSelected: Boolean = false,
    var isSynced: Boolean = false,
    var dateCreated: String? = null,
    var dateModified: String? = null ,
)
