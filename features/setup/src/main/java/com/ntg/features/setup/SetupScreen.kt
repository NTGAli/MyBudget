package com.ntg.features.setup

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ntg.core.designsystem.components.AccountSection
import com.ntg.core.designsystem.components.AppBar
import com.ntg.core.designsystem.components.BudgetButton
import com.ntg.core.designsystem.components.ButtonSize
import com.ntg.core.designsystem.components.ButtonStyle
import com.ntg.core.designsystem.components.ButtonType
import com.ntg.core.designsystem.components.SampleAddAccountButton
import com.ntg.core.model.AccountWithSources
import com.ntg.core.mybudget.common.LoginEventListener
import com.ntg.core.mybudget.common.SharedViewModel
import com.ntg.feature.setup.R
import kotlinx.coroutines.launch

@Composable
fun SetupRoute(
    sharedViewModel: SharedViewModel,
    setupViewModel: SetupViewModel = hiltViewModel(),
    navigateToSource: (id: Int, sourceId: Int?) -> Unit,
    navigateToAccount: (id: Int) -> Unit,
    navigateToHome: () -> Unit,
    onShowSnackbar: suspend (Int, String?, Int?) -> Boolean,
    navigateToLogin: (Boolean) -> Unit
) {

    sharedViewModel.setExpand.postValue(true)
    sharedViewModel.bottomNavTitle.postValue(stringResource(id = R.string.submit))

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val accounts =
        setupViewModel.accountWithSources().collectAsStateWithLifecycle(initialValue = null)

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
        },
        deleteAccount = {
            setupViewModel.deleteAccount(it, context = context)
            setupViewModel.homeUiState.value = SetupUiState.Success
        },
        deleteWallet = {
            setupViewModel.tempRemoveWallet(it, context)
            setupViewModel.homeUiState.value = SetupUiState.Success
        },
        onShowSnackbar = onShowSnackbar
    )

    LaunchedEffect(key1 = accounts) {
        sharedViewModel.loginEventListener = object : LoginEventListener {
            override fun onBottomButtonClick() {
                scope.launch {
                    if (accounts.value.orEmpty().isEmpty()) {
                        onShowSnackbar.invoke(R.string.err_no_aacount, null, null)
                    } else if (accounts.value.orEmpty().none {
                            it.sources.isNotEmpty()
                        }) {
                        onShowSnackbar.invoke(R.string.err_no_sources, null, null)
                    } else {
                        setupViewModel.selectDefault()
                        navigateToHome()
                    }
                }
            }
        }
    }

//    if (accounts.value != null){
//        LaunchedEffect(key1 = Unit) {
//            if (accounts.value.orEmpty().any { it.sources.isNotEmpty() }){
//                navigateToHome()
//            }
//        }
//    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SetupScreen(
    accounts: State<List<AccountWithSources>?>,
    navigateToSource: (id: Int, sourceId: Int?) -> Unit,
    uiSate: SetupUiState,
    onShowSnackbar: suspend (Int, String?, Int?) -> Boolean,
    navigateToAccount: (id: Int) -> Unit,
    editAccount: (id: Int) -> Unit,
    deleteAccount: (id: Int) -> Unit,
    deleteWallet: (id: Int) -> Unit,
    backToLogin: () -> Unit
) {

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showDialog by remember {
        mutableStateOf(false)
    }

    var dialogTitle by remember {
        mutableStateOf("")
    }

    var dialogDiscription by remember {
        mutableStateOf("")
    }

    var selectedAccount by remember {
        mutableStateOf<Int?>(null)
    }

    var selectedWallet by remember {
        mutableStateOf<Int?>(null)
    }

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
                    isAccountSelected = true,
                    account = account, isCheckBox = false, insertNewItem = {
                        navigateToSource(account.accountId, null)
                    }, accountEndIconClick = {
                        editAccount(it)
                    }, onSourceEdit = {
                        navigateToSource(account.accountId, it)
                    }, deleteSource = {
                        showDialog = true
                        dialogTitle = context.getString(R.string.delete_source)
                        dialogDiscription = context.getString(R.string.delete_source_desc)
                        selectedWallet = it
                    }, deleteAccount = {
                        if (!account.isDefault) {
                            showDialog = true
                            selectedAccount = account.accountId
                            dialogTitle = context.getString(R.string.delete_account)
                            dialogDiscription =
                                context.getString(R.string.delete_account_desc)
                        } else {
                            scope.launch {
                                onShowSnackbar(R.string.deleting_deafult_account, null, null)
                            }
                        }
                    }
                )
            }

            item {
                SampleAddAccountButton() {
                    navigateToAccount(0)
                }
            }

            item {
                Spacer(modifier = Modifier.padding(24.dp))
            }

        }
    }


    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                selectedAccount = null
                selectedWallet = null
                showDialog = false
            },
            title = { Text(dialogTitle) },
            text = { Text(dialogDiscription) },
            confirmButton = {
                BudgetButton(
                    text = stringResource(id = R.string.delete),
                    type = ButtonType.Error,
                    size = ButtonSize.MD,
                    style = ButtonStyle.TextOnly
                ) {
                    showDialog = false
                    if (selectedAccount != null) {
                        deleteAccount(selectedAccount!!)
                    }else if (selectedWallet != null){
                        deleteWallet(selectedWallet!!)
                    }
                }
            },
            dismissButton = {
                BudgetButton(
                    text = stringResource(id = R.string.cancel),
                    size = ButtonSize.MD,
                    style = ButtonStyle.TextOnly
                ) {
                    showDialog = false
                    selectedAccount = null
                    selectedWallet = null
                }
            },
        )
    }


}