@file:OptIn(ExperimentalMaterial3Api::class)

package com.ntg.features.report

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import com.ntg.core.designsystem.components.AppBar
import com.ntg.core.designsystem.components.ChartItem
import com.ntg.mybudget.core.designsystem.R

@Composable
fun ReportRoute() {

    ReportScreen()
}

@Composable
fun ReportScreen() {

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val inputData = remember { mutableMapOf(
        Pair(1, 20),
        Pair(2, 10),
        Pair(3, 15),
        Pair(4, 20),
        Pair(5, 12),
        Pair(6, 19),
        Pair(7, 30),
        Pair(8, 10),
        Pair(20, 5),
    ) }

    val outputData = remember { mutableMapOf(
        Pair(1, 10),
        Pair(2, 18),
        Pair(3, 25),
        Pair(4, 9),
        Pair(5, 20),
        Pair(6, 14),
        Pair(7, 6),
        Pair(8, 10),
        Pair(20, 35),
        Pair(25, 13),
        Pair(30, 0),
    ) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppBar(
                enableNavigation = false,
                title = stringResource(id = R.string.Report),
                scrollBehavior = scrollBehavior
            )
        },
    ) { padding ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ChartItem(inputData, inputData, outputData)
        }
    }
}