package com.ntg.mybudget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import com.ntg.core.designsystem.theme.MyBudgetTheme
import com.ntg.mybudget.ui.BudgetApp
import com.ntg.mybudget.ui.rememberBudgetAppState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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

            MyBudgetTheme {
                BudgetApp(appState)
            }
        }
    }
}