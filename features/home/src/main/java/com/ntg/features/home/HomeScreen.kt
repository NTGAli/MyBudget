package com.ntg.features.home

import android.app.Activity
import android.content.Intent
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ntg.core.designsystem.components.AccountSection
import com.ntg.core.designsystem.components.AccountSelector
import com.ntg.core.designsystem.components.AppBar
import com.ntg.core.designsystem.components.BudgetButton
import com.ntg.core.designsystem.components.BudgetTextField
import com.ntg.core.designsystem.components.ButtonStyle
import com.ntg.core.designsystem.components.CardReport
import com.ntg.core.designsystem.components.CustomKeyboard
import com.ntg.core.designsystem.components.FullScreenBottomSheet
import com.ntg.core.designsystem.components.ImagePicker
import com.ntg.core.designsystem.components.Lottie
import com.ntg.core.designsystem.components.SampleAddAccountButton
import com.ntg.core.designsystem.components.SampleItem
import com.ntg.core.designsystem.components.SwitchText
import com.ntg.core.designsystem.components.Tag
import com.ntg.core.designsystem.components.TextDivider
import com.ntg.core.designsystem.components.TransactionItem
import com.ntg.core.designsystem.components.WheelList
import com.ntg.core.designsystem.model.SwitchItem
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.core.model.AccountWithSources
import com.ntg.core.model.SourceType
import com.ntg.core.model.SourceWithDetail
import com.ntg.core.model.Transaction
import com.ntg.core.model.res.Category
import com.ntg.core.mybudget.common.Constants
import com.ntg.core.mybudget.common.LoginEventListener
import com.ntg.core.mybudget.common.SharedViewModel
import com.ntg.core.mybudget.common.calculateExpression
import com.ntg.core.mybudget.common.formatCurrency
import com.ntg.core.mybudget.common.formatInput
import com.ntg.core.mybudget.common.generateUniqueFiveDigitId
import com.ntg.core.mybudget.common.getCurrentJalaliDate
import com.ntg.core.mybudget.common.jalaliToTimestamp
import com.ntg.core.mybudget.common.isOperator
import com.ntg.core.mybudget.common.logd
import com.ntg.core.mybudget.common.orDefault
import com.ntg.core.mybudget.common.orZero
import com.ntg.core.mybudget.common.persianDate.PersianDate
import com.ntg.core.mybudget.common.toPersianDate
import com.ntg.feature.home.R
import kotlinx.coroutines.launch
import java.time.LocalTime
import kotlin.math.exp

