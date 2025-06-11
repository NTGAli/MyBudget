package com.ntg.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ntg.core.model.res.Category

@Entity(
    tableName = "category_table"
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String?= null,
    val hint: String?= null,
    val type: Int = 0
)


fun CategoryEntity.toCategory() =
    Category(
        id = id,
        name = name.orEmpty(),
        hint = hint.orEmpty(),
        type = type
    )


fun Category.toEntity(type: Int) =
    CategoryEntity(
        id = id,
        name = name,
        hint = hint,
        type = type
    )