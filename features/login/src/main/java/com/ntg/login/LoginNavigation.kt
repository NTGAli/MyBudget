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

fun NavController.navigateToLogin(removeBackStack: Boolean = false) {
    if (removeBackStack) {
        navigate(Login_Route) {
            popUpTo(0) {
                inclusive = true
            }
            launchSingleTop = true
        }
    } else {
        navigate(Login_Route)
    }
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
    onShowSnackbar: suspend (Int, String?, Int?) -> Boolean,
    navigateToCountries: () -> Unit = {},
    navigateToCode: (String) -> Unit = {},
    navigateToSetup: () -> Unit = {},
    onBack: () -> Unit = {},
    finishLogin: (String) -> Unit = {},
) {

    composable(
        route = Login_Route,
    ) {
        loginViewModel.sendCode("")
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
            onBack,
            finishLogin = finishLogin,
            onShowSnackbar = onShowSnackbar
        )
    }
}
