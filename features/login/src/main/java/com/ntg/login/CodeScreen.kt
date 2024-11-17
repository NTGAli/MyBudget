package com.ntg.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.ntg.core.designsystem.components.AppBar
import com.ntg.core.designsystem.components.LoadingView
import com.ntg.core.designsystem.components.OtpField
import com.ntg.core.model.Account
import com.ntg.core.model.Wallet
import com.ntg.core.model.SourceType
import com.ntg.core.model.req.VerifyOtp
import com.ntg.core.mybudget.common.generateUniqueFiveDigitId
import com.ntg.core.mybudget.common.logd
import com.ntg.core.mybudget.common.orFalse
import com.ntg.core.network.model.Result
import com.ntg.mybudget.core.designsystem.R
import com.ntg.mybudget.sync.work.workers.initializers.Sync
import kotlinx.coroutines.launch

@Composable
fun CodeRoute(
    phone: String,
    onBack: () -> Unit,
    finishLogin: (String) -> Unit,
    loginViewModel: LoginViewModel = hiltViewModel(),
    onShowSnackbar: suspend (Int, String?, Int?) -> Boolean,
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val currentLifecycleState = lifecycleOwner.lifecycle
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val wasWrong = remember {
        mutableStateOf(false)
    }

    val isSucceeded = remember {
        mutableStateOf(false)
    }

    val isLoading = remember {
        mutableStateOf(false)
    }

    val uiState = loginViewModel.loginUiState.collectAsState()

    CodeScreen(
        phone,
        onBack = onBack,
        wasWrong = wasWrong,
        isSucceeded = isSucceeded,
        isLoading = isLoading,
        sendCode = {
            loginViewModel.verifyCode(
                VerifyOtp(
                    query = phone,
                    otp = it.toInt()
                )
            )
        },
        uiState
    )

    LaunchedEffect(Unit) {
        loginViewModel.codeVerificationState
            .flowWithLifecycle(currentLifecycleState, Lifecycle.State.STARTED)
            .collect {
                when (it) {
                    is Result.Error -> {
                        wasWrong.value = true
                        scope.launch {
                            onShowSnackbar.invoke(R.string.err_invalid_code, null, null)
                        }
                        isLoading.value = false
                    }

                    is Result.Loading -> {
                        if (it.loading) {
                            isLoading.value = true
                        }
                    }

                    is Result.Success -> {
                        if (it.data?.accessToken.orEmpty()
                                .isNotEmpty() && it.data?.expiresAt.orEmpty().isNotEmpty()
                        ) {
                            loginViewModel.saveUserLogin(
                                it.data?.accessToken.orEmpty(),
                                it.data?.expiresAt.orEmpty()
                            )
                            isSucceeded.value = true
                        }
                        isLoading.value = false
                    }
                }
            }
    }


    LaunchedEffect(isSucceeded.value) {
        if (isSucceeded.value) {
            loginViewModel.serverAccounts()
//            delay(800)
            loginViewModel.serverAccounts.collect {
                when (it) {
                    is Result.Error -> {
                        loginViewModel.loginUiState.value = LoginUiState.Error
                        scope.launch {
                            onShowSnackbar(R.string.err_fetch_data, null, null)
                        }
                        loginViewModel.logout()
                        onBack()
                    }

                    is Result.Loading -> {
//                        loginViewModel.loginUiState.value = LoginUiState.Loading
                    }

                    is Result.Success -> {
                        loginViewModel.loginUiState.value = LoginUiState.Success
                        it.data?.forEach { account ->
                            val localAccountId =
                                if (account.isDefault.orFalse()) 1 else generateUniqueFiveDigitId()
                            loginViewModel.insertNewAccount(
                                Account(
                                    id = localAccountId,
                                    sId = account.id,
                                    name = account.name.orEmpty(),
                                    isDefault = account.isDefault.orFalse(),
                                    isSelected = (account.isDefault.orFalse() && (it.data.orEmpty()
                                        .first().wallets.orEmpty()
                                        .isNotEmpty() || it.data.orEmpty().size > 1)),
                                    isSynced = true,
                                    dateCreated = account.createdAt.orEmpty()
                                )
                            )
                            account.wallets.orEmpty().forEach { wallet ->

                                var data: SourceType? = null

                                if (wallet.walletType == 1) {
                                    data = SourceType.BankCard(
                                        number = wallet.details.cardNumber.orEmpty(),
                                        cvv = wallet.details.cvv2.orEmpty(),
                                        sheba = wallet.details.sheba,
                                        name = wallet.details.cardOwner.orEmpty(),
                                        bankId = try {
                                            wallet.details.bankId!!.toInt()
                                        } catch (e: Exception) {
                                            -1
                                        }
                                    )
                                }

                                loginViewModel.insertNewSource(
                                    Wallet(
                                        id = 0,
                                        sId = wallet.id.orEmpty(),
                                        type = wallet.walletType,
                                        accountId = localAccountId,
                                        icon = null,
                                        currencyId = wallet.currencyId,
                                        isSelected = false,
                                        isSynced = true,
                                        dateCreated = wallet.createdAt.orEmpty(),
                                        data = data
                                    )
                                )


                            }

                        }
                        Sync.updateConfigs(context = context)
                        finishLogin(
                            if (it.data.orEmpty().size == 1 && it.data.orEmpty()
                                    .first().wallets.orEmpty().isEmpty()
                            ) "SetupRoute" else "home_route"
                        )
                    }
                }

            }
        } else loginViewModel.loginUiState.value = LoginUiState.Success
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CodeScreen(
    phone: String,
    onBack: () -> Unit,
    wasWrong: MutableState<Boolean>,
    isSucceeded: MutableState<Boolean>,
    isLoading: MutableState<Boolean>,
    sendCode: (String) -> Unit,
    loginUiState: State<LoginUiState>,
) {

    var code by remember {
        mutableStateOf("")
    }

    Scaffold(
        topBar = {
            AppBar(
                enableNavigation = true,
                navigationOnClick = onBack
            )
        }
    ) {

        if (loginUiState.value == LoginUiState.Success) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = stringResource(id = R.string.entere_code),
                    style = MaterialTheme.typography.titleSmall.copy(MaterialTheme.colorScheme.onBackground)
                )

                Text(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(top = 8.dp),
                    text = stringResource(id = R.string.sent_code_format, phone),
                    style = MaterialTheme.typography.bodySmall.copy(MaterialTheme.colorScheme.outline)
                        .copy(textAlign = TextAlign.Center)
                )

                OtpField(
                    modifier = Modifier.padding(top = 32.dp),
                    wasWrong = wasWrong.value,
                    isSucceeded = isSucceeded.value,
                    isLoading = isLoading.value
                ) { userInputCode, _ ->
                    code = userInputCode
                    wasWrong.value = false
                }


                LaunchedEffect(code.length) {
                    if (code.length == 5) {
                        sendCode(code)
                    }
                }
            }
        } else {
            LoadingView()
        }


    }

}