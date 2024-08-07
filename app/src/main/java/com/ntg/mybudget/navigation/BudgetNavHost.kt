package com.ntg.mybudget.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import com.ntg.core.mybudget.common.SharedViewModel
import com.ntg.login.LoginViewModel
import com.ntg.login.Login_Route
import com.ntg.login.loginScreen
import com.ntg.login.navigateToCountries
import com.ntg.mybudget.ui.BudgetAppState

@Composable
fun BudgetNavHost(
    appState: BudgetAppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
    startDestination: String = Login_Route,
    sharedViewModel: SharedViewModel
) {
    val navController = appState.navController
    val loginViewModel: LoginViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {

        loginScreen(
            loginViewModel,
            sharedViewModel,
            navigateToCountries = navController::navigateToCountries,
            onBack = navController::popBackStack
        )

    }
}
