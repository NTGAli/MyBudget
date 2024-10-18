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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    @Dispatcher(BudgetDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val categoryDao: CategoryDao,
    private val network: BudgetNetworkDataSource
) : CategoryRepository {
    override suspend fun getCategories(): Flow<List<Category>?> {
        network.categories().collect{
            if (it is Result.Success){
                categoryDao.upsert(it.data?.expense.orEmpty().map { it.toEntity(0)})
                categoryDao.upsert(it.data?.income.orEmpty().map { it.toEntity(1)})
            }
        }
        return  flow {
            emit(
                categoryDao.getAll()
                    .map(CategoryEntity::toCategory)
            )
        }
            .flowOn(ioDispatcher)
    }
}