package com.ntg.features.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ntg.core.mybudget.common.SharedViewModel

const val Home_Route = "home_route"

fun NavController.navigateToHome(){
    navigate(Home_Route){
        popUpTo(0)
    }
}


fun NavGraphBuilder.homeScreen(
    sharedViewModel: SharedViewModel
){

    composable(
        route = Home_Route
    ){
        HomeRoute(
            sharedViewModel
        )
    }


}
