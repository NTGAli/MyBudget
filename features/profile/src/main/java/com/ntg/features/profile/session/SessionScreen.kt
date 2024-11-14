package com.ntg.features.profile.session

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ntg.core.designsystem.components.AppBar
import com.ntg.core.designsystem.components.ButtonType
import com.ntg.core.designsystem.components.SampleItem
import com.ntg.core.designsystem.components.SessionItem
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.feature.profile.R

@Composable
fun SessionRout() {

    SessionScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen() {

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppBar(
                enableNavigation = false,
                title = stringResource(id = R.string.ActiveSessions),
                scrollBehavior = scrollBehavior
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(top = 8.dp)
        ) {

            item {
                SessionItem(
                    isMySelf = true,
                    isMobile = true,
                    deviceName = "Samsung A51",
                    deviceIp = "127.0.0.1",
                    deviceStatus = "2024-09-21 ساعت 13:30"
                ) {}

                SampleItem(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 4.dp),
                    title = stringResource(id = R.string.RemoveOtherSessions),
                    iconPainter = painterResource(id = BudgetIcons.doNotDo),
                    iconTint = MaterialTheme.colorScheme.error,
                    type = ButtonType.Error
                ) {

                }

                HorizontalDivider(
                    thickness = 8.dp,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = stringResource(id = R.string.OtherSessions),
                    style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 8.dp, start = 28.dp, end = 28.dp)
                        .fillMaxWidth()
                )
            }

            items(
                count = 6,

            ) {
                SessionItem(
                    isMySelf = false,
                    isMobile = it % 3 != 0,
                    deviceName = "Samsung A51",
                    deviceIp = "127.0.0.1",
                    deviceStatus = "2024-09-21 ساعت 13:30"
                ) {}
            }
        }
    }
}