package com.ntg.core.data.repository

import com.ntg.core.model.res.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    suspend fun getCategories(): Flow<List<Category>>

}