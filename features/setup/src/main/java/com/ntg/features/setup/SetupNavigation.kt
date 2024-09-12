package com.ntg.features.setup

import android.util.Log
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
const val SourceId_Arg = "accountId"

fun NavController.navigateToSetup() {
    navigate(Setup_Route)
}

fun NavController.navigateToSource(id: Int, sourceId: Int?) {
    val finalRoute = if (sourceId == null) "$Source_Route/$id"
    else "$Source_Route/$id/${sourceId}"
    navigate(finalRoute)
}

fun NavController.navigateToCreateAccount(id: Int?) {
    val finalRoute = "$Create_Account_Route/$id"
    navigate(finalRoute)
}


fun NavGraphBuilder.setupScreen(
    sharedViewModel: SharedViewModel,
    navigateToSource: (id: Int, sourceId: Int?) -> Unit,
    navigateToAccount: (id: Int) -> Unit,
    onBack:() -> Unit,
    onShowSnackbar: suspend (Int, String?) -> Boolean,
    navigateToLogin:(Boolean) -> Unit
) {

    composable(
        route = Setup_Route
    ) {
        SetupRoute(sharedViewModel, navigateToSource = navigateToSource, navigateToAccount = navigateToAccount, onShowSnackbar = onShowSnackbar, navigateToLogin = navigateToLogin)
    }


    composable(
        route = "$Source_Route/{$AccountId_Arg}/{$SourceId_Arg}?",
        arguments = listOf(
            navArgument(AccountId_Arg) {
                type = NavType.IntType
            },
            navArgument(SourceId_Arg) {
                type = NavType.IntType
            }
        )
    ) {
        SourceRoute(sharedViewModel, it.arguments?.getInt(AccountId_Arg) ?: 0, sourceId = it.arguments?.getInt(SourceId_Arg),onShowSnackbar = onShowSnackbar, onBack = onBack)
    }

    composable(
        route = "$Source_Route/{$AccountId_Arg}",
        arguments = listOf(
            navArgument(AccountId_Arg) {
                type = NavType.IntType
            }
        )
    ) {
        SourceRoute(sharedViewModel, it.arguments?.getInt(AccountId_Arg) ?: 0,onShowSnackbar = onShowSnackbar, onBack = onBack)
    }

    composable(
        route = "$Create_Account_Route/{$AccountId_Arg}",
        arguments = listOf(
            navArgument(AccountId_Arg) {
                type = NavType.IntType
            }
        )
    ) {
        CreateAccountRoute(sharedViewModel, id =  it.arguments?.getInt(AccountId_Arg) ?: 0, onShowSnackbar = onShowSnackbar, onBack=onBack)
    }

}