package com.ntg.features.report

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val Report_ROUTE = "report_route"

fun NavController.navigateToReport() {
    navigate(Report_ROUTE)
}

fun NavGraphBuilder.reportScreen() {
    composable(Report_ROUTE) {
        ReportRoute()
    }
}