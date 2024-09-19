package com.ntg.features.setup

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ntg.core.designsystem.components.AccountSection
import com.ntg.core.designsystem.components.AppBar
import com.ntg.core.designsystem.components.Popup
import com.ntg.core.designsystem.model.PopupItem
import com.ntg.core.designsystem.components.LoadingView
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.core.model.Account
import com.ntg.core.model.AccountWithSources
import com.ntg.core.mybudget.common.LoginEventListener
import com.ntg.core.mybudget.common.SharedViewModel
import com.ntg.core.mybudget.common.generateUniqueFiveDigitId
import com.ntg.core.mybudget.common.toUnixTimestamp
import com.ntg.core.network.model.Result
import com.ntg.feature.setup.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

@Composable
fun SetupRoute(
    sharedViewModel: SharedViewModel,
    setupViewModel: SetupViewModel = hiltViewModel(),
    navigateToSource: (id: Int, sourceId: Int?) -> Unit,
    navigateToAccount: (id: Int) -> Unit,
    navigateToHome: () -> Unit,
    onShowSnackbar: suspend (Int, String?) -> Boolean,
    navigateToLogin:(Boolean) -> Unit
) {

    sharedViewModel.setExpand.postValue(true)
    sharedViewModel.bottomNavTitle.postValue(stringResource(id = R.string.submit))

    val scope = rememberCoroutineScope()
        val accounts =
        setupViewModel.accountWithSources().collectAsStateWithLifecycle(initialValue = null)


    LaunchedEffect(accounts.value) {
        Log.d("SetupRoute", "SetupRoute: $accounts")
        if (accounts.value != null && accounts.value.orEmpty().isEmpty()){
            setupViewModel.serverAccounts()
            setupViewModel.serverAccounts.collect{
                when(it){
                    is Result.Error -> {
                        setupViewModel.homeUiState.value = SetupUiState.Error
                        scope.launch {
                            onShowSnackbar(R.string.err_fetch_data, null)
                        }
                    }
                    is Result.Loading -> {
                        setupViewModel.homeUiState.value = SetupUiState.Loading
                    }
                    is Result.Success -> {
                        setupViewModel.homeUiState.value = SetupUiState.Success
                        it.data?.forEach { account ->
                            if (account.name == "default"){
                                setupViewModel.setDefaultAccount()
                            }else{
                                val localAccountId = generateUniqueFiveDigitId()
                                setupViewModel.insertNewAccount(Account(
                                    id = localAccountId,
                                    sId = account.id,
                                    name = account.name.orEmpty(),
                                    isSelected = false,
                                    isSynced = true,
                                    dateCreated = account.createdAt.orEmpty().toUnixTimestamp().toString()
                                ))
                            }
                        }

                    }
                }

            }
        }

    }

    val uiSate = setupViewModel.homeUiState.collectAsStateWithLifecycle()

    SetupScreen(
        accounts = accounts,
        navigateToSource = navigateToSource,
        navigateToAccount = navigateToAccount,
        uiSate = uiSate.value,
        editAccount = {
            navigateToAccount(it)
        },
        backToLogin = {
            setupViewModel.logout()
            navigateToLogin(true)
        }
    )

    LaunchedEffect(key1 = accounts) {
        sharedViewModel.loginEventListener = object : LoginEventListener {
            override fun onLoginEvent() {
                scope.launch {
                    if (accounts.value.orEmpty().isEmpty()) {
                        onShowSnackbar.invoke(R.string.err_no_aacount, null)
                    } else if (accounts.value.orEmpty().none {
                            it.sources.isNotEmpty()
                        }) {
                        onShowSnackbar.invoke(R.string.err_no_sources, null)
                    }else{
                        navigateToHome()
                    }
                }
            }
        }
    }

    if (accounts.value != null){
        LaunchedEffect(key1 = Unit) {
            if (accounts.value.orEmpty().any { it.sources.isNotEmpty() }){
                navigateToHome()
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SetupScreen(
    accounts: State<List<AccountWithSources>?>,
    navigateToSource: (id: Int, sourceId: Int?) -> Unit,
    uiSate: SetupUiState,
    navigateToAccount: (id: Int) -> Unit,
    editAccount: (id: Int) -> Unit,
    backToLogin: () -> Unit
) {

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()



    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppBar(
                enableNavigation = false,
                title = stringResource(id = R.string.createAccount),
                scrollBehavior = scrollBehavior
            )
        }
    ) {

        when(uiSate){
            SetupUiState.Error -> {
                backToLogin()
            }
            SetupUiState.Loading -> {
                LoadingView(Modifier.padding(it))
            }
            SetupUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(it)
                ) {

                    item {
                        Spacer(modifier = Modifier.padding(8.dp))
                    }

                    items(accounts.value.orEmpty()) { account ->
                        AccountSection(
                            modifier = Modifier
                                .padding(horizontal = 24.dp)
                                .padding(top = 8.dp),
                            account = account, canEdit = true, insertNewItem = {
                                navigateToSource(account.accountId, null)
                            }, accountEndIconClick = {
                                editAccount(it)
                            }, onSourceEdit = {
                                navigateToSource(account.accountId, it)
                            })
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                                .padding(horizontal = 24.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    width = 1.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surfaceContainerHighest
                                )
                                .clickable {
                                    navigateToAccount(0)
                                }
                                .padding(vertical = 18.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.add_new_account),
                                style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.outlineVariant)
                            )
                            Icon(
                                modifier = Modifier.padding(start = 8.dp),
                                painter = painterResource(id = BudgetIcons.Add),
                                contentDescription = "add account"
                            )
                        }

                    }

                    item {
                        Spacer(modifier = Modifier.padding(24.dp))
                    }

                }
            }
        }



    }


}