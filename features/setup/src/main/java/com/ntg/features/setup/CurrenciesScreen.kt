package com.ntg.features.setup

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.ntg.core.mybudget.common.SharedViewModel
import kotlinx.coroutines.flow.collect

@Composable
fun CurrenciesRoute(
    sharedViewModel: SharedViewModel,
    setupViewModel: SetupViewModel = hiltViewModel(),
    onBack:() -> Unit
){

    LaunchedEffect(key1 = Unit) {
        setupViewModel.loadCurrencies().collect{
        }
    }

}

@Composable
private fun CurrenciesScreen(){




}