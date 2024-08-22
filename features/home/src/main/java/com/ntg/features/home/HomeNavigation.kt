package com.ntg.features.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val Home_Route = "home_route"

fun NavController.navigateToHome(){
    navigate(Home_Route)
}


fun NavGraphBuilder.homeScreen(){

    composable(
        route = Home_Route
    ){
        HomeRoute()
    }


}
