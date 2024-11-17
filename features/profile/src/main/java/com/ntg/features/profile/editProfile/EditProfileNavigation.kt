package com.ntg.features.profile.editProfile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ntg.core.model.ProfileActions
import com.ntg.core.mybudget.common.SharedViewModel

const val Edit_Profile_Route = "edit_profile_route"

fun NavController.navigateToEditProfile() {
    navigate(Edit_Profile_Route)
}

fun NavGraphBuilder.editProfileScreen(
    sharedViewModel: SharedViewModel,
    onShowSnackbar: suspend (Int, String?, Int?) -> Boolean,
) {

    composable(route = Edit_Profile_Route) {
        EditeProfileRout(
            sharedViewModel = sharedViewModel,
            onShowSnackbar = onShowSnackbar,
        )
    }
}