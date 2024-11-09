package com.ntg.core.model.res

data class UserInfo(
    val id: String,
    val full_name: String?,
    val email: String?,
    val avatar_url: String,
    val phone: String,

    // do not need these fields now
//    val avatar: Any,
//    val email_verified_at: String?,
//    val updated_at: String,
//    val username: String?,
//    val created_at: String,
)