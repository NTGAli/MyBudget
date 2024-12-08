package com.ntg.features.report

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ntg.core.designsystem.components.AppBar
import com.ntg.core.designsystem.components.ExpenseDonutChart
import com.ntg.core.designsystem.components.IncomeOutcomeChart
import com.ntg.core.designsystem.components.PieChartInput
import com.ntg.core.designsystem.components.TwoWeekOverviewChart
import com.ntg.core.mybudget.common.Constants
import com.ntg.core.mybudget.common.Constants.BudgetType
import com.ntg.core.mybudget.common.convertToFirstDay
import com.ntg.mybudget.core.designsystem.R

@Composable
fun ReportRoute(
    reportViewModel: ReportViewModel = hiltViewModel()
) {

    val walletIds = reportViewModel.selectedWalletsIds().collectAsStateWithLifecycle(initialValue = null)
    val transactions = reportViewModel.transactions(walletIds.value.orEmpty()).collectAsStateWithLifecycle(initialValue = null)

    val incomeTransactions = transactions
        .value?.filter { it.type == BudgetType.INCOME }.orEmpty()
        .groupBy { convertToFirstDay(it.date, Constants.FilterTime.DAY) }
        .mapValues { (_, group) -> group.sumOf { it.amount } }


    val expenseTransactions = transactions
        .value?.filter { it.type == BudgetType.EXPENSE }.orEmpty()
        .groupBy { convertToFirstDay(it.date, Constants.FilterTime.DAY) }
        .mapValues { (_, group) -> group.sumOf { it.amount } }


    val avgTransaction = transactions.value?.groupBy { convertToFirstDay(it.date, Constants.FilterTime.DAY) }
        .orEmpty().mapValues { (_, group) -> group.map { it.amount }.average().toLong() }


    val categories by reportViewModel.getCategories().collectAsStateWithLifecycle(initialValue = emptyList())
    val expenseTransactionsWithCategory = transactions
        .value?.filter { it.type == BudgetType.EXPENSE }.orEmpty()
        .groupBy { transaction ->
            transaction.categoryId ?: 0
        }
        .mapValues { (_, group) ->
            group.sumOf { it.amount }
        }


    val donutChartData = expenseTransactionsWithCategory.map { (categoryId, amount) ->
        PieChartInput(
            color = MaterialTheme.colorScheme.primary,// Temporary placeholder color
            brushPattern = R.drawable.default_pattern,
            value = amount,
            Title = categories?.find { it.id  == categoryId }?.name.orEmpty()
        )
    }.sortedByDescending { it.value }
        .take(4)
        .mapIndexed { index, pieChartInput ->
            val color = when (index) {
                0 -> MaterialTheme.colorScheme.primary
                1 -> MaterialTheme.colorScheme.secondary
                2 -> MaterialTheme.colorScheme.tertiary
                else -> MaterialTheme.colorScheme.error
            }
            pieChartInput.copy(color = color)
        }

    ReportScreen(
        incomeTransactions,
        expenseTransactions,
        avgTransaction,
        donutChartData
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    incomeTransactions: Map<Long, Long>,
    expenseTransactions: Map<Long, Long>,
    avgTransaction: Map<Long, Long>,
    donutChartData: List<PieChartInput>
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppBar(
                enableNavigation = false,
                title = stringResource(id = R.string.Report),
                scrollBehavior = scrollBehavior
            )
        },
    ) { padding ->

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {

            // Income Outcome Chart
            if (incomeTransactions.isNotEmpty() && expenseTransactions.isNotEmpty() && avgTransaction.isNotEmpty()) {
                IncomeOutcomeChart(
                    incomeList = incomeTransactions.toMutableMap(),
                    outcomeList = expenseTransactions.toMutableMap(),
                    walletBalance = avgTransaction.toMutableMap(),
                )
            }

            // Expense transaction with category
            if (donutChartData.isNotEmpty())
                ExpenseDonutChart(donutChartData, disableClick = true, modifier = Modifier.padding(top = 40.dp))


            val thisWeekState = remember { listOf(BudgetType.INCOME, BudgetType.EXPENSE, BudgetType.EXPENSE, BudgetType.NOTHING, BudgetType.NOTHING, BudgetType.NOTHING, BudgetType.NOTHING) }
            val previousWeek = remember { listOf(BudgetType.INCOME, BudgetType.NOTHING, BudgetType.NOTHING, BudgetType.NOTHING, BudgetType.EXPENSE, BudgetType.EXPENSE, BudgetType.EXPENSE,) }

            TwoWeekOverviewChart(
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                thisWeekState = thisWeekState,
                previousWeek = previousWeek
            )
        }
    }
}