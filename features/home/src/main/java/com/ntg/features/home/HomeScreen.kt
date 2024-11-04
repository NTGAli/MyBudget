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
import com.ntg.core.designsystem.components.CardReport
import com.ntg.core.designsystem.components.CustomKeyboard
import com.ntg.core.designsystem.components.FullScreenBottomSheet
import com.ntg.core.designsystem.components.ImagePicker
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
import com.ntg.core.mybudget.common.getCurrentJalaliDate
import com.ntg.core.mybudget.common.logd
import com.ntg.core.mybudget.common.orDefault
import com.ntg.core.mybudget.common.persianDate.PersianDate
import com.ntg.core.mybudget.common.toPersianDate
import com.ntg.feature.home.R
import kotlinx.coroutines.launch
import java.time.LocalTime

@Composable
fun HomeRoute(
    sharedViewModel: SharedViewModel,
    homeViewModel: HomeViewModel = hiltViewModel(),
    navigateToSource: (id: Int, sourceId: Int?) -> Unit,
    navigateToAccount: (id: Int) -> Unit,
    onShowSnackbar: suspend (Int, String?) -> Boolean,
) {
    val expandTransaction = remember { mutableStateOf(false) }
    sharedViewModel.setExpand.postValue(expandTransaction.value)
    sharedViewModel.bottomNavTitle.postValue(if (expandTransaction.value) "submit" else null)


//    LaunchedEffect(Unit) {
//        (1..10).toList().forEachIndexed { index, i ->
//            homeViewModel.insertTransaction(
//                Transaction(
//                    id = 0,
//                    sId = "",
//                    accountId = 10001,
//                    sourceId = 10002,
//                    amount = (2*index).toLong(),
//                    categoryId = 1,
//                    type = Constants.BudgetType.EXPENSE.toString(),
//                    date = System.currentTimeMillis(),
//                    note = "478787"
//                )
//            )
//        }
//
//    }

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
            })
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
    logoUrl: String? = null,
    categories: List<Category>? = null,
    navigateToSource: (id: Int, sourceId: Int?) -> Unit,
    navigateToAccount: (id: Int) -> Unit,
    onShowSnackbar: suspend (Int, String?) -> Boolean,
    onUpdateSelectedAccount: (id: Int, sourcesId: List<Int>) -> Unit,
    onUpdateSelectedSource: (sourcesId: List<Int>) -> Unit
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
                CardReport(modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(horizontal = 24.dp),
                    title = formatCurrency(amount = transactions.value?.sumOf { it.amount } ?: 0L,
                        mask = "###,###",
                        currency = "ت",
                        pos = 2),
                    subTitle = "موجودی همه حساب ها",
                    out = formatCurrency(amount = transactions.value?.filter { it.type == "out" }
                        .orEmpty().sumOf { it.amount },
                        mask = "###,###",
                        currency = "ت",
                        pos = 2
                    ),
                    inValue = formatCurrency(amount = transactions.value?.filter { it.type == "in" }
                        .orEmpty().sumOf { it.amount },
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

            items(transactions.value.orEmpty()){
                TransactionItem(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    title = it.name.toString(),
                    amount = formatCurrency(it.amount, "###,###", "ت", 2),
                    date = it.date.toPersianDate(),
                    divider = transactions.value?.last() != it,
                    attached = false,
                    type = it.type.orDefault().toInt()
                )
            }

        }


    }

    InsertTransactionView(expandTransaction, currentResource, logoUrl, categories)

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsertTransactionView(
    expandTransaction: MutableState<Boolean>,
    currentResource: List<SourceWithDetail>?,
    logoUrl: String?,
    categories: List<Category>?
) {

    val context = LocalContext.current
    val balance = remember { mutableStateOf("") }
    var input by remember { mutableStateOf("") }
    var lastInput by remember { mutableStateOf("") }
    val tag = remember { mutableStateOf("") }

    var selectedSource by remember {
        mutableStateOf<SourceWithDetail?>(null)
    }

    var selectedCategory by remember {
        mutableStateOf<Category?>(null)
    }

    val tags = remember { mutableStateListOf<String>() }
    val contacts = remember { mutableStateListOf<String>() }


    var sheetType by remember { mutableIntStateOf(0) }

    val concurrency = remember {
        mutableStateOf("تومن")
    }

    val scope = rememberCoroutineScope()

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
                        contacts.add(name)
                    }
                }
            }
        }
    }



    FullScreenBottomSheet(showSheet = expandTransaction, appbar = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(modifier = Modifier
                .padding(vertical = 16.dp)
                .padding(start = 16.dp),
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

            DateItem(unixTime = 123L)
        }
    }) {
        Column(
            modifier = Modifier
//            .padding(it)
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


            // price
            BudgetTextField(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                text = balance,
                label = stringResource(id = com.ntg.mybudget.core.designsystem.R.string.price),
                fixLeadingText = if (layoutDirection == LayoutDirection.Ltr) concurrency.value else null,
                fixTrailingText = if (layoutDirection == LayoutDirection.Rtl) concurrency.value else null,
                readOnly = true,
                onClick = {
                    sheetType = 0
                    opendKeyboard = true
                    input = balance.value
                    lastInput = balance.value
                }
            )

            // bank
            AnimatedVisibility(visible = budgetType != Constants.BudgetType.TRANSFER) {
                BudgetTextField(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    text = try {
                        mutableStateOf(
                            "${(selectedSource?.sourceType as SourceType.BankCard).nativeName.orEmpty()} - ${
                                (selectedSource?.sourceType as SourceType.BankCard).number.takeLast(
                                    4
                                )
                            }"
                        )
                    } catch (e: Exception) {
                        mutableStateOf("")
                    },
                    label = stringResource(id = com.ntg.mybudget.core.designsystem.R.string.source_expenditure),
                    trailingIcon = painterResource(id = BudgetIcons.directionLeft),
                    readOnly = true,
                    onClick = {
                        sheetType = 1
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
                        text = try {
                            mutableStateOf(
                                "${(selectedSource?.sourceType as SourceType.BankCard).nativeName.orEmpty()} - ${
                                    (selectedSource?.sourceType as SourceType.BankCard).number.takeLast(
                                        4
                                    )
                                }"
                            )
                        } catch (e: Exception) {
                            mutableStateOf("")
                        },
                        label = stringResource(id = com.ntg.mybudget.core.designsystem.R.string.from),
                        trailingIcon = painterResource(id = BudgetIcons.directionLeft),
                        readOnly = true,
                        onClick = {
                            sheetType = 1
                            opendKeyboard = true
                        }
                    )

                    BudgetTextField(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        text = try {
                            mutableStateOf(
                                "${(selectedSource?.sourceType as SourceType.BankCard).nativeName.orEmpty()} - ${
                                    (selectedSource?.sourceType as SourceType.BankCard).number.takeLast(
                                        4
                                    )
                                }"
                            )
                        } catch (e: Exception) {
                            mutableStateOf("")
                        },
                        label = stringResource(id = com.ntg.mybudget.core.designsystem.R.string.to),
                        trailingIcon = painterResource(id = BudgetIcons.directionLeft),
                        readOnly = true,
                        onClick = {
                            sheetType = 1
                            opendKeyboard = true
                        }
                    )

                    BudgetTextField(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        text = balance,
                        label = stringResource(id = com.ntg.mybudget.core.designsystem.R.string.fee),
                        fixLeadingText = if (layoutDirection == LayoutDirection.Ltr) concurrency.value else null,
                        fixTrailingText = if (layoutDirection == LayoutDirection.Rtl) concurrency.value else null,
                        readOnly = true,
                        onClick = {
                            sheetType = 0
                            opendKeyboard = true
                            input = balance.value
                            lastInput = balance.value
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
                    text = mutableStateOf(selectedCategory?.name.orEmpty()),
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
                        text = it,
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
            )


            // description
            BudgetTextField(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(horizontal = 24.dp),
                singleLine = false,
                label = stringResource(id = R.string.description)
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
                0 -> {
                    val isExpressionComplete = remember(input, lastInput) {
                        logd("isExpressionComplete :: $input --- $lastInput")
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

                        if (isExpressionComplete) {
                            lastInput = formatInput(
                                calculateExpression(
                                    (if (input.last()
                                            .isDigit()
                                    ) input else input.dropLast(1)).replace(
                                        ",", ""
                                    )
                                ).toString()
                            )

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
                                            calculateExpression(
                                                lastInput.replace(
                                                    ",", ""
                                                )
                                            ).toString()
                                        ),
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                    )
                                }
                            }
                        }

                        CustomKeyboard(onKeyPressed = { key ->
                            input += key
                        }, onBackspace = {
                            if (input.isNotEmpty()) {
                                input = input.dropLast(1)
                            }
                        }, onConfirm = {
                            balance.value = lastInput
                            scope.launch {
                                sheetState.hide()
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
                                isRadioCheck = selectedSource == it
                            ) {
                                selectedSource = it
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
    modifier: Modifier = Modifier, unixTime: Long
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
            style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, textDirection = TextDirection.Ltr)
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
        selectedTimeState.add(0,currentTime.hour.toString())
        selectedTimeState.add(1, currentTime.minute.toString())
        selectedTime = "${selectedTimeState[0]} : ${selectedTimeState[1]}"
    }

    if (selectedDateState.isNotEmpty()){
        LaunchedEffect(key1 = selectedDateState[1]) {
            daysInMonth = when (months.indexOfFirst { it == selectedDateState[1] }+1) {
                in 1..6 -> 31
                in 7..11 -> 30
                12 -> if (PersianDate().isLeap(selectedDateState[0].toInt())) 30 else 29
                else -> throw IllegalArgumentException("Invalid month in Jalali calendar")
            }
        }
    }

    if (openSheet){
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { openSheet = false }) {

            Column {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Row(
                        modifier = Modifier.padding(vertical = 24.dp)
                    ) {

                        if (type == 0){
                            WheelList(
                                modifier = Modifier.weight(1f),
                                items = (currentYear-5..currentYear).toList(),
                                initialItem = selectedDateState[0].toInt(),
                                onItemSelected = { i, item ->
                                    selectedDateState.removeAt(0)
                                    selectedDateState.add(0, (currentYear-5..currentYear).toList()[i].toString())
                                }
                            )

                            WheelList(
                                modifier = Modifier.weight(1f),
                                items = months,
                                initialItem = selectedDateState[1],
                                onItemSelected = { i, item ->
                                    selectedDateState.removeAt(1)
                                    selectedDateState.add(1, months[i])
                                }
                            )


                            WheelList(
                                modifier = Modifier.weight(1f),
                                items = (1..daysInMonth).toList(),
                                initialItem = selectedDateState[2].toInt(),
                                onItemSelected = { i, item ->
                                    selectedDateState.removeAt(2)
                                    selectedDateState.add(2, (1..31).toList()[i].toString())
                                }
                            )
                        }else{
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
                    text = stringResource(id = com.ntg.mybudget.core.designsystem.R.string.submit)){
                    selectedDate = "${selectedDateState[2]} ${selectedDateState[1]} ${selectedDateState[0]}"
                    selectedTime = "${selectedTimeState[0]} : ${selectedTimeState[1]}"
                    if (type == 1){
                        scope.launch {
                            type = 0
                            sheetState.hide()
                        }
                    }
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
    onShowSnackbar: suspend (Int, String?) -> Boolean,
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
                                    onShowSnackbar(R.string.err_min_resource, null)
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


