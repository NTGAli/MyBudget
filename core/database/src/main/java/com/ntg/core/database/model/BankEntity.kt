package com.ntg.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "banks"
)
data class BankEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val sId: String? = null,
    val enName: String? = null,
    val nativeName: String? = null,
    val logoName: String? = null,
    val countryAlpha2: String? = null,
    val countryAlpha3: String? = null,
    val bind: List<String?>? = null,
    val colorLogo: String? = null,
    val monoLogo: String? = null,
    val updatedAt: String? = null,
    val createdAt: String? = null
)
