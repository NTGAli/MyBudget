package com.ntg.login

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ntg.core.designsystem.components.AppBar
import com.ntg.core.designsystem.components.OtpField
import com.ntg.feature.login.R

@Composable
fun CodeRoute(
    phone: String,
    navigateToSetup:() -> Unit,
    onBack:() -> Unit,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    CodeScreen(
        phone,
        onBack = onBack,
        setDefaultAccount = {
            loginViewModel.setDefaultAccount()
        },
        navigateToSetup = navigateToSetup
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CodeScreen(
    phone: String,
    onBack: () -> Unit,
    setDefaultAccount: () -> Unit,
    navigateToSetup: () -> Unit,
) {

    var wasWrong by remember {
        mutableStateOf(false)
    }

    var isSucceeded by remember {
        mutableStateOf(false)
    }

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
                style = MaterialTheme.typography.bodySmall.copy(MaterialTheme.colorScheme.outline).copy(textAlign = TextAlign.Center)
            )

            OtpField(
                modifier = Modifier.padding(top = 32.dp),
                wasWrong = wasWrong, isSucceeded = isSucceeded
            ) { userInputCode, bool ->
                code = userInputCode
                wasWrong = false

                Log.d("AWJHKWJAHDJWA", "$isSucceeded --- $bool")


            }

            if (code.length == 6){
                wasWrong = code != "123456"
                isSucceeded = !wasWrong
                if (isSucceeded){
                    LaunchedEffect(key1 = Unit) {
                        setDefaultAccount()
                        Handler(Looper.getMainLooper()).postDelayed({
                            navigateToSetup()
                        }, 1400)
                    }
                }
            }

        }
    }

}