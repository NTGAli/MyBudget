package com.ntg.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.ntg.core.database.model.CategoryEntity

@Dao
interface CategoryDao {

    @Insert
    suspend fun insert(categoryEntity: CategoryEntity)

    @Insert
    suspend fun insertAll(categoryEntities : List<CategoryEntity>)

    @Query("SELECT * FROM category_table")
    fun getAll(): List<CategoryEntity>

    @Upsert
    fun upsert(map: List<CategoryEntity>)

}