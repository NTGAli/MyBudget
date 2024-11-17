package com.ntg.features.profile.appearance

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val Appearance_Route = "appearance_route"

fun NavController.navigateToAppearance() {
    navigate(Appearance_Route)
}

fun NavGraphBuilder.appearanceScreen(
    onBack:() -> Unit
) {
    composable(route = Appearance_Route) {
        AppearanceRoute(
            onBack = onBack
        )
    }
}