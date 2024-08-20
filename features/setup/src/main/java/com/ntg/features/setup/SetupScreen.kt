package com.ntg.features.setup

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ntg.core.designsystem.components.AccountSection
import com.ntg.core.designsystem.components.AppBar
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.core.model.AccountWithSources
import com.ntg.core.mybudget.common.LoginEventListener
import com.ntg.core.mybudget.common.SharedViewModel
import com.ntg.feature.setup.R
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Composable
fun SetupRoute(
    sharedViewModel: SharedViewModel,
    setupViewModel: SetupViewModel = hiltViewModel(),
    navigateToSource: (id: Int, sourceId: Int?) -> Unit,
    navigateToAccount: (id: Int) -> Unit,
    onShowSnackbar: suspend (Int, String?) -> Boolean,
) {

    sharedViewModel.setExpand.postValue(true)
    sharedViewModel.bottomNavTitle.postValue(stringResource(id = R.string.submit))

    val scope = rememberCoroutineScope()
    val accounts =
        setupViewModel.accountWithSources().collectAsStateWithLifecycle(initialValue = null)
//    val sources = setupViewModel.accounts().collectAsStateWithLifecycle(initialValue = null)
    SetupScreen(
        accounts,
        navigateToSource,
        navigateToAccount,
        editAccount = {
            navigateToAccount(it)
        }
    )

    LaunchedEffect(key1 = accounts) {
        sharedViewModel.loginEventListener = object : LoginEventListener {
            override fun onLoginEvent() {
                scope.launch {
                    if (accounts.value.orEmpty().isEmpty()) {
                        onShowSnackbar.invoke(R.string.err_no_aacount, null)
                    } else if (accounts.value.orEmpty().none {
                            it.sources.isNotEmpty()
                        }) {
                        onShowSnackbar.invoke(R.string.err_no_sources, null)
                    }else{

                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SetupScreen(
    accounts: State<List<AccountWithSources>?>,
    navigateToSource: (id: Int, sourceId: Int?) -> Unit,
    navigateToAccount: (id: Int) -> Unit,
    editAccount: (id: Int) -> Unit,
) {

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppBar(
                enableNavigation = false,
                title = stringResource(id = R.string.createAccount),
                scrollBehavior = scrollBehavior
            )
        }
    ) {

        LazyColumn(
            modifier = Modifier
                .padding(it)
        ) {

            item {
                Spacer(modifier = Modifier.padding(8.dp))
            }

            items(accounts.value.orEmpty()) { account ->
                AccountSection(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(top = 8.dp),
                    account = account, canEdit = true, insertNewItem = {
                        navigateToSource(account.accountId, null)
                    }, accountEndIconClick = {
                        editAccount(it)
                    }, onSourceEdit = {
                        navigateToSource(account.accountId, it)
                    })

            }

            item {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            width = 1.dp,
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHighest
                        )
                        .clickable {
                            navigateToAccount(0)
                        }
                        .padding(vertical = 18.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.add_new_account),
                        style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.outlineVariant)
                    )
                    Icon(
                        modifier = Modifier.padding(start = 8.dp),
                        painter = painterResource(id = BudgetIcons.Add),
                        contentDescription = "add account"
                    )
                }

            }

            item {
                Spacer(modifier = Modifier.padding(24.dp))
            }

        }

    }

}