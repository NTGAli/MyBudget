package com.ntg.features.profile.profile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ntg.core.model.ProfileActions
import com.ntg.core.mybudget.common.SharedViewModel

const val Profile_Route = "profile_route"

fun NavController.navigateToProfile() {
    navigate(Profile_Route)
}

fun NavGraphBuilder.profileScreen(
    sharedViewModel: SharedViewModel,
    profileActions: (action: ProfileActions) -> Unit
) {

    composable(route = Profile_Route) {
        ProfileRoute(
            sharedViewModel = sharedViewModel,
            profileActions = profileActions
        )
    }
}