package com.ntg.features.profile.notifications

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val Notifications_Route = "notifications_route"

fun NavController.navigateToNotifications(){
    navigate(Notifications_Route)
}

fun NavGraphBuilder.notificationsScreen() {
    composable(route = Notifications_Route) {
        NotificationsRoute()
    }
}