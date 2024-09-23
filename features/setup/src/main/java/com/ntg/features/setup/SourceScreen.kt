package com.ntg.features.setup

import android.util.Log
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
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
import com.ntg.core.model.SourceExpenditure
import com.ntg.core.model.SourceType
import com.ntg.core.model.SourceTypes
import com.ntg.core.model.SourceWithDetail
import com.ntg.core.model.res.Bank
import com.ntg.core.model.res.ServerConfig
import com.ntg.core.model.res.WalletType
import com.ntg.core.mybudget.common.LoginEventListener
import com.ntg.core.mybudget.common.SharedViewModel
import com.ntg.core.mybudget.common.generateUniqueFiveDigitId
import com.ntg.mybudget.core.designsystem.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SourceRoute(
    sharedViewModel: SharedViewModel,
    accountId: Int,
    sourceId: Int? = null,
    setupViewModel: SetupViewModel = hiltViewModel(),
    onShowSnackbar: suspend (Int, String?) -> Boolean,
    onBack: () -> Unit,
    navigateToCurrencies: () -> Unit
) {
    sharedViewModel.setExpand.postValue(true)
    sharedViewModel.bottomNavTitle.postValue(stringResource(id = com.ntg.feature.setup.R.string.submit))
    var source by remember {
        mutableStateOf<SourceExpenditure?>(null)
    }
    var bankCard by remember {
        mutableStateOf<SourceType.BankCard?>(null)
    }
    var sourceType by remember {
        mutableIntStateOf(-1)
    }
    var cardBalance by remember {
        mutableStateOf("")
    }

    var localBanks by remember {
        mutableStateOf<List<Bank>?>(null)
    }

    val walletTypes = setupViewModel.walletTypes().collectAsStateWithLifecycle().value

    val editSource = setupViewModel.getSourcesById(sourceId ?: -1).collectAsStateWithLifecycle(
        initialValue = null
    ).value

    val logoUrlColor = setupViewModel.getBankLogoColor().collectAsStateWithLifecycle(initialValue = null).value
    val logoUrlMono = setupViewModel.getBankLogoMono().collectAsStateWithLifecycle(initialValue = null).value

    LaunchedEffect(key1 = Unit) {
        delay(3000)
        navigateToCurrencies()
    }

    SourceScreen(
        editSource, onBack = onBack,
        deleteSource = {
            if (sourceId != null) {
                setupViewModel.tempRemove(sourceId)
                onBack()
            }
        },
        selectedSource = {
            sourceType = it
        },
        walletTypes = walletTypes,
        logoUrlMono = logoUrlMono,
        logoUrlColor = logoUrlColor,
        localBanks = localBanks.orEmpty()
        ) { sourceValue, card, balance ->
        source = sourceValue
        bankCard = card
        cardBalance = balance
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = bankCard) {
        sharedViewModel.loginEventListener = object : LoginEventListener {
            override fun onLoginEvent() {

                when (sourceType) {
                    //Bank card
                    1 -> {
                        if (bankCard?.number.orEmpty().isEmpty()) {
                            scope.launch {
                                onShowSnackbar(R.string.err_empty_card_number, null)
                            }
                            return
                        } else if (bankCard?.number.orEmpty().length != 16) {
                            scope.launch {
                                onShowSnackbar(R.string.err_length_number, null)
                            }
                            return
                        } else if (bankCard?.name.orEmpty().isEmpty()) {
                            scope.launch {
                                onShowSnackbar(R.string.err_empty_name, null)
                            }
                            return
                        }else if (cardBalance.isEmpty()){
                            scope.launch {
                                onShowSnackbar(R.string.err_balance_empty, null)
                            }
                            return
                        }

                        if (editSource != null) {
                            bankCard?.sourceId = sourceId
                            setupViewModel.updateBankCard(bankCard!!)
                            onBack()
                        } else if (source != null && bankCard != null) {
                            source?.accountId = accountId
                            bankCard?.sourceId = source?.id
                            setupViewModel.insertNewSource(source!!)
                            setupViewModel.insertNewBankCard(bankCard!!)

                            // insert balance value
                            setupViewModel.initCardTransactions(cardBalance.replace(",", "").toLong(), source?.id!!, accountId)

                            onBack()
                        } else {
                            scope.launch {
                                onShowSnackbar(com.ntg.feature.setup.R.string.err_in_submit, null)
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
        when(sourceType){
            1 -> {
                setupViewModel.getLocalUserBanks().collect{
                    localBanks = it
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SourceScreen(
    editSource: SourceWithDetail?,
    walletTypes : List<WalletType>?,
    localBanks: List<Bank>,
    logoUrlMono: ServerConfig?,
    logoUrlColor: ServerConfig?,
    onBack: () -> Unit,
    deleteSource: () -> Unit,
    selectedSource: (Int) -> Unit,
    submitCard: (source: SourceExpenditure, card: SourceType.BankCard?, balance: String) -> Unit
) {


    var sourceType by remember {
        mutableStateOf<WalletType?>(null)
    }

    var bankCard by remember {
        mutableStateOf<SourceType.BankCard?>(null)
    }

    var editMode by remember {
        mutableStateOf(false)
    }

    var balance by remember {
        mutableStateOf("")
    }

    submitCard.invoke(
        SourceExpenditure(
            id = generateUniqueFiveDigitId(),
            accountId = 0,
            name = "تومن",
            symbol = "ت",
            isSelected = false,
            type = sourceType?.id,
            dateCreated = System.currentTimeMillis().toString()
        ),
        bankCard,
        balance
    )


    if (editSource != null) {
        editMode = true
        when (editSource.sourceType) {
            is SourceType.BankCard -> sourceType = walletTypes?.find { it.faName == stringResource(id = R.string.bank_card) }
            is SourceType.Gold -> sourceType = walletTypes?.find { it.faName == stringResource(id = R.string.gold) }
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
                    sourceType = it
                    selectedSource.invoke(it.id ?: -1)
                }
            }


            when (sourceType?.id) {

                1 -> {

                    BankCardView(
                        if (editMode) editSource?.sourceType as SourceType.BankCard else null,
                        localBanks = localBanks,
                        logoUrlMono = logoUrlMono,
                        logoUrlColor = logoUrlColor,
                        deleteCard = {
                            deleteSource()
                        }
                    ) {card, finalBalance ->
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
    deleteCard: () -> Unit,
    bankCard: (SourceType.BankCard, String) -> Unit
) {

    val layoutDirection = LocalLayoutDirection.current

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

    val localBank =localBanks.find { it.bin.orEmpty().find { cardNumber.value.take(6).contains(it) } != null }

    bankCard(
        SourceType.BankCard(
            id = editBankCard?.id ?: generateUniqueFiveDigitId(),
            number = cardNumber.value,
            date = "$year/$month",
            name = name.value,
            updatedAt = System.currentTimeMillis().toString(),
            cvv = cvv.value,
            sheba = sheba.value,
            accountNumber = accountNumber.value
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
            if (editBankCard.date.isNotEmpty()) {
                year = editBankCard.date.split("/")[0].toInt()
                month = editBankCard.date.split("/")[1].toInt()
                expire.value = editBankCard.date
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
            readOnly = true
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
            currencyName = concurrency.value,
            maxNoOfDecimal = 2,
            label = stringResource(id = R.string.balance),
            maxLines = 1,
            divider = ",",
            fixLeadingText = if (layoutDirection == LayoutDirection.Ltr) concurrency.value else null,
            fixTrailingText = if (layoutDirection == LayoutDirection.Rtl) concurrency.value else null,
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
        BudgetButton(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            iconStart = painterResource(id = BudgetIcons.trash),
            text = stringResource(id = R.string.delete_card),
            type = ButtonType.Error,
            style = ButtonStyle.TextOnly,
            size = ButtonSize.MD
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
                    text = stringResource(id = com.ntg.feature.setup.R.string.submit),
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

