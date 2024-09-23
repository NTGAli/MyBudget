package com.ntg.features.setup

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ntg.core.designsystem.components.AppBar
import com.ntg.core.designsystem.components.CurrencyItem
import com.ntg.core.designsystem.components.LoadingView
import com.ntg.core.designsystem.model.AppbarItem
import com.ntg.core.model.res.Currency
import com.ntg.core.mybudget.common.SharedViewModel
import com.ntg.feature.setup.R
import kotlinx.coroutines.flow.flowOf

@Composable
fun CurrenciesRoute(
    sharedViewModel: SharedViewModel,
    setupViewModel: SetupViewModel = hiltViewModel(),
    onBack: () -> Unit
) {

    val currencies =
        setupViewModel.loadCurrencies().collectAsStateWithLifecycle(initialValue = null)

    CurrenciesScreen(
        currencies = currencies,
        onBack = onBack
    ){
        setupViewModel.selectedCurrency = flowOf(it)
        onBack()
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrenciesScreen(currencies: State<List<Currency>?>, onBack: () -> Unit,
                             selectedCurrency:(Currency) -> Unit) {

    var query by remember {
        mutableStateOf("")
    }
    val enableSearchBar = remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppBar(
                title = stringResource(id = R.string.currency),
                actions = listOf(
                    AppbarItem(
                        0,
                        Icons.Rounded.Search,
                        MaterialTheme.colorScheme.outline
                    )
                ),
                enableSearchbar = enableSearchBar,
                navigationOnClick = {
                    onBack()
                },
                actionOnClick = {
                    enableSearchBar.value = !enableSearchBar.value
                },
                onQueryChange = {
                    query = it
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) {
        if (currencies.value.orEmpty().isEmpty()){
            LoadingView()
        }else{
            LazyColumn(modifier = Modifier.padding(it)) {

                items(currencies.value.orEmpty()
                    .filter { it.isCrypto == 0 }.filter {
                        it.faName.orEmpty().contains(query) || it.enName.orEmpty()
                            .contains(query) || it.symbol.orEmpty()
                            .contains(query) || it.country.orEmpty().contains(query) ||
                                it.countryAlpha2.orEmpty().contains(query)
                    }) {

                    CurrencyItem(
                        currency = it
                    ) {
                        selectedCurrency(it)
                    }

                }

            }
        }

    }
}