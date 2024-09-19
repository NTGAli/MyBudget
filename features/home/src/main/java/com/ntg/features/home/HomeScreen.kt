package com.ntg.features.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ntg.core.designsystem.components.AccountSelector
import com.ntg.core.designsystem.components.AppBar
import com.ntg.core.designsystem.components.CardReport
import com.ntg.core.designsystem.components.FullScreenBottomSheet
import com.ntg.core.designsystem.components.SwitchText
import com.ntg.core.designsystem.model.SwitchItem
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.core.model.AccountWithSources
import com.ntg.core.model.Transaction
import com.ntg.core.mybudget.common.LoginEventListener
import com.ntg.core.mybudget.common.SharedViewModel
import com.ntg.core.mybudget.common.formatCurrency
import com.ntg.feature.home.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun HomeRoute(
    sharedViewModel: SharedViewModel,
    homeViewModel: HomeViewModel = hiltViewModel()
){
    var expandTransaction = remember { mutableStateOf(false) }
    sharedViewModel.setExpand.postValue(expandTransaction.value)
    sharedViewModel.bottomNavTitle.postValue(if (expandTransaction.value) "submit" else null)

    val currentAccount = homeViewModel.selectedAccount().collectAsState(initial = null)

    if (currentAccount.value != null && currentAccount.value.orEmpty().isNotEmpty()){
        val sourceIds = currentAccount.value.orEmpty().first().sources.map { it?.id ?: 0 }

        val transactions = homeViewModel.transactions(sourceIds).collectAsState(initial = null)
        HomeScreen(currentAccount.value.orEmpty().first(), transactions, expandTransaction)
    }

    LaunchedEffect(key1 = Unit) {
        sharedViewModel.loginEventListener = object : LoginEventListener {
            override fun onLoginEvent() {
                expandTransaction.value = !expandTransaction.value
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    currentAccount: AccountWithSources,
    transactions: State<List<Transaction>?>,
    expandTransaction: MutableState<Boolean>
) {
    Scaffold(
        topBar = {
            AppBar(
                titleState = {
                    AccountSelector(title = currentAccount.accountName, subTitle = stringResource(
                        id = R.string.items_format, currentAccount.sources.size
                    ))
                },
                enableNavigation = false
            )
        }
        ) {padding ->

        LazyColumn(
            Modifier
                .padding(padding)
                .fillMaxSize()) {

            item {

            }

            item {
                CardReport(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .padding(horizontal = 24.dp),
                    title = formatCurrency(
                        amount = transactions.value?.map { it.amount }?.sum() ?: 0L,
                        mask = "###,###",
                        currency = "ت",
                        pos = 2
                    ), subTitle = "موجودی همه حساب ها", out =
                    formatCurrency(
                        amount = transactions.value?.filter { it.type == "out" }.orEmpty()
                            .sumOf { it.amount },
                        mask = "###,###",
                        currency = "ت",
                        pos = 2
                    ), inValue = formatCurrency(
                        amount = transactions.value?.filter { it.type == "in" }.orEmpty()
                            .sumOf { it.amount },
                        mask = "###,###",
                        currency = "ت",
                        pos = 2
                    ))

                Text(
                    modifier = Modifier.padding(top = 16.dp, start = 32.dp, bottom = 16.dp),
                    text = stringResource(id = R.string.transactions), style = MaterialTheme.typography.titleMedium.copy(MaterialTheme.colorScheme.outline))
            }

        }



    }



    InsertTransactionView(expandTransaction)


}



@Composable
fun InsertTransactionView(
    expandTransaction : MutableState<Boolean>
){


    FullScreenBottomSheet(showSheet = expandTransaction, appbar = {
        Row(modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background), verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .padding(start = 16.dp),
                onClick = { /*TODO*/ }) {
                Icon(painter = painterResource(id = BudgetIcons.directionDown), contentDescription = "close")
            }

            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
                ,
                text = stringResource(id = R.string.new_transaction), style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))

            DateItem(unixTime = 123L)
        }
    }) {
        Column(modifier = Modifier
//            .padding(it)
            .verticalScroll(rememberScrollState())) {


            val items = listOf(
                SwitchItem(0, stringResource(id = com.ntg.mybudget.core.designsystem.R.string.internal_transfer), tint = MaterialTheme.colorScheme.onPrimary, backColor = MaterialTheme.colorScheme.primary),
                SwitchItem(0, stringResource(id = com.ntg.mybudget.core.designsystem.R.string.income), tint = MaterialTheme.colorScheme.onSecondary, backColor = MaterialTheme.colorScheme.secondary),
                SwitchItem(0, stringResource(id = com.ntg.mybudget.core.designsystem.R.string.outcome), tint = MaterialTheme.colorScheme.onError, backColor = MaterialTheme.colorScheme.error),
            )

            SwitchText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                items = items) {

            }


        }
    }


}


@Composable
fun DateItem(
    modifier: Modifier = Modifier,
    unixTime: Long
){

    Row(
        modifier = modifier
            .padding(end = 16.dp)
            .background(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.surfaceContainer
            )
            .padding(vertical = 2.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "12 مرداد 1402", style = MaterialTheme.typography.labelMedium.copy(MaterialTheme.colorScheme.onSurfaceVariant))
        Box(modifier = Modifier
            .padding(horizontal = 8.dp)
            .size(4.dp)
            .background(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceDim))
        Text(text = "12:14", style = MaterialTheme.typography.labelMedium.copy(MaterialTheme.colorScheme.onSurfaceVariant))
        
        Icon(painter = painterResource(id = BudgetIcons.CalenderTick), contentDescription = "calender", tint = MaterialTheme.colorScheme.outline)
    }

}



