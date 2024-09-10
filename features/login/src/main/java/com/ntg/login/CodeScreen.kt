package com.ntg.login

import android.os.Handler
import android.os.Looper
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.ntg.core.designsystem.components.AppBar
import com.ntg.core.designsystem.components.OtpField
import com.ntg.core.model.req.VerifyOtp
import com.ntg.core.network.model.Result
import com.ntg.feature.login.R
import kotlinx.coroutines.launch

@Composable
fun CodeRoute(
    phone: String,
    onBack: () -> Unit,
    loginViewModel: LoginViewModel = hiltViewModel(),
    onShowSnackbar: suspend (Int, String?) -> Boolean,
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val currentLifecycleState = lifecycleOwner.lifecycle
    val scope = rememberCoroutineScope()

    val wasWrong = remember {
        mutableStateOf(false)
    }

    val isSucceeded = remember {
        mutableStateOf(false)
    }

    val isLoading = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        loginViewModel.codeVerificationState
            .flowWithLifecycle(currentLifecycleState, Lifecycle.State.STARTED)
            .collect {
                when (it) {
                    is Result.Error -> {
                        wasWrong.value = true
                        scope.launch {
                            onShowSnackbar.invoke(R.string.err_invalid_code, null)
                        }
                        isLoading.value = false
                    }

                    is Result.Loading -> {
                        if (it.loading){
                            isLoading.value = true
                        }
                    }

                    is Result.Success -> {
                        if (it.data?.accessToken.orEmpty()
                                .isNotEmpty() && it.data?.expiresAt.orEmpty().isNotEmpty()
                        ) {
                            isSucceeded.value = true
                            loginViewModel.setDefaultAccount()
                            Handler(Looper.getMainLooper()).postDelayed({
                                loginViewModel.saveUserLogin(
                                    it.data?.accessToken.orEmpty(),
                                    it.data?.expiresAt.orEmpty()
                                )
                            }, 1400)
                        }
                        isLoading.value = false
                    }
                }

            }
    }


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
        }
    )
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
                wasWrong = wasWrong.value, isSucceeded = isSucceeded.value, isLoading = isLoading.value
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
    }

}