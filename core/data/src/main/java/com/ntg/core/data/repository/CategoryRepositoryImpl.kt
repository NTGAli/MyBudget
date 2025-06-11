package com.ntg.core.data.repository

import com.ntg.core.database.dao.CategoryDao
import com.ntg.core.database.model.CategoryEntity
import com.ntg.core.database.model.toCategory
import com.ntg.core.database.model.toEntity
import com.ntg.core.model.res.Category
import com.ntg.core.mybudget.common.BudgetDispatchers
import com.ntg.core.mybudget.common.Dispatcher
import com.ntg.core.network.BudgetNetworkDataSource
import com.ntg.core.network.model.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    @Dispatcher(BudgetDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val categoryDao: CategoryDao,
    private val network: BudgetNetworkDataSource
) : CategoryRepository {

    override suspend fun getCategories(): Flow<List<Category>> {
        CoroutineScope(ioDispatcher).launch {
            try {
                network.categories().collect { result ->
                    if (result is Result.Success) {
                        categoryDao.upsert(result.data?.expense.orEmpty().map { it.toEntity(0) })
                        categoryDao.upsert(result.data?.income.orEmpty().map { it.toEntity(1) })
                    }
                }
            } catch (_: Exception) {
            }
        }

        return  categoryDao.getAll()
            .map { it.map { it.toCategory() } }
            .flowOn(ioDispatcher)
    }
}