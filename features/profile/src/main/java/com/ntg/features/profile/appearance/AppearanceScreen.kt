@file:OptIn(ExperimentalMaterial3Api::class)

package com.ntg.features.profile.appearance

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ntg.core.designsystem.components.AppBar
import com.ntg.core.designsystem.components.CustomDivider
import com.ntg.core.designsystem.components.HeaderSection
import com.ntg.core.designsystem.components.OptionSelector
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.core.model.ThemeState
import com.ntg.mybudget.core.designsystem.R

@Composable
fun AppearanceRoute(
    appearanceViewModel: AppearanceViewModel = hiltViewModel(),
    onBack:() -> Unit
) {
    val userData = appearanceViewModel.userData.collectAsStateWithLifecycle(null)

    val selectedTheme = remember(userData.value) {
        mutableIntStateOf(
            when (userData.value?.themeState) {
                ThemeState.Default -> 0
                ThemeState.Light -> 1
                ThemeState.Dark -> 2
                else -> 0
            }
        )
    }
    val selectedLanguage = remember { mutableIntStateOf(0) }

    AppearanceScreen(
        selectedTheme = selectedTheme,
        selectedLanguage = selectedLanguage,
        onThemeChange = appearanceViewModel::setTheme,
        onBack = onBack
    )
}

@Composable
fun AppearanceScreen(
    selectedTheme: MutableIntState,
    selectedLanguage: MutableIntState,
    onThemeChange: (ThemeState) -> Unit,
    onBack:() -> Unit
) {

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val themeOptions = listOf(
        Pair("پیشفرض گوشی", BudgetIcons.default),
        Pair("روشن", BudgetIcons.sun),
        Pair("تاریک", BudgetIcons.moon),
    )

    val languageOptions = listOf(
        Pair("فارسی", null),
        Pair("English", null),
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppBar(
                enableNavigation = true,
                title = stringResource(id = R.string.AppUi),
                scrollBehavior = scrollBehavior,
                navigationOnClick = {
                    onBack()
                }
            )
        },

    ) { paddingValues ->

        Column (modifier = Modifier.padding(paddingValues)){

            HeaderSection(text = stringResource(id = R.string.AppTheme))

            OptionSelector(
                themeOptions,
                selectedTheme.intValue,
                onOptionSelected = {
                    selectedTheme.intValue = it
                    when(it){
                        0 -> onThemeChange(ThemeState.Default)
                        1 -> onThemeChange(ThemeState.Light)
                        2 -> onThemeChange(ThemeState.Dark)
                    }
                }
            )

            CustomDivider(modifier = Modifier.padding(top = 8.dp, bottom = 13.dp))

            HeaderSection(text = stringResource(id = R.string.Language))

            OptionSelector(
                languageOptions,
                selectedLanguage.intValue,
                onOptionSelected = { selectedLanguage.intValue = it }
            )
        }
    }
}