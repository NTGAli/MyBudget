package com.ntg.features.home

import android.app.Activity
import android.content.Intent
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
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
import com.ntg.core.designsystem.components.BudgetButton
import com.ntg.core.designsystem.components.BudgetTextField
import com.ntg.core.designsystem.components.ButtonSize
import com.ntg.core.designsystem.components.ButtonStyle
import com.ntg.core.designsystem.components.CustomKeyboard
import com.ntg.core.designsystem.components.FullScreenBottomSheet
import com.ntg.core.designsystem.components.ImagePicker
import com.ntg.core.designsystem.components.Popup
import com.ntg.core.designsystem.components.SampleItem
import com.ntg.core.designsystem.components.SearchTextField
import com.ntg.core.designsystem.components.SwitchText
import com.ntg.core.designsystem.components.Tag
import com.ntg.core.designsystem.components.TextDivider
import com.ntg.core.designsystem.model.PopupItem
import com.ntg.core.designsystem.model.PopupType
import com.ntg.core.designsystem.model.SwitchItem
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.core.model.Contact
import com.ntg.core.model.Person
import com.ntg.core.model.SourceType
import com.ntg.core.model.Transaction
import com.ntg.core.model.Wallet
import com.ntg.core.model.res.Bank
import com.ntg.core.model.res.Category
import com.ntg.core.mybudget.common.Constants
import com.ntg.core.mybudget.common.LoginEventListener
import com.ntg.core.mybudget.common.SharedViewModel
import com.ntg.core.mybudget.common.calculateExpression
import com.ntg.core.mybudget.common.formatInput
import com.ntg.core.mybudget.common.isOperator
import com.ntg.core.mybudget.common.logd
import com.ntg.core.mybudget.common.orDefault
import com.ntg.core.mybudget.common.orFalse
import com.ntg.core.mybudget.common.orZero
import com.ntg.mybudget.core.designsystem.R
import kotlinx.coroutines.launch
import kotlin.math.log

