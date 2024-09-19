package com.ntg.mybudget.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport.Session.User
import com.ntg.core.data.repository.UserDataRepository
import com.ntg.core.designsystem.components.BottomNavigation
import com.ntg.core.designsystem.components.BudgetSnackBar
import com.ntg.core.designsystem.components.scrollbar.BudgetBackground
import com.ntg.core.designsystem.model.NavigationItem
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.core.mybudget.common.SharedViewModel
import com.ntg.features.setup.Setup_Route
import com.ntg.login.Login_Route
import com.ntg.mybudget.navigation.BudgetNavHost
import com.ntg.mybudget.navigation.TopLevelDestination

@Composable
fun BudgetApp(
    appState: BudgetAppState, modifier: Modifier = Modifier,
    userDataRepository: UserDataRepository
//    setupViewModel: SetupViewModel = hiltViewModel(),
) {

    var showSettingsDialog by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }


    BudgetBackground(modifier = modifier
        .safeDrawingPadding()) {
        BudgetApp(
            appState = appState,
            snackbarHostState = snackbarHostState,
            showSettingsDialog = showSettingsDialog,
            onSettingsDismissed = { showSettingsDialog = false },
            onTopAppBarActionClick = { showSettingsDialog = true },
            userDataRepository = userDataRepository
        )
    }

}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun BudgetApp(
    appState: BudgetAppState,
    snackbarHostState: SnackbarHostState,
    showSettingsDialog: Boolean,
    onSettingsDismissed: () -> Unit,
    onTopAppBarActionClick: () -> Unit,
    userDataRepository: UserDataRepository,
    modifier: Modifier = Modifier,
    sharedViewModel: SharedViewModel = hiltViewModel()
) {

    val lifecycle = LocalLifecycleOwner.current
    val context = LocalContext.current
    val isUserLogged = userDataRepository.userData.collectAsState(initial = null).value?.isLogged
        ?: return

    var bottomNavTitle by remember {
        mutableStateOf<String?>(null)
    }

    var bottomNavIcon by remember {
        mutableIntStateOf(BudgetIcons.Transaction)
    }

    var isExpand by remember {
        mutableStateOf(false)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = Unit) {
        sharedViewModel.bottomNavTitle.observe(lifecycle){
            bottomNavTitle = it
        }
        sharedViewModel.bottomNavIcon.observe(lifecycle){
            bottomNavIcon = it
        }
        sharedViewModel.setExpand.observe(lifecycle){
            isExpand = it
        }
        sharedViewModel.setLoading.observe(lifecycle){
            isLoading = it
        }
    }

    Scaffold(
        modifier = modifier
//            .safeContentPadding()
            .semantics {
                testTagsAsResourceId = true
            },
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
//        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(
            snackbarHostState,){
            BudgetSnackBar(data = it)
        } },
        bottomBar = {
            if (appState.shouldShowBottomBar) {
                AppBottomBar(
                    onNavigateToDestination = {
                        sharedViewModel.sendLoginEvent()
//                        appState::navigateToTopLevelDestination
                    },
                    expandButton = isExpand,
                    title = bottomNavTitle,
                    isLoading = isLoading
                )
            }
        }
    ) { padding ->
        Row(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal,
                    ),
                ),
        ) {

            if (appState.shouldShowNavRail) {
//        NavigationRail(
//        )
            }


            Column(Modifier.fillMaxSize()) {
                // Show the top app bar on top level destinations.
                val destination = appState.currentTopLevelDestination
                val shouldShowTopAppBar = destination != null
                if (destination != null) {
//                    BudgetAppbar()
                }

                BudgetNavHost(
                    appState = appState,
                    onShowSnackbar = { message, action ->
                        snackbarHostState.showSnackbar(
                            message = context.getString(message),
                            actionLabel = action,
                            duration = SnackbarDuration.Short
                        ) == SnackbarResult.ActionPerformed
                    },
                    modifier = if (shouldShowTopAppBar) {
                        Modifier.consumeWindowInsets(
                            WindowInsets.safeDrawing.only(WindowInsetsSides.Top),
                        )
                    } else {
                        Modifier
                    },
                    startDestination = if (isUserLogged) Setup_Route else Login_Route,
                    sharedViewModel
                )
            }
        }
    }
}


@Composable
private fun AppBottomBar(
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    expandButton:Boolean,
    isLoading:Boolean,
    title: String? = null
) {
    val navs = listOf(
        NavigationItem(
            1,
            "test",
            painterResource(id = BudgetIcons.Home),
            painterResource(id = BudgetIcons.Home),
            isSelected = true,
        ),
        NavigationItem(
            2,
            "test",
            painterResource(id = BudgetIcons.Home),
            painterResource(id = BudgetIcons.Home),
            isSelected = false,
        ),
    )


    BottomNavigation(modifier = Modifier, items = navs, expandButton = expandButton, txtButton = title, isLoading = isLoading) {
        onNavigateToDestination(TopLevelDestination.HOME)
//        if (it == 1) {
//            onNavigateToDestination(TopLevelDestination.HOME)
//        } else if (it == 2) {
//            onNavigateToDestination(TopLevelDestination.PROFILE)
//        } else {
//            onNavigateToDestination(TopLevelDestination.TRANSACTION)
//        }
    }
}
