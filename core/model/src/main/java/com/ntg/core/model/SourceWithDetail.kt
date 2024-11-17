package com.ntg.core.model

data class SourceWithDetail(
    val id: Int,
    val accountSId: String? = null,
    val accountId: Int,
    val currencyId: Int? = null,
    val type: Int,
    val name: String? = null,
    val sourceType: SourceType?,
    val bankId: Int?=null,
    val expire: Int? = null,
)

sealed class SourceType {
    data class BankCard (
        val number: String,
        val date: String?=null,
        val cvv: String? = null,
        val sheba: String? = null,
        val accountNumber: String? = null,
        val name: String,
        val bankId: Int? = null,
        val nativeName: String? = null,
        val logoName: String? = null,
        val isDeleted: Boolean = false,
        val updatedAt: String? = null
    ) : SourceType()

    data class Gold(val value: Double, val weight: Double) : SourceType()
}