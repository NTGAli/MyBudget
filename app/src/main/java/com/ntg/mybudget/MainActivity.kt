package com.ntg.mybudget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import com.ntg.core.data.repository.UserDataRepository
import com.ntg.core.designsystem.theme.MyBudgetTheme
import com.ntg.core.model.ThemeState
import com.ntg.core.mybudget.common.logd
import com.ntg.mybudget.ui.BudgetApp
import com.ntg.mybudget.ui.rememberBudgetAppState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userDataRepository: UserDataRepository

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val appState = rememberBudgetAppState(
                windowSizeClass = calculateWindowSizeClass(this),
//                networkMonitor = networkMonitor,
//                userNewsResourceRepository = userNewsResourceRepository,
//                timeZoneMonitor = timeZoneMonitor,
            )

            val themeState = userDataRepository.userData.collectAsState(initial = null)
            val darkTheme = when (themeState.value?.themeState) {
                ThemeState.Default -> isSystemInDarkTheme()
                ThemeState.Dark -> true
                ThemeState.Light -> false
                else -> {
                    isSystemInDarkTheme()
                }
            }

            MyBudgetTheme(darkTheme = darkTheme) {
                BudgetApp(appState, userDataRepository = userDataRepository)
            }
        }
    }
}