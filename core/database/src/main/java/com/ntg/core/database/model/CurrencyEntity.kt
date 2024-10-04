package com.ntg.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ntg.core.model.res.Currency

@Entity(
    tableName = "currencies",
)
data class CurrencyEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val nativeName: String? = null,
    val enName: String? = null,
    val faName: String? = null,
    val symbol: String? = null,
    val country: String? = null,
    val countryAlpha2: String? = null,
    val countryAlpha3: String? = null,
    val isoCode: Int? = null,
    val precision: String? = null,
    val isCrypto: Int? = null
)


fun CurrencyEntity.toCurrency() =
    Currency(
        id = id,
        nativeName = nativeName,
        enName = enName,
        faName = faName,
        symbol = symbol,
        country = country,
        countryAlpha2 = countryAlpha2,
        countryAlpha3 = countryAlpha3,
        isoCode = isoCode,
        precision = precision,
        isCrypto = isCrypto
    )

fun Currency.toCurrencyEntity() =
    CurrencyEntity(
        id = id,
        nativeName = nativeName,
        enName = enName,
        faName = faName,
        symbol = symbol,
        country = country,
        countryAlpha2 = countryAlpha2,
        countryAlpha3 = countryAlpha3,
        isoCode = isoCode,
        precision = precision,
        isCrypto = isCrypto
    )