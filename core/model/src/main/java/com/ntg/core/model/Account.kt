package com.ntg.core.model

data class Account(
    val id: Int,
    val sId: String? = null,
    val name: String,
    val isSelected: Boolean = false,
    val isSynced: Boolean = false,
    val dateCreated: String
)
