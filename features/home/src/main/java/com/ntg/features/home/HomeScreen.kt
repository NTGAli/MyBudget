package com.ntg.features.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ntg.core.designsystem.components.AccountSection
import com.ntg.core.designsystem.components.AccountSelector
import com.ntg.core.designsystem.components.AppBar
import com.ntg.core.designsystem.components.BudgetButton
import com.ntg.core.designsystem.components.ButtonSize
import com.ntg.core.designsystem.components.ButtonStyle
import com.ntg.core.designsystem.components.ButtonType
import com.ntg.core.designsystem.components.CardReport
import com.ntg.core.designsystem.components.DateDivider
import com.ntg.core.designsystem.components.Lottie
import com.ntg.core.designsystem.components.SampleAddAccountButton
import com.ntg.core.designsystem.components.SampleItem
import com.ntg.core.designsystem.components.TransactionItem
import com.ntg.core.designsystem.components.WheelList
import com.ntg.core.designsystem.model.AppbarItem
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.core.model.AccountWithSources
import com.ntg.core.model.AttachData
import com.ntg.core.model.Contact
import com.ntg.core.model.Transaction
import com.ntg.core.model.Wallet
import com.ntg.core.model.res.Bank
import com.ntg.core.model.res.Category
import com.ntg.core.model.res.Currency
import com.ntg.core.mybudget.common.Constants
import com.ntg.core.mybudget.common.LoginEventListener
import com.ntg.core.mybudget.common.SharedViewModel
import com.ntg.core.mybudget.common.formatCurrency
import com.ntg.core.mybudget.common.formatTimestampToTime
import com.ntg.core.mybudget.common.getCurrentJalaliDate
import com.ntg.core.mybudget.common.getCurrentJalaliMonth
import com.ntg.core.mybudget.common.isTransactionInCurrentJalaliMonth
import com.ntg.core.mybudget.common.jalaliToTimestamp
import com.ntg.core.mybudget.common.logd
import com.ntg.core.mybudget.common.orDefault
import com.ntg.core.mybudget.common.orFalse
import com.ntg.core.mybudget.common.orZero
import com.ntg.core.mybudget.common.persianDate.PersianDate
import com.ntg.core.mybudget.common.toPersianDate
import com.ntg.core.mybudget.common.withSuffix
import com.ntg.mybudget.core.designsystem.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalTime

