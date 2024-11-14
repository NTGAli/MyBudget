package com.ntg.features.profile.session

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val Session_Route = "session_route"

fun NavController.navigateToSession() {

    navigate(Session_Route)

}

fun NavGraphBuilder.sessionScreen() {

    composable(route = Session_Route) {
        SessionRout()
    }
}