@Composable
fun HomeRoute(
    sharedViewModel: SharedViewModel,
    homeViewModel: HomeViewModel = hiltViewModel(),
    navigateToSource: (id: Int, sourceId: Int?) -> Unit,
    navigateToAccount: (id: Int) -> Unit,
    onShowSnackbar: suspend (Int, String?, Int?) -> Boolean,
) {
    val expandTransaction = remember { mutableStateOf(false) }
    sharedViewModel.setExpand.postValue(expandTransaction.value)
    sharedViewModel.bottomNavTitle.postValue(if (expandTransaction.value) "submit" else null)
    val scope = rememberCoroutineScope()

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
            navigateToSource,
            navigateToAccount,
            onShowSnackbar,
            onUpdateSelectedAccount = { id, sourcesId ->
                homeViewModel.updatedSelectedAccount(id)
                homeViewModel.updatedSelectedSources(sourcesId)
            },
            onUpdateSelectedSource = { sourcesId ->
                homeViewModel.updatedSelectedSources(sourcesId)
            }, onTransactionChanged = {
                transaction = it
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
                    }else if (transaction?.amount.orDefault() <= 0L){
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    accounts: State<List<AccountWithSources>?>,
    currentAccount: AccountWithSources,
    currentResource: List<SourceWithDetail>?,
    transactions: State<List<Transaction>?>,
    expandTransaction: MutableState<Boolean>,
    logoUrl: String? = null,
    categories: List<Category>? = null,
    navigateToSource: (id: Int, sourceId: Int?) -> Unit,
    navigateToAccount: (id: Int) -> Unit,
    onShowSnackbar: suspend (Int, String?, Int?) -> Boolean,
    onUpdateSelectedAccount: (id: Int, sourcesId: List<Int>) -> Unit,
    onUpdateSelectedSource: (sourcesId: List<Int>) -> Unit,
    onTransactionChanged:(Transaction) -> Unit
) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    BottomSheetScaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppBar(
                titleState = {
                    AccountSelector(
                        title = currentAccount.accountName, subTitle = stringResource(
                            id = R.string.items_format, currentAccount.sources.size
                        )
                    ) {
                        scope.launch { scaffoldState.bottomSheetState.expand() }
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
                onShowSnackbar,
                onUpdateSelectedAccount,
                onUpdateSelectedSource
            )
        }) { padding ->

        LazyColumn(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {


            item {

                val income = transactions.value?.filter { it.type == Constants.BudgetType.INCOME }
                    .orEmpty().sumOf { it.amount }
                val expense = transactions.value?.filter { it.type == Constants.BudgetType.EXPENSE }
                    .orEmpty().sumOf { it.amount }

                CardReport(modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(horizontal = 24.dp),
                    title = formatCurrency(amount = (income - expense),
                        mask = "###,###",
                        currency = "ت",
                        pos = 2),
                    subTitle = "موجودی همه حساب ها",
                    out = formatCurrency(amount = expense,
                        mask = "###,###",
                        currency = "ت",
                        pos = 2
                    ),
                    inValue = formatCurrency(amount = income,
                        mask = "###,###",
                        currency = "ت",
                        pos = 2
                    )
                )

                Text(
                    modifier = Modifier.padding(top = 16.dp, start = 32.dp, bottom = 16.dp),
                    text = stringResource(id = R.string.transactions),
                    style = MaterialTheme.typography.titleMedium.copy(MaterialTheme.colorScheme.outline)
                )
            }

            items(transactions.value.orEmpty()) {
                TransactionItem(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    title = it.name.toString(),
                    amount = formatCurrency(it.amount, "###,###", "ت", 2),
                    date = it.date.toPersianDate(),
                    divider = transactions.value?.last() != it,
                    attached = false,
                    type = it.type.orZero()
                )
            }

            if (transactions.value.orEmpty().isEmpty()) {
                item {
                    Lottie(
                        modifier = Modifier.padding(horizontal = 64.dp),
                        res = com.ntg.mybudget.core.designsystem.R.raw.happy
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

    InsertTransactionView(expandTransaction, currentResource, logoUrl, categories, onShowSnackbar) {
        onTransactionChanged(it)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsertTransactionView(
    expandTransaction: MutableState<Boolean>,
    currentResource: List<SourceWithDetail>?,
    logoUrl: String?,
    categories: List<Category>?,
    onShowSnackbar: suspend (Int, String?, Int?) -> Boolean,
    onTransactionChange: (Transaction) -> Unit
) {

    val context = LocalContext.current
    val amount = remember { mutableStateOf("") }
    val note = remember { mutableStateOf("") }
    var input by remember { mutableStateOf("") }
    var lastInput by remember { mutableStateOf("") }
    val tag = remember { mutableStateOf("") }

    var selectedSource by remember {
        mutableStateOf<SourceWithDetail?>(null)
    }

    var secondSource by remember {
        mutableStateOf<SourceWithDetail?>(null)
    }

    var selectedCategory by remember {
        mutableStateOf<Category?>(null)
    }

    val tags = remember { mutableStateListOf<String>() }
    val contacts = remember { mutableStateListOf<Contact>() }
    val images = remember { mutableStateListOf<String>() }


    var sheetType by remember { mutableIntStateOf(0) }
    var selectSourceType by remember { mutableIntStateOf(0) }
    var selectedTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    val concurrency = remember {
        mutableStateOf("تومن")
    }

    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val sheetState = rememberModalBottomSheetState()

    var opendKeyboard by remember {
        mutableStateOf(false)
    }
    val layoutDirection = LocalLayoutDirection.current

    var budgetType by remember {
        mutableIntStateOf(Constants.BudgetType.EXPENSE)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Get the contact URI from the result data
            val contactUri = result.data?.data

            contactUri?.let { uri ->
                // Query contact information from the URI
                val cursor = context.contentResolver.query(
                    uri,
                    arrayOf(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                    ),
                    null,
                    null,
                    null
                )

                cursor?.use {
                    if (it.moveToFirst()) {
                        val name =
                            it.getString(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                        val phoneNumber =
                            it.getString(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        val contact = Contact(
                            fullName = name,
                            phoneNumber = phoneNumber,
                        )

                        contacts.add(contact)
                    }
                }
            }
        }
    }


    if (amount.value.isNotEmpty()) {
        onTransactionChange(
            Transaction(
                id = 0,
                accountId = selectedSource?.accountId.orZero(),
                sourceId = selectedSource?.id.orZero(),
                categoryId = selectedCategory?.id.orZero(),
                amount = amount.value.replace(",", "").toLong(),
                type = budgetType,
                date = selectedTime,
                note = note.value,
                images = images,
                tags = tags,
                contacts = contacts
            )
        )
    }

    LaunchedEffect(expandTransaction.value) {
        if (!expandTransaction.value){
            amount.value = ""
            selectedCategory = null
            selectedSource = null
            note.value = ""
            budgetType = Constants.BudgetType.EXPENSE
            tags.clear()
            contacts.clear()
            images.clear()
        }
    }

    LaunchedEffect(key1 = budgetType) {
        selectedCategory = null
    }


    FullScreenBottomSheet(showSheet = expandTransaction,
        scrollBehavior = scrollBehavior,
        appbar = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(modifier = Modifier
                .padding(vertical = 16.dp)
                .padding(start = 12.dp),
                onClick = {
                    expandTransaction.value = false
                }) {
                Icon(
                    painter = painterResource(id = BudgetIcons.directionDown),
                    contentDescription = "close"
                )
            }

            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp),
                text = stringResource(id = R.string.new_transaction),
                style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )

            DateItem {
                selectedTime = it
            }
        }
    }) {
        Column(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
        ) {


            val items = listOf(
                SwitchItem(
                    0,
                    stringResource(id = com.ntg.mybudget.core.designsystem.R.string.outcome),
                    tint = MaterialTheme.colorScheme.onError,
                    backColor = MaterialTheme.colorScheme.error
                ),
                SwitchItem(
                    0,
                    stringResource(id = com.ntg.mybudget.core.designsystem.R.string.income),
                    tint = MaterialTheme.colorScheme.onSecondary,
                    backColor = MaterialTheme.colorScheme.secondary
                ),
                SwitchItem(
                    0,
                    stringResource(id = com.ntg.mybudget.core.designsystem.R.string.internal_transfer),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    backColor = MaterialTheme.colorScheme.primary
                ),
            )

            SwitchText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp), items = items
            ) {
                budgetType = it
            }


            // amount
            BudgetTextField(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                text = amount,
                label = stringResource(id = com.ntg.mybudget.core.designsystem.R.string.price),
                fixLeadingText = if (layoutDirection == LayoutDirection.Ltr) concurrency.value else null,
                fixTrailingText = if (layoutDirection == LayoutDirection.Rtl) concurrency.value else null,
                readOnly = true,
                onClick = {
                    sheetType = 0
                    opendKeyboard = true
                    input = amount.value.replace(",", "")
                    lastInput = ""
                }
            )

            // bank
            AnimatedVisibility(visible = budgetType != Constants.BudgetType.TRANSFER) {
                BudgetTextField(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    text = remember(selectedSource) {
                        if (selectedSource != null){
                            mutableStateOf(
                                "${(selectedSource?.sourceType as SourceType.BankCard).nativeName.orEmpty()} - ${
                                    (selectedSource?.sourceType as SourceType.BankCard).number.takeLast(
                                        4
                                    )
                                }"
                            )
                        }else{
                            mutableStateOf("")
                        }
                    },
                    label = stringResource(id = com.ntg.mybudget.core.designsystem.R.string.source_expenditure),
                    trailingIcon = painterResource(id = BudgetIcons.directionLeft),
                    readOnly = true,
                    onClick = {
                        sheetType = 1
                        selectSourceType = 1
                        opendKeyboard = true
                    }
                )
            }


            AnimatedVisibility(visible = budgetType == Constants.BudgetType.TRANSFER) {
                Column {
                    BudgetTextField(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        text = remember(selectedSource) {
                            if (selectedSource != null){
                                mutableStateOf(
                                    "${(selectedSource?.sourceType as SourceType.BankCard).nativeName.orEmpty()} - ${
                                        (selectedSource?.sourceType as SourceType.BankCard).number.takeLast(
                                            4
                                        )
                                    }"
                                )
                            }else{
                                mutableStateOf("")
                            }
                        },
                        label = stringResource(id = com.ntg.mybudget.core.designsystem.R.string.from),
                        trailingIcon = painterResource(id = BudgetIcons.directionLeft),
                        readOnly = true,
                        onClick = {
                            sheetType = 1
                            selectSourceType = 1
                            opendKeyboard = true
                        }
                    )

                    BudgetTextField(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        text = remember(secondSource) {
                            if (secondSource != null){
                                mutableStateOf(
                                    "${(secondSource?.sourceType as SourceType.BankCard).nativeName.orEmpty()} - ${
                                        (secondSource?.sourceType as SourceType.BankCard).number.takeLast(
                                            4
                                        )
                                    }"
                                )
                            }else{
                                mutableStateOf("")
                            }
                        },
                        label = stringResource(id = com.ntg.mybudget.core.designsystem.R.string.to),
                        trailingIcon = painterResource(id = BudgetIcons.directionLeft),
                        readOnly = true,
                        onClick = {
                            sheetType = 1
                            selectSourceType = 2
                            opendKeyboard = true
                        }
                    )

                    BudgetTextField(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        text = amount,
                        label = stringResource(id = com.ntg.mybudget.core.designsystem.R.string.fee),
                        fixLeadingText = if (layoutDirection == LayoutDirection.Ltr) concurrency.value else null,
                        fixTrailingText = if (layoutDirection == LayoutDirection.Rtl) concurrency.value else null,
                        readOnly = true,
                        onClick = {
                            sheetType = 0
                            opendKeyboard = true
                            input = amount.value
                            lastInput = amount.value
                        }
                    )
                }
            }

            AnimatedVisibility(visible = budgetType != Constants.BudgetType.TRANSFER) {
                //category
                BudgetTextField(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    text = remember(selectedCategory) { mutableStateOf(selectedCategory?.name.orEmpty()) },
                    label = stringResource(id = com.ntg.mybudget.core.designsystem.R.string.catgory),
                    trailingIcon = painterResource(id = BudgetIcons.directionLeft),
                    readOnly = true,
                    onClick = {
                        sheetType = 2
                        opendKeyboard = true
                    }
                )
            }

            // tags
            TextDivider(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                title = stringResource(id = R.string.tags)
            )

            BudgetTextField(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                text = tag,
                label = stringResource(id = R.string.tags),
                trailingIcon = painterResource(id = BudgetIcons.Add),
                trailingIconOnClick = {
                    if (it.isNotEmpty()) {
                        tags.add(it)
                    }
                    tag.value = ""
                }
            )

            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(top = 8.dp)
                    .padding(horizontal = 24.dp),
            ) {
                tags.forEach {
                    Tag(
                        modifier = Modifier.padding(end = 8.dp),
                        text = it,
                        dismissClick = {
                            tags.remove(it)
                        }
                    ) {
                        tags.remove(it)
                    }
                }
            }

            // people
            TextDivider(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                title = stringResource(id = R.string.people)
            )

            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(top = 16.dp)
                    .padding(horizontal = 24.dp)
            ) {


                Tag(
                    modifier = Modifier.padding(end = 8.dp),
                    icon = painterResource(id = BudgetIcons.Add)
                ) {
                    val intent = Intent(Intent.ACTION_PICK).apply {
                        type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
                    }
                    launcher.launch(intent)
                }

                contacts.forEach {
                    Tag(
                        modifier = Modifier.padding(end = 8.dp),
                        text = it.fullName,
                        dismissClick = {
                            contacts.remove(it)
                        }
                    ) {
                        contacts.remove(it)
                    }
                }

            }


            // images
            TextDivider(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                title = stringResource(id = R.string.images)
            )

            ImagePicker(
                modifier = Modifier.padding(top = 16.dp),
                padding = PaddingValues(horizontal = 24.dp)
            ){
                if (it.isNotEmpty()) {
                    images.addAll(it)
                }
            }


            // description
            BudgetTextField(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(horizontal = 24.dp),
                singleLine = false,
                label = stringResource(id = R.string.description),
                text = note,
                maxLines = 5,
                minLines = 5
            )

            Spacer(modifier = Modifier.padding(vertical = 24.dp))

        }
    }

    LaunchedEffect(key1 = sheetState.currentValue) {
        opendKeyboard = sheetState.isVisible
    }

    if (opendKeyboard) {

        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                opendKeyboard = false
            }) {

            when (sheetType) {
                //calculator
                0 -> {
                    val isExpressionComplete = remember(input, lastInput) {
                        input.isNotEmpty() && (if (input.last()
                                .isDigit()
                        ) input else input.dropLast(1)) != lastInput.replace(",", "")
                    }

                    // Font size based on the condition
                    val topTextFontSize by animateIntAsState(
                        targetValue = if (isExpressionComplete) MaterialTheme.typography.bodyLarge.fontSize.value.toInt() else MaterialTheme.typography.titleLarge.fontSize.value.toInt(),
                        label = ""
                    )

                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Text(
                            text = formatInput(input),
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .padding(horizontal = 4.dp)
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = topTextFontSize.sp, textDirection = TextDirection.Ltr
                            )
                        )

                        if (isExpressionComplete && calculateExpression(input) != null) {
                            lastInput = calculateExpression(input).toString()
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                Row(
                                    modifier = Modifier.padding(bottom = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Icon(
                                        painter = painterResource(id = BudgetIcons.equal),
                                        contentDescription = "equal"
                                    )

                                    Text(
                                        text = formatInput(
                                            lastInput
                                        ),
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            textDirection = TextDirection.Rtl,
                                            textAlign = TextAlign.End
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                    )
                                }
                            }
                        }

                        CustomKeyboard(onKeyPressed = { key ->
                            if (input.isNotEmpty()) {
                                if (isOperator(input.last()) && isOperator(key.last())) {
                                    input = input.dropLast(1)
                                    input += key
                                    return@CustomKeyboard
                                }
                            } else {
                                if (isOperator(key.last())) return@CustomKeyboard
                            }
                            input += key
                        }, onBackspace = {
                            if (input.isNotEmpty()) {
                                input = input.dropLast(1)
                            }
                        }, onConfirm = {
                            if (lastInput.toLong() > 0L) {
                                amount.value = formatInput(lastInput)
                                scope.launch {
                                    sheetState.hide()
                                }
                            } else {
                                scope.launch {
                                    onShowSnackbar(R.string.err_negetive_number, null, null)
                                }
                            }
                        })
                    }
                }

                1 -> {

                    LazyColumn {
                        items(currentResource.orEmpty()) {
                            var logoName = ""
                            val data = if (it.sourceType is SourceType.BankCard) {
                                logoName = (it.sourceType as SourceType.BankCard).logoName.orEmpty()
                                "${(it.sourceType as SourceType.BankCard).nativeName.orEmpty()} - ${
                                    (it.sourceType as SourceType.BankCard).number.takeLast(
                                        4
                                    )
                                }"
                            } else ""

                            SampleItem(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                title = data, setRadio = true,
                                imageUrl = if (logoUrl.orEmpty()
                                        .isNotEmpty() && logoName.isNotEmpty()
                                ) {
                                    "${logoUrl}${logoName}.svg"
                                } else null,
                                isRadioCheck = if (selectSourceType == 1) selectedSource == it else secondSource == it
                            ) {
                                if (selectSourceType == 1){
                                    selectedSource = it
                                }else{
                                    secondSource = it
                                }
                                scope.launch {
                                    sheetState.hide()
                                }
                            }
                        }
                    }

                }

                2 -> {
                    LazyColumn {

//                        item {
//                            Text(
//                                modifier = Modifier
//                                    .padding(start = 16.dp)
//                                    .padding(vertical = 8.dp),
//                                text = stringResource(id = com.ntg.mybudget.core.designsystem.R.string.expenses),
//                                style = MaterialTheme.typography.titleMedium
//                            )
//                        }

                        items(categories.orEmpty().filter { it.type == budgetType }) {
                            SampleItem(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                title = it.name, setRadio = true,
                                isRadioCheck = selectedCategory == it
                            ) {
                                selectedCategory = it
                                scope.launch {
                                    sheetState.hide()
                                }
                            }
                        }

//                        item {
//                            Text(
//                                modifier = Modifier
//                                    .padding(start = 16.dp)
//                                    .padding(vertical = 8.dp),
//                                text = stringResource(id = com.ntg.mybudget.core.designsystem.R.string.income),
//                                style = MaterialTheme.typography.titleMedium
//                            )
//                        }
//
//                        items(categories.orEmpty().filter { it.type == 1 }) {
//                            SampleItem(
//                                modifier = Modifier.padding(horizontal = 8.dp),
//                                title = it.name, setRadio = true,
//                                isRadioCheck = selectedCategory == it
//                            ) {
//                                selectedCategory = it
//                                scope.launch {
//                                    sheetState.hide()
//                                }
//                            }
//                        }

                    }
                }

            }

        }
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
        selectedDateState.add(1, months[getCurrentJalaliDate().second])
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
                    text = stringResource(id = com.ntg.mybudget.core.designsystem.R.string.submit)
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
    currentResource: List<SourceWithDetail>?,
    navigateToSource: (id: Int, sourceId: Int?) -> Unit,
    navigateToAccount: (id: Int) -> Unit,
    onShowSnackbar: suspend (Int, String?, Int?) -> Boolean,
    onUpdateSelectedAccount: (id: Int, sourcesId: List<Int>) -> Unit,
    onUpdateSelectedSource: (sourcesId: List<Int>) -> Unit
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
                AccountSection(modifier = Modifier
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
}


