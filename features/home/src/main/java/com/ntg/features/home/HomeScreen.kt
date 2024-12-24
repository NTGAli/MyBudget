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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.core.model.AccountWithSources
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
import com.ntg.core.mybudget.common.jalaliToTimestamp
import com.ntg.core.mybudget.common.orDefault
import com.ntg.core.mybudget.common.orZero
import com.ntg.core.mybudget.common.persianDate.PersianDate
import com.ntg.core.mybudget.common.toPersianDate
import com.ntg.mybudget.core.designsystem.R
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
    onShowSnackbar: suspend (Int, String?, Int?) -> Boolean,
) {
    val expandTransaction = remember { mutableStateOf(false) }
    sharedViewModel.setExpand.postValue(expandTransaction.value)
    sharedViewModel.bottomNavTitle.postValue(if (expandTransaction.value) "submit" else null)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val accounts =
        homeViewModel.accountWithSources().collectAsStateWithLifecycle(initialValue = emptyList())
    val currentAccount =
        homeViewModel.selectedAccount().collectAsStateWithLifecycle(initialValue = null)
    val currentResource =
        homeViewModel.selectedSources().collectAsStateWithLifecycle(initialValue = emptyList())
    val logoUrlColor =
        homeViewModel.getBankLogoColor()
            .collectAsStateWithLifecycle(initialValue = null).value?.value
    val categories = homeViewModel.getCategories().collectAsStateWithLifecycle(initialValue = null)
    val localBanks =
        homeViewModel.getLocalUserBanks().collectAsStateWithLifecycle(initialValue = emptyList()).value?.toMutableStateList()
    val currencyData = homeViewModel.currencyInfo().collectAsStateWithLifecycle(null)
    val contacts = homeViewModel.getContacts().collectAsStateWithLifecycle()

    var transaction by remember { mutableStateOf<Transaction?>(null) }

    if (currentAccount.value != null && currentAccount.value.orEmpty().isNotEmpty()) {
        val sourceIds = currentAccount.value.orEmpty().first().sources.map { it?.id ?: 0 }

        val transactions =
            homeViewModel.transactions(sourceIds).collectAsStateWithLifecycle(initialValue = null)
        HomeScreen(accounts,
            currentAccount.value.orEmpty().first(),
            currentResource.value,
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
            }, onTransactionChanged = {
                transaction = it
            }, deleteWallet = {
                homeViewModel.tempRemoveWallet(it, context)
            }, deleteAccount = {
                homeViewModel.deleteAccount(it, context = context)
            }, editAccount = {
                navigateToAccount(it)
            }, transactionDetails = {
                navigateToDetail(it)
            }, newContact = {
                homeViewModel.insertContact(it)
            })
    }

    LaunchedEffect(key1 = transaction) {
        sharedViewModel.loginEventListener = object : LoginEventListener {
            override fun onBottomButtonClick() {

                if (expandTransaction.value){
                    if (transaction == null){
                        scope.launch {
                            onShowSnackbar(R.string.err_non_transacton, null, null)
                        }
                    }
                    else if (transaction?.type == Constants.BudgetType.TRANSFER){
                        scope.launch {
//                            onShowSnackbar(R.string.err_not_impelemnted, null, com.ntg.mybudget.core.designsystem.R.raw.shy2)
                        }
                    }
                    else if (transaction?.amount.orDefault() <= 0L){
                        scope.launch {
                            onShowSnackbar(R.string.err_amount, null, null)
                        }
                    }else if (transaction?.sourceId == 0 || transaction?.sourceId == null){
                        scope.launch {
                            onShowSnackbar(R.string.err_input_source, null, null)
                        }
                    }else if (transaction?.categoryId == null){
                        scope.launch {
                            onShowSnackbar(R.string.err_input_category, null, null)
                        }
                    }else{
                        homeViewModel.insertTransaction(transaction!!)
                        expandTransaction.value = false
                    }
                }else{
                    expandTransaction.value = true
                }


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
    transactions: State<List<Transaction>?>,
    expandTransaction: MutableState<Boolean>,
    logoUrl: String? = null,
    categories: List<Category>? = null,
    localBanks:SnapshotStateList<Bank>?,
    contacts: State<List<Contact>?>,
    currency : State<Currency?>,
    navigateToSource: (id: Int, sourceId: Int?) -> Unit,
    navigateToAccount: (id: Int) -> Unit,
    navigateToProfile: () -> Unit,
    onShowSnackbar: suspend (Int, String?, Int?) -> Boolean,
    onUpdateSelectedAccount: (id: Int, sourcesId: List<Int>) -> Unit,
    onUpdateSelectedSource: (sourcesId: List<Int>) -> Unit,
    deleteAccount: (id: Int) -> Unit,
    deleteWallet: (id: Int) -> Unit,
    editAccount: (id: Int) -> Unit,
    transactionDetails:(Int) -> Unit,
    newContact: (Contact) -> Unit,
    onTransactionChanged:(Transaction) -> Unit,
) {

    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            skipHiddenState = false
        )
    )

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var stickyHeaderText by remember { mutableStateOf("") }
    val isAccountSheetOpen = remember { mutableStateOf(false) }

    LaunchedEffect(scaffoldState.bottomSheetState.targetValue) {
        if (scaffoldState.bottomSheetState.targetValue == SheetValue.PartiallyExpanded){
            isAccountSheetOpen.value = false
        }
    }

    BottomSheetScaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppBar(
                titleState = {
                    AccountSelector(
                        title = currentAccount.accountName, subTitle = stringResource(
                            id = R.string.items_format, currentAccount.sources.size
                        ),
                        isOpen = isAccountSheetOpen
                    ) {
                        scope.launch { scaffoldState.bottomSheetState.expand() }
                        isAccountSheetOpen.value = true
                    }
                }, enableNavigation = false,
                scrollBehavior = scrollBehavior
            )
        },
        sheetTonalElevation = 0.dp,
        sheetContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            AccountSelectorSheet(
                accounts,
                currentAccount,
                currentResource,
                navigateToSource,
                navigateToAccount,
                navigateToProfile,
                onShowSnackbar,
                onUpdateSelectedAccount,
                onUpdateSelectedSource,
                deleteWallet = {
                    deleteWallet(it)
                },
                deleteAccount = {
                    deleteAccount(it)
                },
                editAccount = {
                    editAccount(it)
                }
            )
        }) { padding ->

        LazyColumn(
            Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            item {

                val init = transactions.value?.filter { it.type == Constants.BudgetType.INIT }
                    .orEmpty().sumOf { it.amount }
                val income = transactions.value?.filter { it.type == Constants.BudgetType.INCOME }
                    .orEmpty().sumOf { it.amount }
                val expense = transactions.value?.filter { it.type == Constants.BudgetType.EXPENSE }
                    .orEmpty().sumOf { it.amount }

                CardReport(modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(horizontal = 24.dp),
                    title = formatCurrency(amount =  init + (income - expense),
                        mask = "###,###",
                        currency = currency.value?.symbol.orEmpty(),
                        pos = 2),
                    subTitle = "موجودی همه حساب ها",
                    out = formatCurrency(amount = expense,
                        mask = "###,###",
                        currency = currency.value?.symbol.orEmpty(),
                        pos = 2
                    ),
                    inValue = formatCurrency(amount = income,
                        mask = "###,###",
                        currency = currency.value?.symbol.orEmpty(),
                        pos = 2
                    )
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 32.dp, bottom = 16.dp),
                    text = stringResource(id = R.string.transactions),
                    style = MaterialTheme.typography.titleMedium.copy(MaterialTheme.colorScheme.outline)
                )
            }


            val groupedItems = transactions.value.orEmpty()
                .filter { it.type == Constants.BudgetType.EXPENSE || it.type == Constants.BudgetType.INCOME }
                .groupBy  { it.date.toPersianDate() }


            groupedItems.forEach { (date, items) ->
                stickyHeader {
                    DateDivider(
                        modifier = Modifier
                            .onGloballyPositioned { coordinates ->
                                val position = coordinates.positionInParent()
                                stickyHeaderText = if (position.y <= 0) date
                                else ""
                            }
                            .padding(horizontal = 24.dp).padding(top = 12.dp, bottom = 8.dp),
                        date = date, amount = (items.filter { it.type == Constants.BudgetType.INCOME }.sumOf { it.amount.orDefault() }
                                - items.filter { it.type == Constants.BudgetType.EXPENSE }.sumOf { it.amount.orDefault() }), type = "",
                        isCollapse = stickyHeaderText == date
                    )
                }

                items(items){
                    TransactionItem(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        title = it.name.toString(),
                        amount = formatCurrency(it.amount, "###,###", "ت", 2),
                        date = formatTimestampToTime(it.date),
                        divider = it != items.last(),
                        attached = false,
                        type = it.type.orZero()
                    ){
                        transactionDetails(it.id)
                    }
                }
            }

            if (groupedItems.isEmpty()) {
                item {
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

    InsertScreen(expandTransaction, currentResource,contacts.value, logoUrl, localBanks, categories,null, newContact = newContact, onShowSnackbar =  onShowSnackbar) {
        onTransactionChanged(it)
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateItem(
    modifier: Modifier = Modifier,
    onChangeTime: (Long) -> Unit
) {

    var openSheet by remember {
        mutableStateOf(false)
    }

    var selectedDate by remember {
        mutableStateOf("")
    }

    var selectedTime by remember {
        mutableStateOf("")
    }

    val selectedDateState = remember {
        mutableStateListOf<String>()
    }

    val selectedTimeState = remember {
        mutableStateListOf<String>()
    }

    var daysInMonth by remember {
        mutableIntStateOf(31)
    }

    var currentYear by remember {
        mutableIntStateOf(1400)
    }

    var type by remember {
        mutableStateOf(0)
    }

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    Row(
        modifier = modifier
            .padding(end = 16.dp)
            .background(
                shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.surfaceContainer
            )
            .clip(RoundedCornerShape(4.dp))
            .clickable {
                openSheet = true
            }
            .padding(vertical = 2.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = selectedDate,
            style = MaterialTheme.typography.labelMedium.copy(MaterialTheme.colorScheme.onSurfaceVariant)
        )
        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .size(4.dp)
                .background(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceDim)
        )

        Text(
            text = selectedTime,
            style = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textDirection = TextDirection.Ltr
            )
        )

        Icon(
            painter = painterResource(id = BudgetIcons.CalenderTick),
            contentDescription = "calender",
            tint = MaterialTheme.colorScheme.outline
        )
    }

    val months = listOf(
        "فروردین",
        "اردیبهشت",
        "خرداد",
        "تیر",
        "مرداد",
        "شهریور",
        "مهر",
        "آبان",
        "آذر",
        "دی",
        "بهمن",
        "اسفند"
    )



    LaunchedEffect(key1 = sheetState.currentValue) {
        openSheet = sheetState.isVisible
    }

    LaunchedEffect(key1 = Unit) {
        selectedDateState.add(0, getCurrentJalaliDate().first.toString())
        selectedDateState.add(1, months[getCurrentJalaliDate().second - 1])
        selectedDateState.add(2, getCurrentJalaliDate().third.toString())
        selectedDate = "${selectedDateState[2]} ${selectedDateState[1]} ${selectedDateState[0]}"
        currentYear = getCurrentJalaliDate().first

        val currentTime = LocalTime.now()
        selectedTimeState.add(0, currentTime.hour.toString())
        selectedTimeState.add(1, currentTime.minute.toString())
        selectedTime = "${selectedTimeState[0]} : ${selectedTimeState[1]}"

        onChangeTime(
            jalaliToTimestamp(
                year = selectedDateState[0].toInt(),
                month = months.indexOfFirst { it == selectedDateState[1] }+1,
                day = selectedDateState[2].toInt(),
                hour = selectedTimeState[0].toInt(),
                minute = selectedTimeState[1].toInt()
            )
        )
    }

    if (selectedDateState.isNotEmpty()) {
        LaunchedEffect(key1 = selectedDateState[1]) {
            daysInMonth = when (months.indexOfFirst { it == selectedDateState[1] } + 1) {
                in 1..6 -> 31
                in 7..11 -> 30
                12 -> if (PersianDate().isLeap(selectedDateState[0].toInt())) 30 else 29
                else -> throw IllegalArgumentException("Invalid month in Jalali calendar")
            }
        }
    }

    if (openSheet) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { openSheet = false }) {

            Column {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Row(
                        modifier = Modifier.padding(vertical = 24.dp)
                    ) {

                        if (type == 0) {
                            // year
                            WheelList(
                                modifier = Modifier.weight(1f),
                                items = (currentYear - 5..currentYear).toList(),
                                initialItem = selectedDateState[0].toInt(),
                                onItemSelected = { i, item ->
                                    selectedDateState.removeAt(0)
                                    selectedDateState.add(
                                        0,
                                        (currentYear - 5..currentYear).toList()[i].toString()
                                    )
                                }
                            )

                            //month
                            WheelList(
                                modifier = Modifier.weight(1f),
                                items = months,
                                initialItem = selectedDateState[1],
                                onItemSelected = { i, item ->
                                    selectedDateState.removeAt(1)
                                    selectedDateState.add(1, months[i])
                                }
                            )

                            //day
                            WheelList(
                                modifier = Modifier.weight(1f),
                                items = (1..daysInMonth).toList(),
                                initialItem = selectedDateState[2].toInt(),
                                onItemSelected = { i, item ->
                                    selectedDateState.removeAt(2)
                                    selectedDateState.add(2, (1..31).toList()[i].toString())
                                }
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                            WheelList(
                                modifier = Modifier.weight(1f),
                                items = (1..24).toList(),
                                initialItem = selectedTimeState[0].toInt(),
                                onItemSelected = { i, item ->
                                    selectedTimeState.removeAt(0)
                                    selectedTimeState.add(0, item.toString())
                                }
                            )

                            WheelList(
                                modifier = Modifier.weight(1f),
                                items = (1..59).toList(),
                                initialItem = selectedTimeState[1].toInt(),
                                onItemSelected = { i, item ->
                                    selectedTimeState.removeAt(1)
                                    selectedTimeState.add(1, item.toString())
                                }
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                BudgetButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .padding(bottom = 24.dp),
                    text = stringResource(id = R.string.submit)
                ) {
                    selectedDate =
                        "${selectedDateState[2]} ${selectedDateState[1]} ${selectedDateState[0]}"
                    selectedTime = "${selectedTimeState[0]} : ${selectedTimeState[1]}"
                    if (type == 1) {
                        onChangeTime(
                            jalaliToTimestamp(
                                year = selectedDateState[0].toInt(),
                                month = months.indexOfFirst { it == selectedDateState[1] }+1,
                                day = selectedDateState[2].toInt(),
                                hour = selectedTimeState[0].toInt(),
                                minute = selectedTimeState[1].toInt()
                            )
                        )
                        scope.launch {
                            type = 0
                            sheetState.hide()
                        }
                        return@BudgetButton
                    }
                    onChangeTime(
                        jalaliToTimestamp(
                            year = selectedDateState[0].toInt(),
                            month = months.indexOfFirst { it == selectedDateState[1] }+1,
                            day = selectedDateState[2].toInt(),
                            hour = selectedTimeState[0].toInt(),
                            minute = selectedTimeState[1].toInt()
                        )
                    )
                    type = 1
                }
            }


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


