package com.ntg.features.setup

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ntg.core.mybudget.common.SharedViewModel

const val Setup_Route = "SetupRoute"
const val Source_Route = "SourceRoute"
const val Create_Account_Route = "CreateAccountRoute"

const val AccountId_Arg = "accountId"

fun NavController.navigateToSetup() {
    navigate(Setup_Route)
}

fun NavController.navigateToSource(id: Int) {
    val finalRoute = "$Source_Route/$id"
    navigate(finalRoute)
}

fun NavController.navigateToCreateAccount(id: Int?) {
    val finalRoute = "$Create_Account_Route/$id"
    navigate(finalRoute)
}


fun NavGraphBuilder.setupScreen(
    sharedViewModel: SharedViewModel,
    navigateToSource: (id: Int) -> Unit
) {

    composable(
        route = Setup_Route
    ) {
        SetupRoute(sharedViewModel, navigateToSource = navigateToSource)
    }


    composable(
        route = "$Source_Route/{$AccountId_Arg}",
        arguments = listOf(
            navArgument(AccountId_Arg) {
                type = NavType.IntType
            }
        )
    ) {
        SourceRoute(sharedViewModel, it.arguments?.getInt(AccountId_Arg) ?: 0)
    }

    composable(
        route = "$Create_Account_Route/{$AccountId_Arg}",
        arguments = listOf(
            navArgument(AccountId_Arg) {
                type = NavType.IntType
            }
        )
    ) {
        CreateAccountRoute(sharedViewModel, id =  it.arguments?.getInt(AccountId_Arg) ?: 0)
    }

}