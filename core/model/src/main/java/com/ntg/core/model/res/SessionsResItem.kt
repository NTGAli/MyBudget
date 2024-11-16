package com.ntg.core.model.res

data class SessionsResItem(
    val device_name: String,
    val id: String,
    val ip_address: String,
    val is_current: Boolean,
    val last_used_at: String,
    val token_id: String,
    val user_id: String
)