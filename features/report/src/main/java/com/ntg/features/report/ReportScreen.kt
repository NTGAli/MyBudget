package com.ntg.features.report

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ntg.core.designsystem.components.AppBar
import com.ntg.core.designsystem.components.ExpenseDonutChart
import com.ntg.core.designsystem.components.IncomeOutcomeChart
import com.ntg.core.designsystem.components.PieChartInput
import com.ntg.core.designsystem.components.TwoWeekOverviewChart
import com.ntg.mybudget.core.designsystem.R

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ReportRoute(
    reportViewModel: ReportViewModel = hiltViewModel()
) {

    val categories = reportViewModel.categories
    val donutChartColors = listOf(
        MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.error
    )
    val donutChartData = reportViewModel.topFourExpenseCategories.entries.mapIndexed { index, entry ->
        PieChartInput(
            color = donutChartColors[index],
            brushPattern = R.drawable.default_pattern,
            value = entry.value, // Amount
            Title = categories.find { it.id  == entry.key }?.name.orEmpty()
        )
    }

    ReportScreen(
        reportViewModel.incomeTransactions,
        reportViewModel.expenseTransactions,
        reportViewModel.avgTransaction,
        donutChartData,
        reportViewModel.thisWeekState,
        reportViewModel.lastWeekState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    incomeTransactions: SnapshotStateMap<Long, Long>,
    expenseTransactions: SnapshotStateMap<Long, Long>,
    avgTransaction: SnapshotStateMap<Long, Long>,
    donutChartData: List<PieChartInput>,
    thisWeekState: MutableList<Int>,
    lastWeekState: MutableList<Int>,
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
                    incomeList = incomeTransactions,
                    outcomeList = expenseTransactions,
                    walletBalance = avgTransaction,
                )
            }

            // Expense transaction with category
            if (donutChartData.isNotEmpty())
                ExpenseDonutChart(donutChartData, disableClick = true, modifier = Modifier.padding(top = 40.dp))

            // Week State Chart
            if (thisWeekState.isNotEmpty()  && lastWeekState.isNotEmpty()) {
                TwoWeekOverviewChart(
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                    thisWeekState = thisWeekState,
                    previousWeek = lastWeekState
                )
            }
        }
    }
}