@Composable
fun HomeRoute(
    sharedViewModel: SharedViewModel,
    homeViewModel: HomeViewModel = hiltViewModel(),
    navigateToSource: (id: Int, sourceId: Int?) -> Unit,
    navigateToAccount: (id: Int) -> Unit,
    navigateToProfile: () -> Unit,
    navigateToDetail: (id: Int) -> Unit,
    startFromSetup: () -> Unit,
    onShowSnackbar: suspend (Int, String?, Int?) -> Boolean,
) {
    val expandTransaction = remember { mutableStateOf(false) }
    sharedViewModel.setExpand.postValue(expandTransaction.value)
    sharedViewModel.bottomNavTitle.postValue(if (expandTransaction.value) "submit" else null)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val accounts = homeViewModel.accountWithSources()
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val currentAccount = homeViewModel.selectedAccount
        .collectAsStateWithLifecycle(initialValue = null)
    val currentResource = homeViewModel.selectedSources
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val allResource = homeViewModel.allSources
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val logoUrlColor = homeViewModel.bankLogoColor
        .collectAsStateWithLifecycle(initialValue = null).value?.value
    val categories = homeViewModel.categories
        .collectAsStateWithLifecycle(initialValue = null)
    logd("categories-home ::: ${categories.value}")
    val localBanks = homeViewModel.localUserBanks
        .collectAsStateWithLifecycle(initialValue = emptyList()).value?.toMutableStateList()
    val currencyData = homeViewModel.currency
        .collectAsStateWithLifecycle(initialValue = null)
    val contacts = homeViewModel.contacts
        .collectAsStateWithLifecycle(initialValue = emptyList())

    var transaction by remember { mutableStateOf<Transaction?>(null) }

    val showFilterSheet = remember { mutableStateOf(false) }
    val transactionFilter by homeViewModel.transactionFilter.collectAsStateWithLifecycle()

    if (currentAccount.value != null && currentAccount.value.orEmpty().isNotEmpty()) {
//        val sourceIds = currentAccount.value.orEmpty().first().sources.map { it?.id ?: 0 }
        val transactions = homeViewModel.transactions
            .collectAsStateWithLifecycle(initialValue = null)

        HomeScreen(
            accounts,
            currentAccount.value.orEmpty().first(),
            currentResource.value,
            allResource.value,
            transactions,
            expandTransaction,
            logoUrlColor,
            categories.value,
            localBanks,
            contacts,
            currencyData,
            navigateToSource,
            navigateToAccount,
            navigateToProfile,
            onShowSnackbar,
            onUpdateSelectedAccount = { id, sourcesId ->
                homeViewModel.updatedSelectedAccount(id)
                homeViewModel.updatedSelectedSources(sourcesId)
            },
            onUpdateSelectedSource = { sourcesId ->
                homeViewModel.updatedSelectedSources(sourcesId)
            },
            onTransactionChanged = {
                transaction = it
            },
            deleteWallet = {
                homeViewModel.tempRemoveWallet(it, context)
            },
            deleteAccount = {
                homeViewModel.deleteAccount(it, context = context)
            },
            editAccount = {
                navigateToAccount(it)
            },
            transactionDetails = {
                navigateToDetail(it)
            },
            newContact = {
                homeViewModel.insertContact(it)
            }
        )
    } else if (currentAccount.value != null && currentAccount.value.orEmpty().isEmpty()){
        LaunchedEffect(currentAccount.value) {
            startFromSetup()
        }
    }

    LaunchedEffect(key1 = transaction) {
        sharedViewModel.loginEventListener = object : LoginEventListener {
            override fun onBottomButtonClick() {
                handleBottomButtonClick(
                    transaction,
                    expandTransaction,
                    scope,
                    homeViewModel,
                    onShowSnackbar
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun HomeScreen(
    accounts: State<List<AccountWithSources>?>,
    currentAccount: AccountWithSources,
    currentResource: List<Wallet>?,
    allResource: List<Wallet>?,
    transactions: State<List<Transaction>?>,
    expandTransaction: MutableState<Boolean>,
    logoUrl: String? = null,
    categories: List<Category>? = null,
    localBanks: SnapshotStateList<Bank>?,
    contacts: State<List<Contact>?>,
    currency: State<Currency?>,
    navigateToSource: (id: Int, sourceId: Int?) -> Unit,
    navigateToAccount: (id: Int) -> Unit,
    navigateToProfile: () -> Unit,
    onShowSnackbar: suspend (Int, String?, Int?) -> Boolean,
    onUpdateSelectedAccount: (id: Int, sourcesId: List<Int>) -> Unit,
    onUpdateSelectedSource: (sourcesId: List<Int>) -> Unit,
    deleteAccount: (id: Int) -> Unit,
    deleteWallet: (id: Int) -> Unit,
    editAccount: (id: Int) -> Unit,
    transactionDetails: (Int) -> Unit,
    newContact: (Contact) -> Unit,
    onTransactionChanged: (Transaction) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var stickyHeaderText by remember { mutableStateOf("") }

    val showAccountSheet = remember { mutableStateOf(false) }
    val modalBottomSheetState = rememberModalBottomSheetState()

    val scrollStateKey = remember(currentAccount.accountId) { "scroll_${currentAccount.accountId}" }

    val lazyListState = rememberSaveable(
        key = scrollStateKey,
        saver = LazyListState.Saver
    ) {
        LazyListState()
    }

    // Keep track of transaction data to prevent scroll reset on data changes
    val stableTransactionData = remember(transactions.value) {
        transactions.value?.filter {
            it.type == Constants.BudgetType.EXPENSE || it.type == Constants.BudgetType.INCOME || it.type == Constants.BudgetType.TRANSFER
        }?.groupBy { it.date.toPersianDate() } ?: emptyMap()
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppBar(
                titleState = {
                    AccountSelector(
                        title = currentAccount.accountName,
                        subTitle = stringResource(
                            id = R.string.items_format, currentAccount.sources.filter { it?.isSelected.orFalse() }.size
                        ),
                        isOpen = remember { mutableStateOf(false) }
                    ) {
                        showAccountSheet.value = true
                    }
                },
                actions = listOf(
                    AppbarItem(
                        id=0,
                        imageVector = ImageVector.vectorResource(BudgetIcons.filter),
                        iconColor = MaterialTheme.colorScheme.outline
                    )
                ),
                enableNavigation = false,
                scrollBehavior = scrollBehavior,
                actionOnClick = {
                    // OnClick for open filter bottom sheet
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            state = lazyListState,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item(key = "header") {
                val init = transactions.value?.filter { it.type == Constants.BudgetType.INIT }
                    .orEmpty().sumOf { it.amount }
                val income = transactions.value?.filter { it.type == Constants.BudgetType.INCOME }
                    .orEmpty().sumOf { it.amount }
                val expense = transactions.value?.filter { it.type == Constants.BudgetType.EXPENSE }
                    .orEmpty().sumOf { it.amount }

                val currentJalaliMonth = getCurrentJalaliMonth()
                val currentMonthIncome = transactions.value?.filter {
                    it.type == Constants.BudgetType.INCOME &&
                            isTransactionInCurrentJalaliMonth(it.date, currentJalaliMonth)
                }?.sumOf { it.amount } ?: 0L

                val currentMonthExpense = transactions.value?.filter {
                    it.type == Constants.BudgetType.EXPENSE &&
                            isTransactionInCurrentJalaliMonth(it.date, currentJalaliMonth)
                }?.sumOf { it.amount } ?: 0L

                CardReport(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .padding(horizontal = 16.dp),
                    title = formatCurrency(
                        amount = init + (income - expense),
                        mask = "###,###",
                        currency = currency.value?.symbol.orEmpty(),
                        pos = 2
                    ),
                    subTitle = "موجودی همه حساب ها",
                    out = currentMonthExpense.withSuffix(),
                    inValue = currentMonthIncome.withSuffix()
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 32.dp, bottom = 16.dp),
                    text = stringResource(id = R.string.transactions),
                    style = MaterialTheme.typography.titleMedium.copy(MaterialTheme.colorScheme.outline)
                )
            }

            stableTransactionData.forEach { (date, items) ->
                stickyHeader(key = "header_$date") {
                    DateDivider(
                        modifier = Modifier
                            .onGloballyPositioned { coordinates ->
                                val position = coordinates.positionInParent()
                                stickyHeaderText = if (position.y <= 0) date else ""
                            }
                            .padding(horizontal = 24.dp)
                            .padding(top = 12.dp, bottom = 8.dp),
                        date = date,
                        amount = (items.filter { it.type == Constants.BudgetType.INCOME }.sumOf { it.amount.orDefault() }
                                - items.filter { it.type == Constants.BudgetType.EXPENSE }.sumOf { it.amount.orDefault() }),
                        type = "",
                        isCollapse = stickyHeaderText == date
                    )
                }

                items(
                    items = items,
                    key = { transaction -> "transaction_${transaction.id}" }
                ) { transaction ->
                    TransactionItem(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        title = if (transaction.name != null) transaction.name.orDefault() else if (transaction.categoryId == -1) {
                            if (transaction.type == Constants.BudgetType.INCOME) stringResource(R.string.internal_transfer_deposit)
                            else stringResource(R.string.internal_transfer_withdraw)
                        }else stringResource(R.string.untitled),
                        amount = formatCurrency(transaction.amount, "###,###", "ت", 2),
                        date = formatTimestampToTime(transaction.date),
                        divider = transaction != items.last(),
                        attached =
                            listOf(
                            if (transaction.images.orEmpty().isNotEmpty()) {
                                AttachData(Constants.AttachTyp.ATTACHED_IMAGE, transaction.images.orEmpty().size)
                            } else null
                        ),
                        type = transaction.type.orZero()
                    ) {
                        transactionDetails(transaction.id)
                    }
                }
            }

            if (stableTransactionData.isEmpty()) {
                item(key = "empty_state") {
                    Lottie(
                        modifier = Modifier.padding(horizontal = 64.dp),
                        res = R.raw.happy
                    )

                    BudgetButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .padding(horizontal = 24.dp),
                        text = stringResource(R.string.submit_first_transacton),
                        style = ButtonStyle.TextOnly
                    ) {
                        expandTransaction.value = true
                    }
                }
            }
        }
    }

    InsertScreen(
        expandTransaction,
        allResource,
        contacts.value,
        logoUrl,
        localBanks,
        categories,
        null,
        newContact = newContact,
        onShowSnackbar = onShowSnackbar,
        currency = currency
    ) {
        onTransactionChanged(it)
    }

    if (showAccountSheet.value) {
        ModalBottomSheet(
            onDismissRequest = { showAccountSheet.value = false },
            sheetState = modalBottomSheetState
        ) {
            AccountSelectorSheet(
                accounts = accounts,
                currentAccount = currentAccount,
                currentResource = currentResource,
                navigateToSource = navigateToSource,
                navigateToAccount = navigateToAccount,
                navigateToProfile = navigateToProfile,
                onShowSnackbar = onShowSnackbar,
                onUpdateSelectedAccount = onUpdateSelectedAccount,
                onUpdateSelectedSource = onUpdateSelectedSource,
                deleteWallet = deleteWallet,
                deleteAccount = deleteAccount,
                editAccount = editAccount
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}



@Composable
fun AccountSelectorSheet(
    accounts: State<List<AccountWithSources>?>,
    currentAccount: AccountWithSources,
    currentResource: List<Wallet>?,
    navigateToSource: (id: Int, sourceId: Int?) -> Unit,
    navigateToAccount: (id: Int) -> Unit,
    navigateToProfile: () -> Unit,
    onShowSnackbar: suspend (Int, String?, Int?) -> Boolean,
    onUpdateSelectedAccount: (id: Int, sourcesId: List<Int>) -> Unit,
    onUpdateSelectedSource: (sourcesId: List<Int>) -> Unit,
    deleteAccount: (id: Int) -> Unit,
    deleteWallet: (id: Int) -> Unit,
    editAccount: (id: Int) -> Unit,
) {

    var selectedAccountId by remember { mutableIntStateOf(currentAccount.accountId) }
    val selectedResources = remember { mutableStateListOf<Int>() }
    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogDiscription by remember { mutableStateOf("") }
    var selectedAccount by remember { mutableStateOf<Int?>(null) }
    var selectedWallet by remember { mutableStateOf<Int?>(null) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

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
                AccountSection(modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 8.dp),
                    account = account,
                    isCheckBox = true,
                    isAccountSelected = account.accountId == selectedAccountId,
                    backgroundColor = MaterialTheme.colorScheme.background,
                    selectedSources = selectedResources,
                    insertNewItem = {
                        navigateToSource(account.accountId, null)
                    },
                    onSourceEdit = { sourceId ->
                        navigateToSource(account.accountId, sourceId)
                    },
                    accountEndIconClick = {
                        editAccount(it)
                    },
                    onAccountSelect = { accountId ->
                        selectedAccountId = accountId
                        mAccounts.find { it.accountId == selectedAccountId }
                            ?.let { selectedAccount ->
                                selectedResources.clear()
                                selectedResources.addAll(selectedAccount.sources.map {
                                    it?.id ?: 0
                                })
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

                        if (selectedResources.contains(sourceId)) {
                            if (selectedResources.size > 1) {
                                selectedResources.remove(sourceId)
                            } else {
                                scope.launch {
                                    onShowSnackbar(R.string.err_min_resource, null, null)
                                }
                            }
                        } else {
                            selectedResources.add(sourceId)
                        }
                        onUpdateSelectedSource.invoke(selectedResources)
                    },
                    deleteSource = {
                        showDialog = true
                        dialogTitle = context.getString(R.string.delete_source)
                        dialogDiscription = context.getString(R.string.delete_source_desc)
                        selectedWallet = it
                    },
                    deleteAccount = {
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
                    })
            }
        }

        item {
            SampleAddAccountButton {
                navigateToAccount(0)
            }
            Spacer(modifier = Modifier.height(8.dp))

            SampleItem(
                modifier = Modifier.padding(horizontal = 24.dp),
                title = stringResource(id = R.string.user_account),
                iconPainter = painterResource(id = BudgetIcons.UserCircle),
                iconTint = MaterialTheme.colorScheme.outline
            ) {
                navigateToProfile.invoke()
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 24.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHighest
            )

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


fun handleBottomButtonClick(
    transaction: Transaction?,
    expandTransaction: MutableState<Boolean>,
    scope: CoroutineScope,
    viewModel: HomeViewModel,
    onShowSnackbar: suspend (Int, String?, Int?) -> Boolean,
) {
    if (expandTransaction.value) {
        when {
            transaction == null -> {
                scope.launch { onShowSnackbar(R.string.err_non_transacton, null, null) }
            }
            transaction.type == Constants.BudgetType.TRANSFER -> {
                scope.launch {
                    if (transaction.toSourceId == null) {
                        onShowSnackbar(R.string.err_input_source, null, null)
                        return@launch
                    }
                    if (transaction.sourceId == transaction.toSourceId) {
                        onShowSnackbar(R.string.err_same_wallet, null, null)
                        return@launch
                    }
                    val expense = transaction.copy(
                        type = Constants.BudgetType.EXPENSE,
                        categoryId = -1
                    )
                    viewModel.insertTransaction(expense)
                    val income = transaction.copy(
                        type = Constants.BudgetType.INCOME,
                        categoryId = -1,
                        sourceId = transaction.toSourceId!!,
                        toSourceId = transaction.sourceId!!,
                    )
                    viewModel.insertTransaction(income)
                    expandTransaction.value = false
                }
            }
            transaction.amount.orDefault() <= 0L -> {
                scope.launch { onShowSnackbar(R.string.err_amount, null, null) }
            }
            transaction.sourceId == 0 || transaction.sourceId == null -> {
                scope.launch { onShowSnackbar(R.string.err_input_source, null, null) }
            }
            transaction.categoryId == null -> {
                scope.launch { onShowSnackbar(R.string.err_input_category, null, null) }
            }
            else -> {
                viewModel.insertTransaction(transaction)
                expandTransaction.value = false
            }
        }
    } else {
        expandTransaction.value = true
    }
}
