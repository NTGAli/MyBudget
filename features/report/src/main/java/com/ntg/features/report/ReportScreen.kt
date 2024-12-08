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
import kotlin.random.Random

@Composable
fun ReportRoute() {

    ReportScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    reportViewModel: ReportViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

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

    val categoryExpenseTransactions = transactions
        .value?.filter { it.type == BudgetType.EXPENSE }.orEmpty()
        .groupBy { transaction ->
            transaction.categoryId ?: 0
        }
        .mapValues { (_, group) ->
            group.sumOf { it.amount }
        }

    val categories by reportViewModel.getCategories().collectAsStateWithLifecycle(initialValue = emptyList())


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
                    incomeList = remember(incomeTransactions) { incomeTransactions.toMap() }.toMutableMap(),
                    outcomeList = remember(expenseTransactions) { expenseTransactions.toMap() }.toMutableMap(),
                    walletBalance = remember(avgTransaction) { avgTransaction.toMap() }.toMutableMap(),
                )
            }
            Spacer(modifier = Modifier.height(40.dp))



            val data = categoryExpenseTransactions.map { (categoryId, amount) ->
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

            if ( data.isNotEmpty())  ExpenseDonutChart(data, disableClick = true)


            Spacer(modifier = Modifier.height(16.dp))
            val thisWeekState = remember { listOf(BudgetType.INCOME, BudgetType.EXPENSE, BudgetType.EXPENSE, BudgetType.NOTHING, BudgetType.NOTHING, BudgetType.NOTHING, BudgetType.NOTHING) }
            val previousWeek = remember { listOf(BudgetType.INCOME, BudgetType.NOTHING, BudgetType.NOTHING, BudgetType.NOTHING, BudgetType.EXPENSE, BudgetType.EXPENSE, BudgetType.EXPENSE,) }

            TwoWeekOverviewChart(
                modifier = Modifier.padding(horizontal = 16.dp),
                thisWeekState = thisWeekState,
                previousWeek = previousWeek
            )
        }
    }
}

fun generateRandomColor(): Int {
    val red = Random.nextInt(256) // Generate a random value for red (0-255)
    val green = Random.nextInt(256) // Generate a random value for green (0-255)
    val blue = Random.nextInt(256) // Generate a random value for blue (0-255)
    return android.graphics.Color.rgb(red, green, blue) // Combine into a color
}