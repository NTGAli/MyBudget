package com.ntg.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ResponseBody<T>(
    val message: String? = null,
    val data:T? = null,
)
