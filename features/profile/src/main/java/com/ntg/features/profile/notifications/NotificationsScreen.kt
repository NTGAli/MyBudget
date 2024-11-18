@file:OptIn(ExperimentalMaterial3Api::class)

package com.ntg.features.profile.notifications

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ntg.core.designsystem.components.AppBar
import com.ntg.core.designsystem.components.HeaderSection
import com.ntg.core.designsystem.components.SampleItem
import com.ntg.mybudget.core.designsystem.R

@Composable
fun NotificationsRoute(
    notificationsViewModel: NotificationsViewModel = hiltViewModel()
) {

    val reminderSubmitTransactions = remember { mutableStateOf(true) }
    val reminderSubmitTransactionsWithMessages = remember { mutableStateOf(true) }
    val loginNotification = remember { mutableStateOf(true) }
    val updateNotification = remember { mutableStateOf(true) }
    val rulesRegulationsNotification = remember { mutableStateOf(true) }


    NotificationsScreen(
        reminderSubmitTransactions,
        reminderSubmitTransactionsWithMessages,
        loginNotification,
        updateNotification,
        rulesRegulationsNotification
    )
}

@Composable
fun NotificationsScreen(
    reminderSubmitTransactions: MutableState<Boolean>,
    reminderSubmitTransactionsWithMessages: MutableState<Boolean>,
    loginNotification: MutableState<Boolean>,
    updateNotification: MutableState<Boolean>,
    rulesRegulationsNotification: MutableState<Boolean>,
) {

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppBar(
                enableNavigation = false,
                title = stringResource(id = R.string.Notifications),
                scrollBehavior = scrollBehavior
            )
        },

        ) { paddingValues ->

        Column (modifier = Modifier.padding(paddingValues)){

            HeaderSection(stringResource(id = R.string.Reminder))

            SampleItem(
                modifier = Modifier.padding(horizontal = 24.dp),
                title = stringResource(id = R.string.ReminderSubmitTransactionsIn21),
                setSwitch = true,
                isSwitchCheck = reminderSubmitTransactions.value,
                hasDivider = true
            ) { reminderSubmitTransactions.value = !reminderSubmitTransactions.value }

            SampleItem(
                modifier = Modifier.padding(horizontal = 24.dp),
                title = stringResource(id = R.string.ReminderSubmitTransactionsWithMessages),
                setSwitch = true,
                isSwitchCheck = reminderSubmitTransactionsWithMessages.value,
                hasDivider = true
            ) { reminderSubmitTransactionsWithMessages.value = !reminderSubmitTransactionsWithMessages.value }

            HeaderSection(stringResource(id = R.string.Security), Modifier.padding(top = 16.dp, bottom = 4.dp))

            SampleItem(
                modifier = Modifier.padding(horizontal = 24.dp),
                title = stringResource(id = R.string.NewLogInNotification),
                setSwitch = true,
                isSwitchCheck = loginNotification.value,
                hasDivider = true
            ) { loginNotification.value = !loginNotification.value }


            HeaderSection(stringResource(id = R.string.Update), Modifier.padding(top = 16.dp, bottom = 4.dp))

            SampleItem(
                modifier = Modifier.padding(horizontal = 24.dp),
                title = stringResource(id = R.string.NewUpdateNotification),
                setSwitch = true,
                isSwitchCheck = updateNotification.value,
                hasDivider = true
            ) { updateNotification.value = !updateNotification.value }

            SampleItem(
                modifier = Modifier.padding(horizontal = 24.dp),
                title = stringResource(id = R.string.NewRulesRegulationsNotification),
                setSwitch = true,
                isSwitchCheck = rulesRegulationsNotification.value,
                hasDivider = true
            ) { rulesRegulationsNotification.value = !rulesRegulationsNotification.value }
        }
    }
}

@Preview
@Composable
fun NotificationsScreenPreview() {
    val test = remember { mutableStateOf(true) }

    NotificationsScreen(test, test, test, test, test)
}