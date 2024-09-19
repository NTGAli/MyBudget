package com.ntg.core.model

data class UserData(
    val isLogged: Boolean,
    val token: String? = null,
    val expire: String?=null
)
