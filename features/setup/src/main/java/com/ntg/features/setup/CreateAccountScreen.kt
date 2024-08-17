package com.ntg.features.setup

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ntg.core.designsystem.components.AppBar
import com.ntg.core.designsystem.components.BudgetTextField
import com.ntg.core.mybudget.common.SharedViewModel
import com.ntg.feature.setup.R

@Composable
fun CreateAccountRoute(
    sharedViewModel: SharedViewModel,
    setupViewModel: SetupViewModel = hiltViewModel(),
    id: Int? = null
) {
    sharedViewModel.setExpand.postValue(true)
    sharedViewModel.bottomNavTitle.postValue(stringResource(id = R.string.save))

    val account = setupViewModel.getAccount(id ?: -1).collectAsState(initial = null).value
    CreateAccountScreen(account?.name.orEmpty())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateAccountScreen(
    name: String
) {

    val accountName = remember {
        mutableStateOf(name)
    }

    Scaffold(
        topBar = {
            AppBar(title = stringResource(id = R.string.account))
        }
    ) {
        BudgetTextField(
            text = accountName,
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 8.dp)
        )
    }

}