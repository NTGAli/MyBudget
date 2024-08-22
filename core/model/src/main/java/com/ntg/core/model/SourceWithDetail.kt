package com.ntg.core.model

data class SourceWithDetail(
    val id: Int,
    val accountId: Int,
    val type: Int,
    val name: String,
    val sourceType: SourceType?,
    val bankId: Int?=null,
    val expire: Int? = null,
)

sealed class SourceType {
    data class BankCard (
        val id: Int,
        val sId: String? = null,
        var sourceId: Int? = null,
        val number: String,
        val date: String,
        val cvv: String? = null,
        val sheba: String? = null,
        val accountNumber: String? = null,
        val name: String,
        val isSynced: Boolean = false,
        val isDeleted: Boolean = false,
        val updatedAt: String? = null
    ) : SourceType()

    data class Gold(val value: Double, val weight: Double) : SourceType()
}