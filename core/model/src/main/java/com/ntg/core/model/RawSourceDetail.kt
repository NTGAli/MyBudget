package com.ntg.core.model

data class RawSourceDetail(
    val id: Int,
    val accountId: Int,
    val type: Int,
    val name: String,
    val number: String?,
    val cvv: String?,
    val date: String?,
    val value: Double?,
    val weight: Double?
)