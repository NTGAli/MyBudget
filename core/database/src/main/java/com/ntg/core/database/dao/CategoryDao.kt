package com.ntg.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.ntg.core.database.model.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert
    suspend fun insert(categoryEntity: CategoryEntity)

    @Insert
    suspend fun insertAll(categoryEntities : List<CategoryEntity>)

    @Query("SELECT * FROM category_table")
    fun getAll(): Flow<List<CategoryEntity>>

    @Upsert
    suspend fun upsert(map: List<CategoryEntity>)

}