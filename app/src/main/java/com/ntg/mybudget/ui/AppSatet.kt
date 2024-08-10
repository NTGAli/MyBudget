package com.ntg.mybudget.ui

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.tracing.trace
import com.ntg.login.Login_Route
import com.ntg.mybudget.navigation.TopLevelDestination
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberBudgetAppState(
  windowSizeClass: WindowSizeClass,
//    networkMonitor: NetworkMonitor,
//    userNewsResourceRepository: UserNewsResourceRepository,
//    timeZoneMonitor: TimeZoneMonitor,
  coroutineScope: CoroutineScope = rememberCoroutineScope(),
  navController: NavHostController = rememberNavController(),
): BudgetAppState {
  NavigationTrackingSideEffect(navController)
  return remember(
    navController,
    coroutineScope,
    windowSizeClass,
//        networkMonitor,
//        userNewsResourceRepository,
//        timeZoneMonitor,
  ) {
    BudgetAppState(
      navController = navController,
      coroutineScope = coroutineScope,
      windowSizeClass = windowSizeClass,
//            networkMonitor = networkMonitor,
//            userNewsResourceRepository = userNewsResourceRepository,
//            timeZoneMonitor = timeZoneMonitor,
    )
  }
}

@Stable
class BudgetAppState(
  val navController: NavHostController,
  coroutineScope: CoroutineScope,
  val windowSizeClass: WindowSizeClass,
//    networkMonitor: NetworkMonitor,
//    userNewsResourceRepository: UserNewsResourceRepository,
//    timeZoneMonitor: TimeZoneMonitor,
) {
  val currentDestination: NavDestination?
    @Composable get() = navController
      .currentBackStackEntryAsState().value?.destination

  val currentTopLevelDestination: TopLevelDestination?
    @Composable get() = when (currentDestination?.route) {
//      Home_Route -> TopLevelDestination.HOME
      else -> null
    }

  val shouldShowBottomBar: Boolean
    @Composable get() = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
      && when (currentDestination?.route) {
        Login_Route -> true
      else -> false
    }

  val shouldShowNavRail: Boolean
    get() = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact

//    val isOffline = networkMonitor.isOnline
//        .map(Boolean::not)
//        .stateIn(
//            scope = coroutineScope,
//            started = SharingStarted.WhileSubscribed(5_000),
//            initialValue = false,
//        )

  /**
   * Map of top level destinations to be used in the TopBar, BottomBar and NavRail. The key is the
   * route.
   */
  val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

  fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
    trace("Navigation: ${topLevelDestination.name}") {
      val topLevelNavOptions = navOptions {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(navController.graph.findStartDestination().id) {
          saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
      }

//      when (topLevelDestination) {
//        TopLevelDestination.HOME -> navController.navigateToForYou(topLevelNavOptions)
//      }


    }
  }

}


@Composable
private fun NavigationTrackingSideEffect(navController: NavHostController) {

}
