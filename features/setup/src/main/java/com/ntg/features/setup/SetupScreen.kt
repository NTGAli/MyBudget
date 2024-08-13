package com.ntg.features.setup

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ntg.core.designsystem.components.AccountSection
import com.ntg.core.designsystem.components.AppBar
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.core.model.Account
import com.ntg.core.mybudget.common.LoginEventListener
import com.ntg.core.mybudget.common.SharedViewModel
import com.ntg.feature.setup.R

@Composable
fun ScreenRoute(
    sharedViewModel: SharedViewModel,
    setupViewModel: SetupViewModel = hiltViewModel(),
    navigateToSource: (id: Int) -> Unit
){

    sharedViewModel.setExpand.postValue(true)
    sharedViewModel.bottomNavTitle.postValue(stringResource(id = R.string.submit))

    DisposableEffect(Unit) {
        val listener = object : LoginEventListener {
            override fun onLoginEvent() {
//                sharedViewModel.bottomNavTitle.postValue("HI")
            }
        }
        sharedViewModel.loginEventListener = listener
        onDispose {
            sharedViewModel.loginEventListener = null
        }
    }

    val accounts = setupViewModel.accounts().collectAsStateWithLifecycle(initialValue = null)
    SetupScreen(
        accounts,
        navigateToSource
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SetupScreen(accounts: State<List<Account?>?>, navigateToSource: (id: Int) -> Unit) {

    val test = listOf(
        Account(
            id = 0,
            sId = null,
            name = "حساب شخصی",
            dateCreated = System.currentTimeMillis().toString()
        )
    )

    Scaffold(
        topBar = {
            AppBar(
                enableNavigation = false,
                title = stringResource(id = R.string.createAccount)
            )
        }
    ) {

        LazyColumn(
            modifier = Modifier
                .padding(it)
                .padding(top = 16.dp)
        ) {

            items(test.orEmpty()){
                if (it != null) {
                    AccountSection(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        account = it, canEdit = true, insertNewItem = {
                            navigateToSource(it.id)
                        })
                }

            }

            item {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(width = 1.dp, shape = RoundedCornerShape(16.dp),color = MaterialTheme.colorScheme.surfaceContainerHighest)
                        .clickable {

                        }
                        .padding(vertical = 18.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(id = R.string.add_new_account), style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.outlineVariant))
                    Icon(
                        modifier = Modifier.padding(start = 8.dp),
                        painter = painterResource(id = BudgetIcons.Add), contentDescription = "add account")
                }

            }

        }

    }

}