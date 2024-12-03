package com.ntg.features.report

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ntg.core.designsystem.components.AppBar
import com.ntg.core.designsystem.components.IncomeOutcomeChart
import com.ntg.core.designsystem.components.TwoWeekOverviewChart
import com.ntg.core.mybudget.common.Constants
import com.ntg.core.mybudget.common.Constants.BudgetType
import com.ntg.core.mybudget.common.convertToFirstDay
import com.ntg.mybudget.core.designsystem.R

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

    val walletIds =
        reportViewModel.selectedWalletsIds().collectAsStateWithLifecycle(initialValue = null)
    val transactions =
        reportViewModel.transactions(walletIds.value.orEmpty())
            .collectAsStateWithLifecycle(initialValue = null)


    val incomeTransactions = transactions
        .value?.filter { it.type == Constants.BudgetType.INCOME }.orEmpty()
        .groupBy { convertToFirstDay(it.date, Constants.FilterTime.DAY) }
        .mapValues { (_, group) -> group.sumOf { it.amount } }

    val expenseTransactions = transactions
        .value?.filter { it.type == Constants.BudgetType.EXPENSE }.orEmpty()
        .groupBy { convertToFirstDay(it.date, Constants.FilterTime.DAY) }
        .mapValues { (_, group) -> group.sumOf { it.amount } }

    val avgTransaction = transactions.value?.groupBy { convertToFirstDay(it.date, Constants.FilterTime.DAY) }
        .orEmpty().mapValues { (_, group) -> group.map { it.amount }.average().toLong() }


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
                .padding(padding)
        ) {
            if (incomeTransactions.isNotEmpty() && expenseTransactions.isNotEmpty() && avgTransaction.isNotEmpty()) {

                IncomeOutcomeChart(
                    incomeList = remember(incomeTransactions) { incomeTransactions.toMap() }.toMutableMap(),
                    outcomeList = remember(expenseTransactions) { expenseTransactions.toMap() }.toMutableMap(),
                    walletBalance = remember(avgTransaction) { avgTransaction.toMap() }.toMutableMap(),
                )
            }

//            Spacer(modifier = Modifier.height(16.dp))
//
//            if (incomeTransactions.isNotEmpty() && expenseTransactions.isNotEmpty() && avgTransaction.isNotEmpty()) {
//
//                ColumnsChart(
//                    outcomeList = remember(expenseTransactions) { expenseTransactions.toMap() }.toMutableMap(),
//                )
//            }

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