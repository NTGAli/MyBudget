package com.ntg.core.model.req

data class SourceDetailReq(
    val cart_number: String? = null,
    val bank_id: String? = null,
    val bank_name: String? = null,
    val cart_owner_name: String? = null,
    val cvv2: String? = null,
)