@Composable
fun InsertRoute(
    transactionId: Int?,
    homeViewModel: HomeViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onShowSnackbar: suspend (Int, String?, Int?) -> Boolean,
) {
    sharedViewModel.setExpand.postValue(true)
    sharedViewModel.bottomNavTitle.postValue("submit")

    val scope = rememberCoroutineScope()
    val isPresent = remember { mutableStateOf(true) }
    val currentResource =
        homeViewModel.selectedSources().collectAsStateWithLifecycle(initialValue = emptyList())
    val logoUrlColor =
        homeViewModel.getBankLogoColor()
            .collectAsStateWithLifecycle(initialValue = null).value?.value
    val categories = homeViewModel.getCategories().collectAsStateWithLifecycle(initialValue = null)
    val localBanks =
        homeViewModel.getLocalUserBanks().collectAsStateWithLifecycle(initialValue = emptyList()).value?.toMutableStateList()

    val contacts = homeViewModel.getContacts().collectAsStateWithLifecycle().value

    val transaction = remember { mutableStateOf<Transaction?>(null) }
    val updatedTransaction = remember { mutableStateOf<Transaction?>(null) }

    if (transactionId != null) {
        transaction.value =
            homeViewModel.transactionById(transactionId)
                .collectAsStateWithLifecycle(initialValue = null).value

    }


    InsertScreen(
        expandTransaction = isPresent,
        currentResource = currentResource.value,
        logoUrl = logoUrlColor,
        contacts = contacts,
        localBanks = localBanks,
        categories = categories.value,
        onShowSnackbar = onShowSnackbar,
        transaction = transaction.value,
        closeable = true,
        newContact = {
            homeViewModel.insertContact(it)
        }
    ) {
        updatedTransaction.value = it
    }

    LaunchedEffect(isPresent.value) {
        if (!isPresent.value) {
            onBack()
        }
    }

    LaunchedEffect(key1 = updatedTransaction) {
        sharedViewModel.loginEventListener = object : LoginEventListener {
            override fun onBottomButtonClick() {
                if (updatedTransaction.value == null) {
                    scope.launch {
                        onShowSnackbar(R.string.err_non_transacton, null, null)
                    }
                } else if (updatedTransaction.value?.type == Constants.BudgetType.TRANSFER) {
                    scope.launch {
                    }
                } else if (updatedTransaction.value?.amount.orDefault() <= 0L) {
                    scope.launch {
                        onShowSnackbar(R.string.err_amount, null, null)
                    }
                } else if (updatedTransaction.value?.sourceId == 0 || updatedTransaction.value?.sourceId == null) {
                    scope.launch {
                        onShowSnackbar(R.string.err_input_source, null, null)
                    }
                } else if (updatedTransaction.value?.categoryId == null || updatedTransaction.value?.categoryId == 0) {
                    scope.launch {
                        onShowSnackbar(R.string.err_input_category, null, null)
                    }
                } else {
                    homeViewModel.updateTransaction(updatedTransaction.value!!)
                    onBack()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsertScreen(
    expandTransaction: MutableState<Boolean>,
    currentResource: List<Wallet>?,
    contacts: List<Contact>?,
    logoUrl: String?,
    localBanks: SnapshotStateList<Bank>?,
    categories: List<Category>?,
    transaction: Transaction? = null,
    closeable: Boolean = false,
    onShowSnackbar: suspend (Int, String?, Int?) -> Boolean,
    newContact: (Contact) -> Unit,
    onTransactionChange: (Transaction) -> Unit,
) {

    val context = LocalContext.current
    val amount = remember { mutableStateOf("") }
    val note = remember { mutableStateOf("") }
    var input by remember { mutableStateOf("") }
    var lastInput by remember { mutableStateOf("") }
    val tag = remember { mutableStateOf("") }

    var selectedSource by remember {
        mutableStateOf<Wallet?>(null)
    }

    var secondSource by remember {
        mutableStateOf<Wallet?>(null)
    }

    var selectedCategory by remember {
        mutableStateOf<Category?>(null)
    }

    val tags = remember { mutableStateListOf<String>() }
    val people = remember { mutableStateListOf<Person>() }
    val images = remember { mutableStateListOf<String>() }


    var sheetType by remember { mutableIntStateOf(0) }
    var selectSourceType by remember { mutableIntStateOf(0) }
    var selectedTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    val concurrency = remember {
        mutableStateOf("تومن")
    }

    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    var openedKeyboard by remember {
        mutableStateOf(false)
    }
    val layoutDirection = LocalLayoutDirection.current

    var budgetType by remember {
        mutableIntStateOf(Constants.BudgetType.EXPENSE)
    }

    val searchContactQuery = remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val contactUri = result.data?.data

            contactUri?.let { uri ->
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
                        newContact(
                            Contact(
                                fullName = name,
                                phoneNumber = phoneNumber,
                            )
                        )
                        people.add(Person(0, phoneNumber, 0))
                    }
                }
            }
        }
    }


    LaunchedEffect(transaction, categories) {
        if (transaction != null) {
            lastInput = formatInput(transaction.amount.toString())
            amount.value = formatInput(transaction.amount.toString())
            selectedTime = transaction.date
            images.clear()
            images.addAll(transaction.images.orEmpty())
            tags.clear()
            tags.addAll(transaction.tags.orEmpty())
            people.clear()
//            contacts.addAll(transaction.contacts.orEmpty())
            note.value = transaction.note.orEmpty()
            selectedSource = currentResource.orEmpty().find { it.id == transaction.sourceId }
            selectedCategory = categories.orEmpty().find { it.id == transaction.categoryId }
            budgetType = transaction.type.orZero()
        }
    }

    if (amount.value.isNotEmpty()) {
        onTransactionChange(
            Transaction(
                id = transaction?.id ?: 0,
                accountId = selectedSource?.accountId.orZero(),
                sourceId = selectedSource?.id.orZero(),
                categoryId = selectedCategory?.id.orZero(),
                amount = amount.value.replace(",", "").toLong(),
                type = budgetType,
                date = selectedTime,
                note = note.value,
                images = images,
                tags = tags,
                contactIds = people.map { it.contactId }
            )
        )
    }

    LaunchedEffect(expandTransaction.value) {
        if (!expandTransaction.value) {
            amount.value = ""
            selectedCategory = null
            selectedSource = null
            note.value = ""
            budgetType = Constants.BudgetType.EXPENSE
            tags.clear()
            people.clear()
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
                        modifier = Modifier.then(
                            if (closeable) Modifier.rotate(-90f) else Modifier
                        ),
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
                    stringResource(id = R.string.outcome),
                    tint = MaterialTheme.colorScheme.onError,
                    backColor = MaterialTheme.colorScheme.error
                ),
                SwitchItem(
                    0,
                    stringResource(id = R.string.income),
                    tint = MaterialTheme.colorScheme.onSecondary,
                    backColor = MaterialTheme.colorScheme.secondary
                ),
                SwitchItem(
                    0,
                    stringResource(id = R.string.internal_transfer),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    backColor = MaterialTheme.colorScheme.primary
                ),
            )

            SwitchText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp), items = items,
                budgetType
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
                label = stringResource(id = R.string.price),
                fixLeadingText = if (layoutDirection == LayoutDirection.Ltr) concurrency.value else null,
                fixTrailingText = if (layoutDirection == LayoutDirection.Rtl) concurrency.value else null,
                readOnly = true,
                onClick = {
                    sheetType = 0
                    openedKeyboard = true
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
                    text = remember(selectedSource, localBanks) {
                        if (selectedSource != null) {
                            val localBankName = localBanks?.find { it.id == (selectedSource?.data as SourceType.BankCard).bankId }?.nativeName
                            mutableStateOf(
                                "${localBankName} - ${
                                    (selectedSource?.data as SourceType.BankCard).number.takeLast(
                                        4
                                    )
                                }"
                            )
                        } else {
                            mutableStateOf("")
                        }
                    },
                    label = stringResource(id = R.string.source_expenditure),
                    trailingIcon = painterResource(id = BudgetIcons.directionLeft),
                    readOnly = true,
                    onClick = {
                        sheetType = 1
                        selectSourceType = 1
                        openedKeyboard = true
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
                            if (selectedSource != null) {
                                mutableStateOf(
                                    "${localBanks?.find { it.id == (selectedSource?.data as SourceType.BankCard).bankId }?.nativeName.orEmpty()} - ${
                                        (selectedSource?.data as SourceType.BankCard).number.takeLast(
                                            4
                                        )
                                    }"
                                )
                            } else {
                                mutableStateOf("")
                            }
                        },
                        label = stringResource(id = R.string.from),
                        trailingIcon = painterResource(id = BudgetIcons.directionLeft),
                        readOnly = true,
                        onClick = {
                            sheetType = 1
                            selectSourceType = 1
                            openedKeyboard = true
                        }
                    )

                    BudgetTextField(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        text = remember(secondSource) {
                            if (secondSource != null) {
                                mutableStateOf(
                                    "${localBanks?.find { it.id == (selectedSource?.data as SourceType.BankCard).bankId }?.nativeName.orEmpty()} - ${
                                        (secondSource?.data as SourceType.BankCard).number.takeLast(
                                            4
                                        )
                                    }"
                                )
                            } else {
                                mutableStateOf("")
                            }
                        },
                        label = stringResource(id = R.string.to),
                        trailingIcon = painterResource(id = BudgetIcons.directionLeft),
                        readOnly = true,
                        onClick = {
                            sheetType = 1
                            selectSourceType = 2
                            openedKeyboard = true
                        }
                    )

                    BudgetTextField(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        text = amount,
                        label = stringResource(id = R.string.fee),
                        fixLeadingText = if (layoutDirection == LayoutDirection.Ltr) concurrency.value else null,
                        fixTrailingText = if (layoutDirection == LayoutDirection.Rtl) concurrency.value else null,
                        readOnly = true,
                        onClick = {
                            sheetType = 0
                            openedKeyboard = true
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
                    label = stringResource(id = R.string.catgory),
                    trailingIcon = painterResource(id = BudgetIcons.directionLeft),
                    readOnly = true,
                    onClick = {
                        sheetType = 2
                        openedKeyboard = true
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
                    sheetType = 3
                    openedKeyboard = true
                }

                people.forEach {person ->
                    Tag(
                        modifier = Modifier.padding(end = 8.dp),
                        text = try {
                            contacts.orEmpty().first { it.phoneNumber == person.contactId }.fullName
                        }catch (e: Exception) {
                            ""
                        },
                        dismissClick = {
                            people.remove(person)
                        }
                    ) {
                        people.remove(person)
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
                padding = PaddingValues(horizontal = 24.dp),
                images = transaction?.images.orEmpty()
            ) {
                if (it.isNotEmpty()) {
                    images.clear()
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
        openedKeyboard = sheetState.isVisible
    }

    if (openedKeyboard) {

        ModalBottomSheet(
            modifier = Modifier.then(if (sheetType == 3) Modifier.fillMaxHeight() else Modifier),
            sheetState = sheetState,
            onDismissRequest = {
                searchContactQuery.value = ""
                openedKeyboard = false
            }) {

            when (sheetType) {
                //calculator
                0 -> {
                    val isExpressionComplete = remember(input) {
                        input.isNotEmpty() && (if (input.last()
                                .isDigit()
                        ) input else input.dropLast(1)) != lastInput.replace(",", "")
                                && input.contains("+")
                                && calculateExpression(input) != null
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
                            val userInput = lastInput.ifEmpty { input }
                            if (userInput.toLong() > 0L) {
                                amount.value = formatInput(userInput)
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
                        items(currentResource.orEmpty()) {wallet ->
                            var logoName = ""
                            val data = if (wallet.data is SourceType.BankCard) {
                                logoName = localBanks?.find { it.id == (wallet.data as SourceType.BankCard).bankId }?.logoName.orEmpty()
                                "${localBanks?.find { it.id == (wallet.data as SourceType.BankCard).bankId }?.nativeName.orEmpty()} - ${
                                    (wallet.data as SourceType.BankCard).number.takeLast(
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
                                isRadioCheck = if (selectSourceType == 1) selectedSource == wallet else secondSource == wallet
                            ) {
                                if (selectSourceType == 1) {
                                    selectedSource = wallet
                                } else {
                                    secondSource = wallet
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
                        items(categories.orEmpty().filter { it.type == budgetType }) {cat ->
                            SampleItem(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                title = cat.name, setRadio = true,
                                isRadioCheck = selectedCategory == cat
                            ) {
                                selectedCategory = cat
                                scope.launch {
                                    sheetState.hide()
                                }
                            }
                        }

                    }
                }

                //contacts
                3 -> {

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SearchTextField(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 16.dp).padding(vertical = 8.dp),
                                query = searchContactQuery
                            )

                            IconButton(onClick = {
                                val intent = Intent(Intent.ACTION_PICK).apply {
                                    type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
                                }
                                launcher.launch(intent)
                            }) {
                                Icon(painter = painterResource(BudgetIcons.userCircleAdd), contentDescription = "")
                            }


                        }
                    }

                    LazyColumn {

                        if (contacts.orEmpty().isEmpty()){
                            item {
                                Text(
                                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                                    text = stringResource(R.string.no_contact),
                                    style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center))
                                BudgetButton(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(top = 8.dp),
                                    text = stringResource(R.string.import_new), style = ButtonStyle.TextOnly, size = ButtonSize.MD){
                                    val intent = Intent(Intent.ACTION_PICK).apply {
                                        type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
                                    }
                                    launcher.launch(intent)
                                }
                            }
                        }

                        items(contacts.orEmpty().filter { it.phoneNumber?.contains(searchContactQuery.value).orFalse() || it.fullName?.contains(searchContactQuery.value).orFalse() }){ct ->
                            SampleItem(
                                modifier = Modifier.padding(horizontal = 24.dp),
                                title = ct.fullName.orEmpty(), setCheckbox = true, hasDivider = true, isRadioCheck = people.any { it.contactId == ct.phoneNumber },
                                secondIconPainter = painterResource(BudgetIcons.more)
                            ) {
                                if (it == 1){
                                    if (people.any { it.contactId == ct.phoneNumber }){
                                        people.remove(Person(0, ct.phoneNumber.orEmpty(), 0))
                                    }else{
                                        people.add(Person(0, ct.phoneNumber.orEmpty(), 0))
                                    }
                                }else{

                                }
                            }
                        }

                    }

                }

            }

        }
    }

}