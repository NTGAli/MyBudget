package com.ntg.features.setup

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ntg.core.mybudget.common.SharedViewModel

const val Setup_Route = "SetupRoute"
const val Source_Route = "SetupRoute"

fun NavController.navigateToSetup() {
    navigate(Setup_Route)
}

fun NavController.navigateToSource() {
    navigate(Source_Route)
}


fun NavGraphBuilder.setupScreen(
    sharedViewModel: SharedViewModel,
    navigateToSource: () -> Unit
) {

    composable(
        route = Setup_Route
    ) {
        ScreenRoute(sharedViewModel, navigateToSource = navigateToSource)
    }


    composable(
        route = Source_Route
    ) {
        SourceRoute(sharedViewModel)
    }

}