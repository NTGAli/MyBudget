package com.ntg.login

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ntg.core.mybudget.common.SharedViewModel

const val Login_Route = "login_route"
const val Countries_Route = "countries_route"
const val Code_Route = "code_route"

fun NavController.navigateToLogin(){
    navigate(Login_Route)
}

fun NavController.navigateToCountries(){
    navigate(Countries_Route)
}

fun NavController.navigateToCode(){
    navigate(Code_Route)
}

fun NavGraphBuilder.loginScreen(
    loginViewModel: LoginViewModel,
    sharedViewModel: SharedViewModel,
    navigateToCountries: () -> Unit = {},
    navigateToCode: () -> Unit = {},
    onBack: () -> Unit = {}
)
{

    composable(
        route = Login_Route,
    ) {
        LoginRoute(
            loginViewModel,
            sharedViewModel,
            navigateToDetail = navigateToCountries,
            navigateToCode = navigateToCode
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

    composable(
        route = Code_Route,
    ) {
        CodeRoute(
            onBack
        )
    }
}
