package com.ntg.core.model

data class SourceWithDetail(
    val id: Int,
    val accountId: Int,
    val type: Int,
    val name: String,
    val sourceType: SourceType?
)

sealed class SourceType {
    data class BankCardSource(val cardNumber: String, val cvv: String, val expire: String) : SourceType()
    data class Gold(val value: Double, val weight: Double) : SourceType()
}