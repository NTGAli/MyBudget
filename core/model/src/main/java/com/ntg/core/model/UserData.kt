package com.ntg.core.model

data class UserData(
    val isLogged: Boolean,
    val token: String? = null,
    val expire: String? = null,
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val avatarImage: String? = null
)
