package com.ntg.login

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ntg.core.designsystem.components.AppBar
import com.ntg.core.designsystem.components.Country
import com.ntg.core.designsystem.components.CountryItem
import com.ntg.core.designsystem.model.AppbarItem
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.feature.login.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

@Composable
fun CountriesRoute(loginViewModel: LoginViewModel, onBack: () -> Unit) {
    SelectCountryScreen(loginViewModel, onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCountryScreen(
    loginViewModel: LoginViewModel,
    onBack: () -> Unit
) {

    val showSearch by remember {
        mutableStateOf(false)
    }
    var query by remember {
        mutableStateOf("")
    }
    val enableSearchBar = remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()


    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppBar(
                title = stringResource(id = R.string.choose_your_countries),
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
    ) { paddingValues ->
        val context = LocalContext.current
        val countries = getCountries(context).sortedBy { it.name }
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues),
            content = {

                itemsIndexed(countries.filter {
                    it.name.orEmpty().lowercase().contains(query.lowercase())
                }) { index, it ->
                    CountryItem(country = it) {
                        loginViewModel.countrySelected.postValue(it.code)
                        onBack()
                    }

                    if (index != countries.size - 1 && query.isEmpty()) {
                        if (countries[index + 1].name.orEmpty().first() != it.name.orEmpty()
                                .first()
                        ) {
                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 32.dp)
                                    .width(1.dp),
                                color = MaterialTheme.colorScheme.surfaceDim
                            )
                        }
                    }
                }

            })
    }


    if (showSearch) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }

}

private fun getCountries(context: Context): ArrayList<Country> {
    val countriesArray = arrayListOf<Country>()

    try {
        val inputStream = context.assets.open("countries.txt")
        val reader = BufferedReader(InputStreamReader(inputStream))
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            var countryName = Country()
            val parts = line?.split(";")
            countryName.name = parts?.get(2)
            countryName.code = parts?.get(0)
            countryName.shortname = parts?.get(1)
            countriesArray.add(countryName)
        }
        reader.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return countriesArray
}