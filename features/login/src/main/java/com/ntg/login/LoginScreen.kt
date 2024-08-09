package com.ntg.login

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ntg.core.designsystem.components.BudgetTextField
import com.ntg.core.designsystem.components.getLanguageFlag
import com.ntg.core.mybudget.common.LoginEventListener
import com.ntg.core.mybudget.common.SharedViewModel
import com.ntg.core.mybudget.common.getCountryFromPhoneNumber
import com.ntg.core.mybudget.common.getCountryFullNameFromPhoneNumber
import com.ntg.core.mybudget.common.isValidIranianPhoneNumber
import com.ntg.feature.login.R

@Composable
fun LoginRoute(
    viewModel: LoginViewModel,
    sharedViewModel: SharedViewModel,
    navigateToDetail: () -> Unit = {},
    navigateToCode: () -> Unit = {},
){
    LoginScreen(
        viewModel,
        sharedViewModel,
        navigateToDetail = navigateToDetail,
        navigateToCode = navigateToCode
    )

}

@Composable
private fun LoginScreen(
    loginViewModel: LoginViewModel,
    sharedViewModel: SharedViewModel,
    navigateToDetail: () -> Unit = {},
    navigateToCode: () -> Unit = {},
){

    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current

    val code = rememberSaveable {
        mutableStateOf("98")
    }

    val phone = rememberSaveable {
        mutableStateOf("")
    }

    var wasWrong by remember {
        mutableStateOf(false)
    }

    var onCLick by remember {
        mutableStateOf(false)
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

    DisposableEffect(Unit) {
        val listener = object : LoginEventListener {
            override fun onLoginEvent() {
                onCLick = true
            }
        }
        sharedViewModel.loginEventListener = listener
        onDispose {
            sharedViewModel.loginEventListener = null
        }
    }

    LaunchedEffect(key1 = Unit) {
        loginViewModel.countrySelected.observe(owner){
            Log.d("countrySelected", it)
            code.value = it
        }
    }
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
                code, phone, wasWrong
            ) {
                Log.d("phoneWasWrongChecker", "$it")
                if (it){
//                    wasWrong = false
                }
            }


        }
    }

    if (onCLick) {
        if (code.value.isEmpty() || phone.value.isEmpty() || getCountryFullNameFromPhoneNumber(
                context,
                code.value
            ).orEmpty().isEmpty() ||
            !isValidIranianPhoneNumber("+${code.value}${phone.value}")
        ) {
            wasWrong = true
            Handler(Looper.getMainLooper()).postDelayed({
                wasWrong = false
            }, 500)
        } else {
            navigateToCode()
        }
        onCLick = false
    }


}

