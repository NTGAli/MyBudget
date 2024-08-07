package com.ntg.login

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ntg.core.mybudget.common.SharedViewModel

const val Login_Route = "login_route"
const val Countries_Route = "countries_route"

fun NavController.navigateToLogin(){
    navigate(Login_Route)
}

fun NavController.navigateToCountries(){
    navigate(Countries_Route)
}

fun NavGraphBuilder.loginScreen(
    loginViewModel: LoginViewModel,
    sharedViewModel: SharedViewModel,
    navigateToCountries: () -> Unit = {},
    onBack: () -> Unit = {}
)
{

    composable(
        route = Login_Route,
    ) {
        LoginRoute(
            loginViewModel,
            sharedViewModel,
            navigateToDetail = navigateToCountries
        )
    }

    composable(
        route = Countries_Route,
    ) {
        CountriesRoute(
            loginViewModel,
            onBack
        )
    }
}
