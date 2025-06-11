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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
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
import com.ntg.core.designsystem.components.WeekData
import com.ntg.core.mybudget.common.SharedViewModel
import com.ntg.mybudget.core.designsystem.R

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ReportRoute(
    sharedViewModel: SharedViewModel,
    reportViewModel: ReportViewModel = hiltViewModel()
) {
    sharedViewModel.bottomNavTitle.postValue(null)
    sharedViewModel.setExpand.postValue(false)

    val categories = reportViewModel.categories
    val donutChartColors = listOf(
        MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.error
    )
    val donutChartData = reportViewModel.topFourExpenseCategories.entries.mapIndexed { index, entry ->
        PieChartInput(
            color = donutChartColors[index],
//            brushPattern = R.drawable.default_pattern,
            value = entry.value,
            Title = categories.find { it.id == entry.key }?.name.orEmpty()
        )
    }

    // Get week data from the new StateFlows
    val thisWeekData by reportViewModel.thisWeekData.collectAsStateWithLifecycle()
    val previousWeekData by reportViewModel.previousWeekData.collectAsStateWithLifecycle()

    ReportScreen(
        reportViewModel.incomeTransactions,
        reportViewModel.expenseTransactions,
        reportViewModel.avgTransaction,
        donutChartData,
        thisWeekData = thisWeekData,
        previousWeekData = previousWeekData
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    incomeTransactions: SnapshotStateMap<Long, Long>,
    expenseTransactions: SnapshotStateMap<Long, Long>,
    avgTransaction: SnapshotStateMap<Long, Long>,
    donutChartData: List<PieChartInput>,
    thisWeekData: WeekData?,
    previousWeekData: WeekData?,
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
                ExpenseDonutChart(
                    donutChartData,
                    disableClick = true,
                    modifier = Modifier
                        .padding(top = 40.dp)
                        .padding(horizontal = 16.dp)
                )

            // Week State Chart with new implementation
            if (thisWeekData != null && previousWeekData != null) {
                TwoWeekOverviewChart(
                    thisWeekData = thisWeekData,
                    previousWeekData = previousWeekData,
                    modifier = Modifier.padding(
                        top = 32.dp,
                        start = 16.dp,
                        end = 16.dp
                    )
                )
            }
        }
    }
}