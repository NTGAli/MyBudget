package com.ntg.features.setup

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ntg.core.designsystem.components.AppBar
import com.ntg.core.designsystem.components.BankCard
import com.ntg.core.designsystem.components.BudgetButton
import com.ntg.core.designsystem.components.BudgetTextField
import com.ntg.core.designsystem.components.ButtonSize
import com.ntg.core.designsystem.components.ButtonStyle
import com.ntg.core.designsystem.components.ButtonType
import com.ntg.core.designsystem.components.CurrencyTextField
import com.ntg.core.designsystem.components.ExposedDropdownMenuSample
import com.ntg.core.designsystem.components.TextDivider
import com.ntg.core.designsystem.components.WheelList
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.core.model.Wallet
import com.ntg.core.model.SourceType
import com.ntg.core.model.res.Bank
import com.ntg.core.model.res.ServerConfig
import com.ntg.core.model.res.WalletType
import com.ntg.core.mybudget.common.LoginEventListener
import com.ntg.core.mybudget.common.SharedViewModel
import com.ntg.core.mybudget.common.generateUniqueFiveDigitId
import com.ntg.mybudget.core.designsystem.R
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import com.ntg.core.designsystem.components.SampleItem
import com.ntg.core.designsystem.components.getLanguageFlag
import com.ntg.core.model.res.Currency
import com.ntg.core.mybudget.common.orZero

