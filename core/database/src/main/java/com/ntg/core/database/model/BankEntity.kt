package com.ntg.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ntg.core.model.res.Bank

@Entity(
    tableName = "banks"
)
data class BankEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val enName: String? = null,
    val nativeName: String? = null,
    val logoName: String? = null,
    val countryAlpha2: String? = null,
    val countryAlpha3: String? = null,
    val bin: List<String>? = null,
    val colorLogo: String? = null,
    val monoLogo: String? = null,
    val updatedAt: String? = null,
    val createdAt: String? = null
)


fun BankEntity.toBank() =
    Bank(
        id = id,
        enName = enName.orEmpty(),
        nativeName = nativeName.orEmpty(),
        logoName = logoName.orEmpty(),
        countryAlpha2 = countryAlpha2.orEmpty(),
        countryAlpha3 = countryAlpha3.orEmpty(),
        bin = bin,
        updatedAt = updatedAt.orEmpty(),
        createdAt = createdAt.orEmpty()
    )


fun Bank.toEntity() =
    BankEntity(
        id = id ?: -1,
        enName = enName,
        nativeName = nativeName,
        logoName = logoName,
        countryAlpha2 = countryAlpha2,
        countryAlpha3 = countryAlpha3,
        bin = bin,
        updatedAt = updatedAt,
        createdAt = createdAt
    )