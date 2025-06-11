package com.ntg.features.report

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.core.data.repository.CategoryRepository
import com.ntg.core.data.repository.WalletsRepository
import com.ntg.core.data.repository.transaction.TransactionsRepository
import com.ntg.core.designsystem.components.WeekData
import com.ntg.core.designsystem.components.convertLegacyWeekState
import com.ntg.core.designsystem.components.transactionsToWeekData
import com.ntg.core.model.Transaction
import com.ntg.core.model.res.Category
import com.ntg.core.mybudget.common.Constants
import com.ntg.core.mybudget.common.Constants.BudgetType
import com.ntg.core.mybudget.common.convertToFirstDay
import com.ntg.core.mybudget.common.getDayOfWeek
import com.ntg.core.mybudget.common.getLastWeekTransactions
import com.ntg.core.mybudget.common.getStartOfPreviousWeek
import com.ntg.core.mybudget.common.getStartOfWeek
import com.ntg.core.mybudget.common.getThisWeekTransactions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _thisWeekData = MutableStateFlow<WeekData?>(null)
    val thisWeekData: StateFlow<WeekData?> = _thisWeekData

    private val _previousWeekData = MutableStateFlow<WeekData?>(null)
    val previousWeekData: StateFlow<WeekData?> = _previousWeekData


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

                // Update week data with new approach
                updateWeekData()
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
            .filter { it.type == BudgetType.EXPENSE || it.type == BudgetType.INCOME }
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
            .filter { it.type == BudgetType.EXPENSE && it.categoryId != -1 }
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

    private fun generateWeekData() {
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            val thisWeekStart = getStartOfWeek(currentTime)
            val previousWeekStart = getStartOfPreviousWeek(thisWeekStart)

            // Get transactions for this week
            val thisWeekTransactions = getTransactionsForWeek(thisWeekStart)
            val previousWeekTransactions = getTransactionsForWeek(previousWeekStart)

            // Convert to WeekData format
            _thisWeekData.value = transactionsToWeekData(
                transactions = thisWeekTransactions,
                weekTitle = "این هفته",
                startDate = thisWeekStart
            )

            _previousWeekData.value = transactionsToWeekData(
                transactions = previousWeekTransactions,
                weekTitle = "هفته قبل",
                startDate = previousWeekStart
            )
        }
    }

    /**
     * Get transactions for a specific week
     */
    private fun getTransactionsForWeek(weekStartTimestamp: Long): List<Transaction> {
        val weekEndTimestamp = weekStartTimestamp + (7 * 24 * 60 * 60 * 1000) - 1

        return transactions.filter { transaction ->
            transaction.date >= weekStartTimestamp && transaction.date <= weekEndTimestamp
        }
    }

    /**
     * Alternative method if you want to keep the old format for backward compatibility
     */
    private fun getThisWeekDataLegacy() {
        val thisWeekTransactions = getThisWeekTransactions(transactions)
        val thisWeekStart = getStartOfWeek(System.currentTimeMillis())

        _thisWeekData.value = convertLegacyWeekState(
            weekState = getWeekState(thisWeekTransactions).toMutableList(),
            weekTitle = "این هفته",
            startDate = thisWeekStart
        )
    }

    private fun getLastWeekDataLegacy() {
        val lastWeekTransactions = getLastWeekTransactions(transactions)
        val previousWeekStart = getStartOfPreviousWeek(getStartOfWeek(System.currentTimeMillis()))

        _previousWeekData.value = convertLegacyWeekState(
            weekState = getWeekState(lastWeekTransactions).toMutableList(),
            weekTitle = "هفته قبل",
            startDate = previousWeekStart
        )
    }

    private fun updateWeekData() {
        generateWeekData()
        // getThisWeekDataLegacy()
        // getLastWeekDataLegacy()
    }

}