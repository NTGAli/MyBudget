package com.ntg.core.model.req

data class VerifyOtp (
    val query: String,
    val otp: Int
)