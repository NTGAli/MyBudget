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
const val Insert_Route = "insert_route"
const val Full_Image_Route = "full_screen_route"

const val Details_Arg = "details_arg"
const val Insert_Arg = "insert_arg"
const val Image_Arg = "image_arg"

fun NavController.navigateToHome(){
    navigate(Home_Route){
        popUpTo(0)
    }
}


fun NavController.navigateToTransaction(id: Int){
    navigate("$Details_Route/$id")
}

fun NavController.navigateToInsert(id: Int){
    navigate("$Insert_Route/$id")
}

fun NavController.navigateToImageFull(path: String){
    navigate("$Full_Image_Route/?path=$path")
}


fun NavGraphBuilder.homeScreen(
    sharedViewModel: SharedViewModel,
    navigateToSource: (id: Int, sourceId: Int?) -> Unit,
    navigateToAccount: (id: Int) -> Unit,
    navigateToDetail: (id: Int) -> Unit,
    navigateToEdit: (id: Int) -> Unit,
    navToImageFull: (path: String) -> Unit,
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
        DetailsRoute(tId = it.arguments?.getInt(Details_Arg), onBack = onBack, navToEdit = navigateToEdit, navToImageFull = navToImageFull)
    }

    composable(
        route = "$Insert_Route/{$Insert_Arg}",
        arguments = listOf(
            navArgument(Insert_Arg) {
                type = NavType.IntType
            }
        )
    ){
        InsertRoute(transactionId = it.arguments?.getInt(Insert_Arg),
            sharedViewModel = sharedViewModel,
            onBack = onBack, onShowSnackbar = onShowSnackbar)
    }

    composable(
        route = "$Full_Image_Route/?path={$Image_Arg}",
        arguments = listOf(
            navArgument(Image_Arg) {
                type = NavType.StringType
            }
        )
    ){
        FullScreenImageScreen(imagePath = it.arguments?.getString(Image_Arg) , onBack)
    }

}
