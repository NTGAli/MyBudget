package com.ntg.features.profile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ntg.core.model.ProfileActions

const val Profile_Route = "profile_route"

fun NavController.navigateToProfile() {
    navigate(Profile_Route)
}

fun NavGraphBuilder.profileScreen(
    profileActions: (action: ProfileActions) -> Unit
) {

    composable(route = Profile_Route) {
        ProfileRoute(
            profileActions = profileActions
        )
    }
}