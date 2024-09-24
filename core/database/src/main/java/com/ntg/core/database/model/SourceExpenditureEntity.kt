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
    val icon: String? = null,
    val currencyId: Int? = null,
    val isSelected: Boolean,
    val isSynced: Boolean = false,
    val isRemoved: Boolean = false,
    val dateCreated: String,
)

fun SourceExpenditureEntity.asSource() = SourceExpenditure(
    id = id,
    sId = sId,
    isSynced = isSynced,
    dateCreated = dateCreated,
    isSelected = isSelected,
    accountId = accountId,
    currencyId = currencyId,
    icon = icon,
    type = type,
)


fun SourceExpenditure.toEntity() = SourceExpenditureEntity(
    id = id,
    sId = sId,
    isSynced = isSynced,
    dateCreated = dateCreated,
    isSelected = isSelected,
    accountId = accountId,
    currencyId = currencyId,
    icon = icon,
    type = type,
)