package com.ntg.features.setup

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ntg.core.designsystem.components.AppBar
import com.ntg.core.designsystem.components.BudgetTextField
import com.ntg.core.model.Account
import com.ntg.core.mybudget.common.LoginEventListener
import com.ntg.core.mybudget.common.SharedViewModel
import com.ntg.feature.setup.R

@Composable
fun CreateAccountRoute(
    sharedViewModel: SharedViewModel,
    setupViewModel: SetupViewModel = hiltViewModel(),
    id: Int? = null,
    onBack:()-> Unit
) {
    sharedViewModel.setExpand.postValue(true)
    sharedViewModel.bottomNavTitle.postValue(stringResource(id = R.string.save))

    val account = setupViewModel.getAccount(id ?: 0).collectAsState(initial = null).value
    Log.d("AWJKDKLJWADKLJWAD", "$id ------ $account")
    var upsertAccount by remember {
        mutableStateOf<Account?>(null)
    }
    CreateAccountScreen(account?.name.orEmpty(), onBack = onBack){
        upsertAccount = it
    }

    LaunchedEffect(key1 = account) {
        sharedViewModel.loginEventListener = object : LoginEventListener {
            override fun onLoginEvent() {
                if (upsertAccount != null){

                    if (account != null){
                        account.name = upsertAccount?.name.orEmpty()
                        setupViewModel.upsertAccount(account)
                    }else{
                        setupViewModel.insertNewAccount(upsertAccount!!)
                    }

                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateAccountScreen(
    name: String,
    onBack: () -> Unit,
    account: (Account) -> Unit
) {

    val accountName = remember {
        mutableStateOf(name)
    }

    LaunchedEffect(key1 = name) {
        if (accountName.value.isEmpty()) {
            accountName.value = name
        }
    }

    account(
        Account(0, name = accountName.value)
    )

    Scaffold(
        topBar = {
            AppBar(title = stringResource(id = R.string.account), navigationOnClick = {onBack.invoke()})
        }
    ) {
        BudgetTextField(
            text = accountName,
            label = stringResource(id = R.string.account_name),
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 8.dp)
        )
    }

}