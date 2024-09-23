package com.ntg.features.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ntg.core.designsystem.components.AccountSection
import com.ntg.core.designsystem.components.AccountSelector
import com.ntg.core.designsystem.components.AppBar
import com.ntg.core.designsystem.components.CardReport
import com.ntg.core.designsystem.components.FullScreenBottomSheet
import com.ntg.core.designsystem.components.SampleAddAccountButton
import com.ntg.core.designsystem.components.SampleItem
import com.ntg.core.designsystem.components.SwitchText
import com.ntg.core.designsystem.model.SwitchItem
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.core.model.AccountWithSources
import com.ntg.core.model.SourceWithDetail
import com.ntg.core.model.Transaction
import com.ntg.core.mybudget.common.LoginEventListener
import com.ntg.core.mybudget.common.SharedViewModel
import com.ntg.core.mybudget.common.formatCurrency
import com.ntg.core.mybudget.common.logd
import com.ntg.feature.home.R
import kotlinx.coroutines.launch

@Composable
fun HomeRoute(
    sharedViewModel: SharedViewModel,
    homeViewModel: HomeViewModel = hiltViewModel(),
    navigateToSource: (id: Int, sourceId: Int?) -> Unit,
    navigateToAccount: (id: Int) -> Unit,
    onShowSnackbar: suspend (Int, String?) -> Boolean,
){
    var expandTransaction = remember { mutableStateOf(false) }
    sharedViewModel.setExpand.postValue(expandTransaction.value)
    sharedViewModel.bottomNavTitle.postValue(if (expandTransaction.value) "submit" else null)

    val accounts = homeViewModel.accountWithSources().collectAsStateWithLifecycle(initialValue = emptyList())
    val currentAccount = homeViewModel.selectedAccount().collectAsStateWithLifecycle(initialValue = null)
    val currentResource = homeViewModel.selectedSources().collectAsStateWithLifecycle(initialValue = emptyList())

    if (currentAccount.value != null && currentAccount.value.orEmpty().isNotEmpty()){
        val sourceIds = currentAccount.value.orEmpty().first().sources.map { it?.id ?: 0 }

        val transactions = homeViewModel.transactions(sourceIds).collectAsStateWithLifecycle(initialValue = null)
        HomeScreen(accounts, currentAccount.value.orEmpty().first(), currentResource.value, transactions, expandTransaction, navigateToSource, navigateToAccount, onShowSnackbar,
            onUpdateSelectedAccount = { id, sourcesId ->
                homeViewModel.updatedSelectedAccount(id)
                homeViewModel.updatedSelectedSources(sourcesId)
            },
            onUpdateSelectedSource = { sourcesId ->
                homeViewModel.updatedSelectedSources(sourcesId)
            }
        )
    }

    LaunchedEffect(key1 = Unit) {
        sharedViewModel.loginEventListener = object : LoginEventListener {
            override fun onLoginEvent() {
                expandTransaction.value = !expandTransaction.value
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    accounts: State<List<AccountWithSources>?>,
    currentAccount: AccountWithSources,
    currentResource: List<SourceWithDetail>?,
    transactions: State<List<Transaction>?>,
    expandTransaction: MutableState<Boolean>,
    navigateToSource: (id: Int, sourceId: Int?) -> Unit,
    navigateToAccount: (id: Int) -> Unit,
    onShowSnackbar: suspend (Int, String?) -> Boolean,
    onUpdateSelectedAccount:(id: Int, sourcesId: List<Int>) -> Unit,
    onUpdateSelectedSource:(sourcesId: List<Int>) -> Unit
) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        topBar = {
            AppBar(
                titleState = {
                    AccountSelector(title = currentAccount.accountName, subTitle = stringResource(
                        id = R.string.items_format, currentAccount.sources.size
                    )){
                        scope.launch { scaffoldState.bottomSheetState.expand() }
                    }
                },
                enableNavigation = false
            )
        },
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            AccountSelectorSheet(accounts, currentAccount, currentResource, navigateToSource, navigateToAccount,onShowSnackbar, onUpdateSelectedAccount, onUpdateSelectedSource)
        }
        ) { padding ->

        LazyColumn(
            Modifier
                .padding(padding)
                .fillMaxSize()) {

            item {

            }

            item {
                CardReport(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .padding(horizontal = 24.dp),
                    title = formatCurrency(
                        amount = transactions.value?.map { it.amount }?.sum() ?: 0L,
                        mask = "###,###",
                        currency = "ت",
                        pos = 2
                    ), subTitle = "موجودی همه حساب ها", out =
                    formatCurrency(
                        amount = transactions.value?.filter { it.type == "out" }.orEmpty()
                            .sumOf { it.amount },
                        mask = "###,###",
                        currency = "ت",
                        pos = 2
                    ), inValue = formatCurrency(
                        amount = transactions.value?.filter { it.type == "in" }.orEmpty()
                            .sumOf { it.amount },
                        mask = "###,###",
                        currency = "ت",
                        pos = 2
                    ))

                Text(
                    modifier = Modifier.padding(top = 16.dp, start = 32.dp, bottom = 16.dp),
                    text = stringResource(id = R.string.transactions), style = MaterialTheme.typography.titleMedium.copy(MaterialTheme.colorScheme.outline))
            }

        }



    }

    InsertTransactionView(expandTransaction)

}

