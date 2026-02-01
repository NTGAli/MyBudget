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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    @Dispatcher(BudgetDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val categoryDao: CategoryDao,
    private val network: BudgetNetworkDataSource
) : CategoryRepository {

    private val defaultCategories = listOf(
        // Expense categories (type = 0)
        CategoryEntity(id = 1, name = "خوراک", hint = "رستوران، سوپرمارکت، کافه", type = 0),
        CategoryEntity(id = 2, name = "حمل‌ونقل", hint = "بنزین، تاکسی، مترو، اتوبوس", type = 0),
        CategoryEntity(id = 3, name = "مسکن", hint = "اجاره، شارژ، تعمیرات", type = 0),
        CategoryEntity(id = 4, name = "قبوض", hint = "برق، آب، گاز، اینترنت، تلفن", type = 0),
        CategoryEntity(id = 5, name = "خرید", hint = "پوشاک، لوازم خانه، لوازم‌التحریر", type = 0),
        CategoryEntity(id = 6, name = "سلامت", hint = "دارو، دکتر، بیمارستان، بیمه درمانی", type = 0),
        CategoryEntity(id = 7, name = "تفریح", hint = "سینما، بازی، سفر، ورزش", type = 0),
        CategoryEntity(id = 8, name = "آموزش", hint = "کتاب، دوره، دانشگاه، کلاس", type = 0),
        CategoryEntity(id = 9, name = "اشتراک", hint = "اپلیکیشن، سرویس آنلاین", type = 0),
        CategoryEntity(id = 10, name = "هدیه و کمک", hint = "هدیه، صدقه، کمک مالی", type = 0),
        CategoryEntity(id = 11, name = "متفرقه", hint = "سایر هزینه‌ها", type = 0),

        // Income categories (type = 1)
        CategoryEntity(id = 101, name = "حقوق", hint = "حقوق ماهانه، دستمزد", type = 1),
        CategoryEntity(id = 102, name = "کار آزاد", hint = "پروژه، فریلنسری", type = 1),
        CategoryEntity(id = 103, name = "سرمایه‌گذاری", hint = "سود سهام، سود بانکی، اجاره", type = 1),
        CategoryEntity(id = 104, name = "هدیه", hint = "عیدی، هدیه نقدی", type = 1),
        CategoryEntity(id = 105, name = "فروش", hint = "فروش کالا، لوازم دست دوم", type = 1),
        CategoryEntity(id = 106, name = "سایر درآمد", hint = "بازپرداخت، جایزه، پاداش", type = 1),
    )

    override suspend fun getCategories(): Flow<List<Category>> {
        CoroutineScope(ioDispatcher).launch {
            try {
                val existing = categoryDao.getAll().flowOn(ioDispatcher).first()
                if (existing.isEmpty()) {
                    categoryDao.upsert(defaultCategories)
                }
            } catch (_: Exception) {
            }
        }

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