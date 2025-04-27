package com.ntg.features.report

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ntg.core.mybudget.common.SharedViewModel

const val Report_ROUTE = "report_route"

fun NavController.navigateToReport() {
    navigate(Report_ROUTE)
}

fun NavGraphBuilder.reportScreen(
    sharedViewModel: SharedViewModel,
) {
    composable(Report_ROUTE) {
        ReportRoute(
            sharedViewModel = sharedViewModel
        )
    }
}