@Composable
fun InsertTransactionView(
    expandTransaction : MutableState<Boolean>
){


    FullScreenBottomSheet(showSheet = expandTransaction, appbar = {
        Row(modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background), verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .padding(start = 16.dp),
                onClick = { /*TODO*/ }) {
                Icon(painter = painterResource(id = BudgetIcons.directionDown), contentDescription = "close")
            }

            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
                ,
                text = stringResource(id = R.string.new_transaction), style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))

            DateItem(unixTime = 123L)
        }
    }) {
        Column(modifier = Modifier
//            .padding(it)
            .verticalScroll(rememberScrollState())) {


            val items = listOf(
                SwitchItem(0, stringResource(id = com.ntg.mybudget.core.designsystem.R.string.outcome), tint = MaterialTheme.colorScheme.onError, backColor = MaterialTheme.colorScheme.error),
                SwitchItem(0, stringResource(id = com.ntg.mybudget.core.designsystem.R.string.income), tint = MaterialTheme.colorScheme.onSecondary, backColor = MaterialTheme.colorScheme.secondary),
                SwitchItem(0, stringResource(id = com.ntg.mybudget.core.designsystem.R.string.internal_transfer), tint = MaterialTheme.colorScheme.onPrimary, backColor = MaterialTheme.colorScheme.primary),


            )

            SwitchText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                items = items) {

            }


        }
    }


}


@Composable
fun DateItem(
    modifier: Modifier = Modifier,
    unixTime: Long
){

    Row(
        modifier = modifier
            .padding(end = 16.dp)
            .background(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.surfaceContainer
            )
            .padding(vertical = 2.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "12 مرداد 1402", style = MaterialTheme.typography.labelMedium.copy(MaterialTheme.colorScheme.onSurfaceVariant))
        Box(modifier = Modifier
            .padding(horizontal = 8.dp)
            .size(4.dp)
            .background(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceDim))
        Text(text = "12:14", style = MaterialTheme.typography.labelMedium.copy(MaterialTheme.colorScheme.onSurfaceVariant))
        
        Icon(painter = painterResource(id = BudgetIcons.CalenderTick), contentDescription = "calender", tint = MaterialTheme.colorScheme.outline)
    }

}

@Composable
fun AccountSelectorSheet(
    accounts: State<List<AccountWithSources>?>,
    currentAccount: AccountWithSources,
    currentResource: List<SourceWithDetail>?,
    navigateToSource: (id: Int, sourceId: Int?) -> Unit,
    navigateToAccount: (id: Int) -> Unit,
    onShowSnackbar: suspend (Int, String?) -> Boolean,
    onUpdateSelectedAccount:(id: Int, sourcesId: List<Int>) -> Unit,
    onUpdateSelectedSource:(sourcesId: List<Int>) -> Unit
) {

    var selectedAccountId by remember { mutableIntStateOf(currentAccount.accountId) }
    val selectedResources = remember { mutableStateListOf<Int>() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        currentResource?.forEach {
            selectedResources.add(it.id)
        }
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {

        accounts.value?.let { mAccounts ->
            items(mAccounts) { account ->
                AccountSection(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(top = 8.dp),
                    account = account,
                    canEdit = false,
                    isAccountSelected = account.accountId == selectedAccountId,
                    selectedSources = selectedResources,
                    insertNewItem = {
                        navigateToSource(account.accountId, null)
                    },
                    onSourceEdit = { sourceId ->
                            navigateToSource(account.accountId, sourceId)
                    },
                    onAccountSelect = { accountId ->
                        selectedAccountId = accountId
                        mAccounts.find { it.accountId == selectedAccountId }?.let { selectedAccount ->
                            selectedResources.clear()
                            selectedResources.addAll(selectedAccount.sources.map { it?.id ?: 0 })
                        }

                        onUpdateSelectedAccount.invoke(accountId, selectedResources)
                    },
                    onSourceSelect = { sourceId ->
                        val selectedAccount = mAccounts.find { it.accountId == selectedAccountId }

                        if (selectedAccount?.sources?.find { it?.id == sourceId } == null) {
                            // when source selected from other account not selectedAccount
                            selectedAccountId = account.accountId
                            selectedResources.clear()
                        }

                        if (selectedResources.contains(sourceId)){
                            if (selectedResources.size > 1) {
                                selectedResources.remove(sourceId)
                            } else {
                                scope.launch {
                                    onShowSnackbar(R.string.err_min_resource, null)
                                }
                            }
                        } else {
                            selectedResources.add(sourceId)
                        }
                        onUpdateSelectedSource.invoke(selectedResources)
                    }
                )
            }
        }

        item {
            SampleAddAccountButton() {
                navigateToAccount(0)
            }
            Spacer(modifier = Modifier.height(8.dp))

            SampleItem(
                modifier = Modifier.padding(horizontal = 24.dp),
                title = stringResource(id = R.string.user_account),
                iconPainter = painterResource(id = BudgetIcons.UserCircle),
                iconTint = MaterialTheme.colorScheme.outline
            ) {
                
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), color = MaterialTheme.colorScheme.surfaceContainerHighest)

            SampleItem(
                modifier = Modifier.padding(horizontal = 24.dp),
                title = stringResource(id = R.string.exit),
                iconPainter = painterResource(id = BudgetIcons.Exit),
                iconTint = MaterialTheme.colorScheme.error
            ) {

            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


