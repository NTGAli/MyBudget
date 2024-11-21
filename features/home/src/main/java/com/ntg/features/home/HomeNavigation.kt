package com.ntg.features.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ntg.core.mybudget.common.SharedViewModel
import com.ntg.core.mybudget.common.logd

const val Home_Route = "home_route"
const val Details_Route = "details_route"

const val Details_Arg = "details_arg"

fun NavController.navigateToHome(){
    navigate(Home_Route){
        popUpTo(0)
    }
}


fun NavController.navigateToTransaction(id: Int){
    navigate("$Details_Route/$id")
}


fun NavGraphBuilder.homeScreen(
    sharedViewModel: SharedViewModel,
    navigateToSource: (id: Int, sourceId: Int?) -> Unit,
    navigateToAccount: (id: Int) -> Unit,
    navigateToDetail: (id: Int) -> Unit,
    onShowSnackbar: suspend (Int, String?, Int?) -> Boolean,
    navigateToProfile: () -> Unit,
    onBack: () -> Unit,
){

    composable(
        route = Home_Route
    ){
        HomeRoute(
            sharedViewModel = sharedViewModel,
            navigateToSource = navigateToSource,
            navigateToAccount = navigateToAccount,
            navigateToDetail = navigateToDetail,
            navigateToProfile = navigateToProfile,
            onShowSnackbar = onShowSnackbar
        )
    }

    composable(
        route = "$Details_Route/{$Details_Arg}",
        arguments = listOf(
            navArgument(Details_Arg) {
                type = NavType.IntType
            }
        )
    ){
        DetailsRoute(tId = it.arguments?.getInt(Details_Arg), onBack = onBack)
    }

}
