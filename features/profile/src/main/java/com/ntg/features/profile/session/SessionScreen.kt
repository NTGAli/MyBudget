@file:OptIn(ExperimentalMaterial3Api::class)

package com.ntg.features.profile.session

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.ntg.core.network.model.Result
import com.ntg.core.designsystem.components.AppBar
import com.ntg.core.designsystem.components.ButtonType
import com.ntg.core.designsystem.components.CustomDivider
import com.ntg.core.designsystem.components.SampleItem
import com.ntg.core.designsystem.components.SessionItem
import com.ntg.core.designsystem.components.SessionItemShimmer
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.core.model.res.SessionsResItem
import com.ntg.core.mybudget.common.convertDateTime
import com.ntg.mybudget.core.designsystem.R
import kotlinx.coroutines.launch

@Composable
fun SessionRout(
    sessionViewModel: SessionViewModel = hiltViewModel(),
    onShowSnackbar: suspend (Int, String?, Int?) -> Boolean,
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(false) }
    val sessionsList = remember { mutableStateListOf<SessionsResItem>() }

    if (!isLoading) {
        SessionScreen(
            sessionsList,
            onTerminateAll = { sessionViewModel.terminateAllSessions() },
            onTerminateSession = { sessionViewModel.terminateSession(it) }
        )
    } else {
        SessionShimmerLoading()
    }

    LaunchedEffect(Unit) {
        sessionViewModel.sessionsState
            .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .collect { res ->
                when (res) {
                    is Result.Success -> {
                        isLoading = false
                        sessionsList.clear()

                        val list = res.data.sortedByDescending { it.is_current }
                        sessionsList.addAll(list)
                    }

                    is Result.Loading -> {
                        if (res.loading) {
                            isLoading = true
                        }
                    }

                    is Result.Error -> {
                        scope.launch {
//                            onShowSnackbar(res.data.message, null) //TODO: we must pass response message
                            onShowSnackbar(R.string.GetDataFailed, null, null)
                        }
                    }
                }
            }
    }

    LaunchedEffect(Unit) {
        sessionViewModel.terminateState
            .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .collect { res ->
                when (res) {
                    is Result.Success -> {
                        sessionViewModel.getAllSessions()
                        isLoading = false

                        scope.launch {
//                            onShowSnackbar(res.data.message, null) //TODO: we must pass response message
                            onShowSnackbar(R.string.SessionsTerminate, null, null)
                        }
                    }

                    is Result.Loading -> {
                        if (res.loading) {
                            isLoading = true
                        }
                    }

                    is Result.Error -> {
                        scope.launch {
//                            onShowSnackbar(res.data.message, null) //TODO: we must pass response message
                            onShowSnackbar(R.string.SessionsTerminateFailed, null, null)
                        }
                    }
                }
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(
    sessionsList: SnapshotStateList<SessionsResItem>,
    onTerminateAll :() -> Unit,
    onTerminateSession :(String) -> Unit
) {

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
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
        ) {

            items(sessionsList) { item ->
                SessionItem(
                    isMySelf = item.is_current,
                    isMobile = item.device_name == "okhttp/4.12.0",//TODO: we must pass device type
                    deviceName = item.device_name,
                    deviceIp = item.ip_address,
                    deviceStatus = convertDateTime(item.last_used_at),
                ) {
                    onTerminateSession(item.id)
                }

                if (item.is_current) {
                    TerminateAllButton(onTerminateAll)
                }

                if (sessionsList.size == 1) {
                    NoActiveSessions()
                }
            }
        }
    }
}

@Composable
fun TerminateAllButton(
    onTerminateAll :() -> Unit,
) {
    SampleItem(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 4.dp),
        title = stringResource(id = R.string.RemoveOtherSessions),
        iconPainter = painterResource(id = BudgetIcons.doNotDo),
        iconTint = MaterialTheme.colorScheme.error,
        type = ButtonType.Error
    ) {
        onTerminateAll()
    }

    CustomDivider(modifier = Modifier.padding(top = 4.dp))

    Text(
        text = stringResource(id = R.string.OtherSessions),
        style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.primary),
        modifier = Modifier
            .padding(top = 16.dp, bottom = 8.dp, start = 28.dp, end = 28.dp)
            .fillMaxWidth()
    )
}

@Composable
fun NoActiveSessions() {
    Box(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .border(
                1.dp,
                MaterialTheme.colorScheme.surfaceContainerHighest,
                RoundedCornerShape(16.dp)
            )
    ) {
        Text(
            text = stringResource(id = R.string.NoActiveSessions),
            style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.outline, textAlign = TextAlign.Center),
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
fun SessionShimmerLoading() {
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
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            SessionItemShimmer()
            TerminateAllButton() {}
            repeat(3) {
                SessionItemShimmer()
            }
        }
    }
}