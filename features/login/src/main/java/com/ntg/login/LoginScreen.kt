package com.ntg.login

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.currentStateAsState
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.ntg.core.designsystem.components.BudgetTextField
import com.ntg.core.designsystem.components.getLanguageFlag
import com.ntg.core.mybudget.common.LoginEventListener
import com.ntg.core.mybudget.common.SharedViewModel
import com.ntg.core.mybudget.common.getCountryFromPhoneNumber
import com.ntg.core.mybudget.common.getCountryFullNameFromPhoneNumber
import com.ntg.core.mybudget.common.isValidIranianPhoneNumber
import com.ntg.core.network.model.Result
import com.ntg.feature.login.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginRoute(
    sharedViewModel: SharedViewModel,
    loginViewModel: LoginViewModel = hiltViewModel(),
    navigateToDetail: () -> Unit = {},
    navigateToCode: (String) -> Unit = {},
    onShowSnackbar: suspend (Int, String?, Int?) -> Boolean,
){

    sharedViewModel.setExpand.postValue(true)
    sharedViewModel.bottomNavTitle.postValue(stringResource(id = R.string.recieve_code))
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentLifecycleState = lifecycleOwner.lifecycle
    val scope = rememberCoroutineScope()


    var phone by remember { mutableStateOf("") }
    val code = remember { mutableStateOf("98") }
    val wasWrong = remember { mutableStateOf(false) }


    LaunchedEffect(key1 = Unit) {

        loginViewModel.countrySelected.observe(lifecycleOwner){
            code.value = it
        }

        sharedViewModel.loginEventListener = object : LoginEventListener {
            override fun onBottomButtonClick() {
                if (code.value.isEmpty() || phone.isEmpty() || getCountryFullNameFromPhoneNumber(
                        context,
                        code.value
                    ).orEmpty().isEmpty() ||
                    !isValidIranianPhoneNumber("+$phone")
                ) {
                    wasWrong.value = true
                    Handler(Looper.getMainLooper()).postDelayed({
                        wasWrong.value = false
                    }, 500)
                } else {
                    loginViewModel.sendCode(phone)
                }
            }
        }
        loginViewModel.sendCodeState
            .flowWithLifecycle(currentLifecycleState, Lifecycle.State.STARTED)
            .collect{
            when(it){
                is Result.Error -> {
                    sharedViewModel.setLoading.postValue(false)
                    scope.launch {
                        onShowSnackbar.invoke(R.string.err, null, null)
                    }
                }
                is Result.Loading -> {
                    if (it.loading){
                        sharedViewModel.setLoading.postValue(true)
                    }
                }
                is Result.Success -> {
                    sharedViewModel.setLoading.postValue(false)
                    navigateToCode(phone)
                }
            }
        }

    }


    LoginScreen(
        navigateToDetail = navigateToDetail,
        code = code,
        wasWrong = wasWrong
    ){
        phone = it
    }

}

@Composable
private fun LoginScreen(
    navigateToDetail: () -> Unit = {},
    code: MutableState<String>,
    wasWrong: MutableState<Boolean>,
    phone: (String) -> Unit,
){

    val context = LocalContext.current

    val phone = rememberSaveable {
        mutableStateOf("")
    }

    var countryValue = remember {
        mutableStateOf("")
    }

    var countryLetter by remember {
        mutableStateOf("")
    }

    var countryName by remember {
        mutableStateOf("")
    }

    var flag by remember {
        mutableStateOf("")
    }

    phone("${code.value}${phone.value}")


    countryLetter = getCountryFromPhoneNumber(context, code.value).orEmpty()
    countryName = getCountryFullNameFromPhoneNumber(context, code.value).orEmpty()
    flag = getLanguageFlag(countryLetter) ?: ""

    countryValue.value = if (flag.isNotEmpty() && countryName.isNotEmpty()) {
        "$countryName    $flag"
    } else if (code.value.length >= 3){
        "Invalid country code"
    }else { "" }


    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 32.dp)
                .padding(top = 64.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {


            Text(
                text = stringResource(R.string.login_with_phone),
                style = MaterialTheme.typography.titleSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Text(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(horizontal = 64.dp),
                text = stringResource(R.string.choose_country_text),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center
                ),
            )

            BudgetTextField(
                modifier = Modifier.padding(top = 32.dp),
                readOnly = true,
                text = countryValue,
                label = stringResource(id = R.string.country),
                onClick = {
                    navigateToDetail()
                }
            )


            BudgetTextField(
                modifier = Modifier.padding(top = 16.dp),
                code, phone, wasWrong.value
            ) {

            }
        }
    }
}

