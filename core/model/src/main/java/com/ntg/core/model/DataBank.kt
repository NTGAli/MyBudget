package com.ntg.core.model

import androidx.annotation.Keep

@Keep
data class DataBank(
    val card_no: Long,
    val bank_name: String,
    val bank_title: String,
    val bank_logo: String,
    val color: String
)
