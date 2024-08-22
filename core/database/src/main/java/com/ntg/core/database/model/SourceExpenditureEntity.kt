package com.ntg.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ntg.core.model.SourceExpenditure

@Entity("sourceExpenditures")
data class SourceExpenditureEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val sId: String? = null,
    val accountId: Int,
    val type: Int? = null,
    val name: String? = null,
    val icon: String? = null,
    val symbol: String? = null,
    val isoCode: String? = null,
    val precision: Int = 0,
    val isSelected: Boolean,
    val isCrypto: Boolean = false,
    val isSynced: Boolean = false,
    val isRemoved: Boolean = false,
    val dateCreated: String,
)

fun SourceExpenditureEntity.asSource() = SourceExpenditure(
    id = id,
    sId = sId,
    symbol = symbol,
    isoCode = isoCode,
    precision = precision,
    isCrypto = isCrypto,
    isSynced = isSynced,
    dateCreated = dateCreated,
    isSelected = isSelected,
    accountId = accountId,
    icon = icon,
    type = type,
    name = name
)


fun SourceExpenditure.toEntity() = SourceExpenditureEntity(
    id = id,
    sId = sId,
    symbol = symbol,
    isoCode = isoCode,
    precision = precision,
    isCrypto = isCrypto,
    isSynced = isSynced,
    dateCreated = dateCreated,
    isSelected = isSelected,
    accountId = accountId,
    icon = icon,
    type = type,
    name = name
)