@Composable
fun WalletRoute(
    sharedViewModel: SharedViewModel,
    setupViewModel: SetupViewModel = hiltViewModel(),
    accountId: Int = 1,
    sourceId: Int? = null,
    onShowSnackbar: suspend (Int, String?, Int?) -> Boolean,
    onBack: () -> Unit,
    navigateToCurrencies: () -> Unit
) {

    sharedViewModel.setExpand.postValue(true)
    val context = LocalContext.current
    sharedViewModel.bottomNavTitle.postValue(stringResource(id = R.string.submit))
    var wallet by remember {
        mutableStateOf<Wallet?>(null)
    }
    var bankCard by remember {
        mutableStateOf<SourceType.BankCard?>(null)
    }
    var sourceType by rememberSaveable {
        mutableIntStateOf(-1)
    }
    var cardBalance by rememberSaveable {
        mutableStateOf("")
    }

    var localBanks by remember {
        mutableStateOf<List<Bank>?>(null)
    }

    var walletTypes by remember {
        mutableStateOf<List<WalletType>?>(null)
    }

    val editSource = setupViewModel.getWalletById(sourceId ?: -1).collectAsStateWithLifecycle(
        initialValue = null
    ).value

    val logoUrlColor =
        setupViewModel.getBankLogoColor().collectAsStateWithLifecycle(initialValue = null).value
    val logoUrlMono =
        setupViewModel.getBankLogoMono().collectAsStateWithLifecycle(initialValue = null).value
    val selectedCurrency =
        setupViewModel.selectedCurrency.collectAsStateWithLifecycle(initialValue = null)


    WalletScreen(
        editSource, onBack = onBack,
        deleteSource = {
            if (sourceId != null) {
                setupViewModel.tempRemoveWallet(sourceId, context)
                onBack()
            }
        },
        selectedSource = {
            sourceType = it
        },
        walletTypes = walletTypes,
        logoUrlMono = logoUrlMono,
        logoUrlColor = logoUrlColor,
        localBanks = localBanks.orEmpty(),
        navigateToCurrencies = navigateToCurrencies,
        selectedCurrency = selectedCurrency.value
    ) { sourceValue, card, balance ->
        wallet = sourceValue
        bankCard = card
        cardBalance = balance
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = bankCard) {
        sharedViewModel.loginEventListener = object : LoginEventListener {
            override fun onBottomButtonClick() {

                when (sourceType) {
                    //Bank card
                    1 -> {
                        if (bankCard?.number.orEmpty().isEmpty()) {
                            scope.launch {
                                onShowSnackbar(R.string.err_empty_card_number, null, null)
                            }
                            return
                        } else if (bankCard?.number.orEmpty().length != 16) {
                            scope.launch {
                                onShowSnackbar(R.string.err_length_number, null, null)
                            }
                            return
                        } else if (bankCard?.name.orEmpty().isEmpty()) {
                            scope.launch {
                                onShowSnackbar(R.string.err_empty_name, null, null)
                            }
                            return
                        } else if (cardBalance.isEmpty() && editSource == null) {
                            scope.launch {
                                onShowSnackbar(R.string.err_balance_empty, null, null)
                            }
                            return
                        }

                        if (editSource != null) {
                            editSource.data = bankCard
                            setupViewModel.updateBankCard(editSource, context = context)
                            onBack()
                        } else if (wallet != null && bankCard != null) {
                            wallet?.accountId = accountId
                            wallet?.data = bankCard
                            setupViewModel.insertNewSource(wallet!!)

                            // insert balance value
                            setupViewModel.initCardTransactions(
                                cardBalance.replace(",", "").toLong(), wallet?.id!!, accountId
                            )

                            onBack()
                        } else {
                            scope.launch {
                                onShowSnackbar(R.string.err_in_submit, null, null)
                            }
                        }
                    }

                    //cash
                    2 -> {

                    }

                    // crypto
                    3 -> {

                    }

                }
            }
        }
    }

    LaunchedEffect(key1 = sourceType) {
        when (sourceType) {
            1 -> {
                setupViewModel.getLocalUserBanks().collect {
                    localBanks = it
                }
            }

        }
    }

    LaunchedEffect(key1 = Unit) {
        setupViewModel.walletTypes().collect{
            walletTypes = it
        }
    }

    LaunchedEffect(editSource) {
        if (editSource != null) {
            sourceType = editSource.type.orZero()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WalletScreen(
    editSource: Wallet?,
    walletTypes: List<WalletType>?,
    localBanks: List<Bank>,
    logoUrlMono: ServerConfig?,
    logoUrlColor: ServerConfig?,
    onBack: () -> Unit,
    deleteSource: () -> Unit,
    selectedSource: (Int) -> Unit,
    navigateToCurrencies: () -> Unit,
    selectedCurrency: Currency?,
    submitCard: (source: Wallet, card: SourceType.BankCard?, balance: String) -> Unit
) {


    var sourceType by rememberSaveable {
        mutableStateOf<Int?>(null)
    }

    var bankCard by remember {
        mutableStateOf<SourceType.BankCard?>(null)
    }

    var bankCardId by rememberSaveable {
        mutableStateOf<Int?>(null)
    }

    var editMode by rememberSaveable {
        mutableStateOf(false)
    }

    var balance by rememberSaveable {
        mutableStateOf("")
    }

    submitCard.invoke(
        Wallet(
            id = generateUniqueFiveDigitId(),
            accountId = 0,
            currencyId = selectedCurrency?.id,
            isSelected = false,
            type = sourceType,
            dateCreated = System.currentTimeMillis().toString()
        ),
        bankCard,
        balance
    )


    if (editSource != null) {
        editMode = true
        when (editSource.data) {
            is SourceType.BankCard -> sourceType = 1
            is SourceType.Gold -> sourceType = 2
            null -> editMode = false
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppBar(
                title = stringResource(id = R.string.source_expenditure),
                scrollBehavior = scrollBehavior,
                navigationOnClick = {
                    onBack.invoke()
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            if (!editMode) {
                ExposedDropdownMenuSample(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(top = 8.dp),
                    walletType = walletTypes.orEmpty()
                ) {
                    sourceType = it.id
                    selectedSource.invoke(it.id ?: -1)
                }
            }


            when (sourceType) {

                1 -> {

                    BankCardView(
                        if (editMode) editSource?.data as SourceType.BankCard else null,
                        localBanks = localBanks,
                        logoUrlMono = logoUrlMono,
                        logoUrlColor = logoUrlColor,
                        navigateToCurrencies = navigateToCurrencies,
                        selectedCurrency = selectedCurrency,
                        deleteCard = {
                            deleteSource()
                        }
                    ) { card, finalBalance ->
                        bankCard = card
                        balance = finalBalance
                    }
                }

                2 -> {

                }

                3 -> {

                }

            }


        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BankCardView(
    editBankCard: SourceType.BankCard? = null,
    localBanks: List<Bank>,
    logoUrlMono: ServerConfig?,
    logoUrlColor: ServerConfig?,
    selectedCurrency: Currency?,
    deleteCard: () -> Unit,
    navigateToCurrencies: () -> Unit,
    bankCard: (SourceType.BankCard, String) -> Unit
) {

    val layoutDirection = LocalLayoutDirection.current
    val context = LocalContext.current

    val concurrency = remember {
        mutableStateOf("تومن")
    }

    val name = remember {
        mutableStateOf("")
    }

    val accountNumber = remember {
        mutableStateOf("")
    }

    val sheba = remember {
        mutableStateOf("")
    }

    val cvv = remember {
        mutableStateOf("")
    }

    val expire = remember {
        mutableStateOf("")
    }

    var month by remember {
        mutableIntStateOf(0)
    }

    var year by remember {
        mutableIntStateOf(0)
    }

    val cardNumber = remember {
        mutableStateOf("")
    }

    val balance = remember {
        mutableStateOf("")
    }

    LaunchedEffect(selectedCurrency) {
        val flag = getLanguageFlag(selectedCurrency?.countryAlpha2 ?: "") ?: ""
        concurrency.value =
            if (selectedCurrency != null) "$flag ${selectedCurrency.symbol} | ${selectedCurrency.faName}" else context.getString(
                R.string.select_currency
            )
    }

    val localBank =
        localBanks.find { it.bin.orEmpty().find { cardNumber.value.take(6).contains(it) } != null }

    bankCard(
        SourceType.BankCard(
            number = cardNumber.value,
            date = "$year/$month",
            name = name.value,
            updatedAt = System.currentTimeMillis().toString(),
            cvv = cvv.value,
            sheba = sheba.value,
            accountNumber = accountNumber.value,
            bankId = localBank?.id,
            nativeName = localBank?.nativeName,
            logoName = localBank?.logoName
        ),
        balance.value
    )

    LaunchedEffect(key1 = editBankCard) {
        if (editBankCard != null && cardNumber.value.isEmpty()) {
            name.value = editBankCard.name
            cardNumber.value = editBankCard.number
            sheba.value = editBankCard.sheba.orEmpty()
            accountNumber.value = editBankCard.accountNumber.orEmpty()
            cvv.value = editBankCard.cvv.orEmpty()
            if (editBankCard.date.orEmpty().isNotEmpty()) {
                year = editBankCard.date.orEmpty().split("/")[0].toInt()
                month = editBankCard.date.orEmpty().split("/")[1].toInt()
                expire.value = editBankCard.date.orEmpty()
            }
        }
    }

    val sheetState = rememberModalBottomSheetState()
    val deleteSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showDeleteSheet by remember { mutableStateOf(false) }
    var isFinalSubmit by remember { mutableStateOf(false) }
    var showMore by remember { mutableStateOf(false) }

    if (editBankCard == null) {
        BudgetTextField(
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            text = concurrency,
            label = stringResource(id = R.string.concurrency),
            readOnly = true,
            onClick = {
                navigateToCurrencies()
            }
        )
    }

    TextDivider(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp),
        title = stringResource(id = R.string.bank_card)
    )



    BankCard(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp),
        cardNumber = cardNumber.value,
        name = name.value,
        amount = "0",
        expiringDate = expire.value,
        bankName = localBank?.nativeName.orEmpty(),
        bankLogo = listOf(
            "${logoUrlColor?.value}${localBank?.logoName}.svg",
            "${logoUrlMono?.value}${localBank?.logoName}.svg"
        )
    )


    TextDivider(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp),
        title = stringResource(id = R.string.card_info)
    )

    BudgetTextField(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        text = cardNumber,
        label = stringResource(id = R.string.card_number),
        length = 16,
        keyboardType = KeyboardType.NumberPassword
    )

    BudgetTextField(
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        text = name,
        label = stringResource(id = R.string.first_last_name)
    )

    BudgetTextField(
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        text = expire,
        label = stringResource(id = R.string.expire),
        readOnly = true,
        onClick = {
            showBottomSheet = true
        }
    )


    if (editBankCard == null) {
        TextDivider(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp),
            title = stringResource(id = R.string.balance)
        )



        CurrencyTextField(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            onChange = {
                balance.value = it
            },
            currencySymbol = "",
            currencyName = selectedCurrency?.nativeName.orEmpty(),
            maxNoOfDecimal = 2,
            label = stringResource(id = R.string.balance),
            maxLines = 1,
            divider = ",",
            fixLeadingText = if (layoutDirection == LayoutDirection.Ltr) selectedCurrency?.symbol.orEmpty() else null,
            fixTrailingText = if (layoutDirection == LayoutDirection.Rtl) selectedCurrency?.symbol.orEmpty() else null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
    }


    BudgetButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        text = stringResource(id = if (showMore) R.string.show_less else R.string.show_more),
        style = ButtonStyle.TextOnly,
        size = ButtonSize.MD,
        type = ButtonType.Neutral,
        iconEnd = painterResource(id = if (showMore) BudgetIcons.directionUp else BudgetIcons.directionDown)
    ) {
        showMore = !showMore
    }


    AnimatedVisibility(
        modifier = Modifier.padding(bottom = 16.dp),
        visible = showMore
    ) {
        Column {
            BudgetTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                text = accountNumber,
                label = stringResource(id = R.string.account_number),
                keyboardType = KeyboardType.Number
            )

            BudgetTextField(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                text = sheba,
                label = stringResource(id = R.string.sheba_number),
                keyboardType = KeyboardType.Number
            )

            BudgetTextField(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                text = cvv,
                label = stringResource(id = R.string.cvv2),
                keyboardType = KeyboardType.Number
            )
        }
    }




    if (editBankCard != null) {
        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHighest)
        SampleItem(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            title = stringResource(id = R.string.delete_card),
            iconPainter = painterResource(id = BudgetIcons.trash),
            type = ButtonType.Error
        ) {
            showDeleteSheet = true
        }
    }

    Spacer(modifier = Modifier.padding(24.dp))

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState
        ) {

            Column {

                Text(
                    modifier = Modifier.padding(start = 24.dp),
                    text = stringResource(id = R.string.select_expire_date),
                    style = MaterialTheme.typography.titleSmall
                )

                Row(
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .padding(bottom = 32.dp, top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WheelList(
                        modifier = Modifier.weight(1f),
                        items = (1..12).toList(),
                        initialItem = month,
                        onItemSelected = { i, item ->
                            month = item
                            expire.value = "$year/$month"
                        }
                    )

                    Text(
                        style = MaterialTheme.typography.titleLarge,
                        text = "/"
                    )

                    WheelList(
                        modifier = Modifier.weight(1f),
                        items = (1403..1408).toList(),
                        initialItem = year,
                        onItemSelected = { i, item ->
                            year = item
                            expire.value = "$year/$month"
                        }
                    )
                }

                BudgetButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    text = stringResource(id = R.string.submit),
                    size = ButtonSize.MD
                ) {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                        }
                    }
                }

            }

        }
    }


    if (showDeleteSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showDeleteSheet = false
            },
            sheetState = deleteSheetState
        ) {

            Text(
                modifier = Modifier.padding(start = 24.dp),
                text = stringResource(id = R.string.delete_card),
                style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.error)
            )

            Text(
                modifier = Modifier.padding(start = 24.dp, top = 4.dp),
                text = stringResource(id = R.string.sure_delete_bank_card),
                style = MaterialTheme.typography.titleSmall
            )

            Row(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(vertical = 16.dp)
            ) {
                BudgetButton(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp),
                    text = stringResource(id = R.string.delete),
                    type = ButtonType.Error,
                    size = ButtonSize.SM
                ) {
                    showDeleteSheet = false
                    deleteCard()
                }

                BudgetButton(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp),
                    text = stringResource(id = R.string.cancel),
                    style = ButtonStyle.Outline,
                    size = ButtonSize.SM
                ) {
                    showDeleteSheet = false
                }
            }


        }
    }
}