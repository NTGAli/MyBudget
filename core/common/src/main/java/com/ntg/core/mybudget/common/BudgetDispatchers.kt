package com.ntg.core.mybudget.common

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val budgetDispatcher: BudgetDispatchers)

enum class BudgetDispatchers {
    Default,
    IO,
}
