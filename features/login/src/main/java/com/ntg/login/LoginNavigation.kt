package com.ntg.login

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ntg.core.mybudget.common.SharedViewModel

const val Login_Route = "login_route"
const val Countries_Route = "countries_route"
const val Code_Route = "code_route"

const val PHONE = "phone"

fun NavController.navigateToLogin() {
    navigate(Login_Route)
}

fun NavController.navigateToCountries() {
    navigate(Countries_Route)
}

fun NavController.navigateToCode(phone: String) {
    val route = "$Code_Route/$phone"
    navigate(route)
}

fun NavGraphBuilder.loginScreen(
    loginViewModel: LoginViewModel,
    sharedViewModel: SharedViewModel,
    onShowSnackbar: suspend (Int, String?) -> Boolean,
    navigateToCountries: () -> Unit = {},
    navigateToCode: (String) -> Unit = {},
    navigateToSetup: () -> Unit = {},
    onBack: () -> Unit = {},
) {

    composable(
        route = Login_Route,
    ) {
        LoginRoute(
            sharedViewModel,
            navigateToDetail = navigateToCountries,
            navigateToCode = navigateToCode,
            onShowSnackbar = onShowSnackbar
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
        route = "$Code_Route/{$PHONE}",
        arguments = listOf(
            navArgument(PHONE) {
                type = NavType.StringType
            }
        ),
    ) {
        CodeRoute(
            it.arguments?.getString(PHONE).orEmpty(),
            navigateToSetup,
            onBack,
        )
    }
}
