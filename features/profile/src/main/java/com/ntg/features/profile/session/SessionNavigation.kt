package com.ntg.features.profile.session

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val Session_Route = "session_route"

fun NavController.navigateToSession() {

    navigate(Session_Route)

}

fun NavGraphBuilder.sessionScreen(
    onShowSnackbar: suspend (Int, String?) -> Boolean,
) {

    composable(route = Session_Route) {
        SessionRout(
            onShowSnackbar = onShowSnackbar
        )
    }
}