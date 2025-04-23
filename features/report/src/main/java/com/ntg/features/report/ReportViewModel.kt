package com.ntg.features.report

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.core.data.repository.CategoryRepository
import com.ntg.core.data.repository.WalletsRepository
import com.ntg.core.data.repository.transaction.TransactionsRepository
import com.ntg.core.model.Transaction
import com.ntg.core.model.res.Category
import com.ntg.core.mybudget.common.Constants
import com.ntg.core.mybudget.common.Constants.BudgetType
import com.ntg.core.mybudget.common.convertToFirstDay
import com.ntg.core.mybudget.common.getDayOfWeek
import com.ntg.core.mybudget.common.getLastWeekTransactions
import com.ntg.core.mybudget.common.getThisWeekTransactions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val walletsRepository: WalletsRepository,
    private val transactionsRepository: TransactionsRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private var selectedWalletIds = mutableStateListOf<Int>()
    private var transactions = mutableStateListOf<Transaction>()

    val incomeTransactions = mutableStateMapOf<Long, Long>()
    val expenseTransactions = mutableStateMapOf<Long, Long>()
    val avgTransaction = mutableStateMapOf<Long, Long>()

    val categories = mutableStateListOf<Category>()
    val topFourExpenseCategories = mutableStateMapOf<Int, Long>()

    val thisWeekState = mutableStateListOf<Int>()
    val lastWeekState = mutableStateListOf<Int>()


    init {
        getCategories()
        selectedWalletsIds()
    }

    private fun selectedWalletsIds() {
        viewModelScope.launch {
            walletsRepository.getSelectedWalletIds().collect{
                selectedWalletIds = it.toMutableStateList()
                getTransactions(it)
            }
        }
    }

    private fun getTransactions(sourceIds: List<Int>) {
        viewModelScope.launch {
            transactionsRepository.getTransactionsBySourceIds(sourceIds).collect {
                transactions.clear()
                transactions.addAll(it)

                getIncomeTransactions()
                getExpenseTransactions()
                getAvgTransaction()

                getTopFourExpenseCategories()

                getThisWeekTransactions()
                getLastWeekTransactions()
            }
        }
    }

    private fun getIncomeTransactions() {
        val incomes = transactions
            .filter { it.type == BudgetType.INCOME }
            .groupBy { convertToFirstDay(it.date, Constants.FilterTime.DAY) }
            .mapValues { (_, group) -> group.sumOf { it.amount } }

        incomeTransactions.clear()
        incomeTransactions.putAll(incomes)
    }

    private fun getExpenseTransactions() {
        val expenses = transactions
            .filter { it.type == BudgetType.EXPENSE }
            .groupBy { convertToFirstDay(it.date, Constants.FilterTime.DAY) }
            .mapValues { (_, group) -> group.sumOf { it.amount } }

        expenseTransactions.clear()
        expenseTransactions.putAll(expenses)
    }

    private fun getAvgTransaction() {
        val avg = transactions
            .groupBy { convertToFirstDay(it.date, Constants.FilterTime.DAY) }
            .mapValues { (_, group) -> group.map { it.amount }.average().toLong() }

        avgTransaction.clear()
        avgTransaction.putAll(avg)
    }

    private fun getCategories() {
        viewModelScope.launch {
            categoryRepository.getCategories().collect{
                categories.clear()
                categories.addAll(it.orEmpty())
            }
        }
    }

    private fun getTopFourExpenseCategories() {
        val topExpenseCategories = transactions
            .filter { it.type == BudgetType.EXPENSE }
            .groupBy { transaction ->
                transaction.categoryId ?: 0
            }
            .mapValues { (_, group) ->
                group.sumOf { it.amount }
            }.toList()
            .sortedByDescending { (_, amount) -> amount }
            .take(4)

        topFourExpenseCategories.clear()
        topFourExpenseCategories.putAll(topExpenseCategories)
    }

    private fun getThisWeekTransactions() {
        thisWeekState.addAll(getWeekState(getThisWeekTransactions(transactions)))
    }

    private fun getLastWeekTransactions() {
        lastWeekState.addAll(getWeekState(getLastWeekTransactions(transactions)))
    }

    private fun getWeekState(transactions: List<Transaction>): MutableList<Int> {
        val thisWeekState = MutableList(7) { BudgetType.NOTHING }

        val netSumByDate = transactions
            .groupBy { getDayOfWeek(it.date) }
            .mapValues { (_, group) ->
                group.sumOf { transaction ->
                    if (transaction.type == 0) -transaction.amount else transaction.amount
                }
            }

        netSumByDate.forEach { (dayOfWeek, netSum) ->
            val state = if (netSum > 0) BudgetType.INCOME else if (netSum < 0) BudgetType.EXPENSE else BudgetType.NOTHING
            thisWeekState[dayOfWeek] = state
        }

        return thisWeekState
    }
}