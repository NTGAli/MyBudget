package com.ntg.core.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.core.mybudget.common.orDefault
import com.ntg.core.mybudget.common.toPersianMonthlyDate
import com.ntg.mybudget.core.designsystem.R
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberTop
import com.patrykandpatrick.vico.compose.cartesian.cartesianLayerPadding
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.shape.rounded
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.shape.CorneredShape

@Composable
fun IncomeOutcomeChart(
    walletBalance: MutableMap<Long, Long>,
    incomeList: MutableMap<Long, Long>,
    outcomeList: MutableMap<Long, Long>,
) {
    val incomeColor = MaterialTheme.colorScheme.secondary
    val outcomeColor = MaterialTheme.colorScheme.error

    val showIncome = remember { mutableStateOf(false) }
    val showOutcome = remember { mutableStateOf(true) }

    val columnColors by remember {
        derivedStateOf {
            if (showIncome.value && showOutcome.value) {
                mutableListOf(incomeColor, outcomeColor)
            } else if (showIncome.value) {
                mutableListOf(incomeColor)
            } else if (showOutcome.value) {
                mutableListOf(outcomeColor)
            } else {
                // when user uncheck both
                showOutcome.value = true
                mutableListOf(outcomeColor)
            }
        }
    }

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(walletBalance, incomeList, outcomeList, showIncome.value, showOutcome.value) {
        modelProducer.runTransaction {
            if (showIncome.value && showOutcome.value)
                lineSeries { series(walletBalance.keys.toList(), walletBalance.values.toList()) }

            columnSeries {
                if (showIncome.value)
                    series(incomeList.keys.toList(), incomeList.values.toList())

                if (showOutcome.value)
                    series(outcomeList.keys.toList(), outcomeList.values.toList())

            }
        }
    }

    Column() {
        Row {
            Spacer(modifier = Modifier.width(18.dp))
            SampleChip(
                showOutcome,
                stringResource(R.string.outcome),
                if (showOutcome.value) BudgetIcons.Close else BudgetIcons.Plus,
                MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.width(8.dp))
            SampleChip(
                showIncome,
                stringResource(R.string.income),
                if (showIncome.value) BudgetIcons.Close else BudgetIcons.Plus,
                MaterialTheme.colorScheme.secondary
            )
        }

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            ChartLineAndColumns(modelProducer = modelProducer, columnColors)
        }
    }
}

@Composable
private fun ChartLineAndColumns(
    modelProducer: CartesianChartModelProducer,
    columnColors: MutableList<Color>,
    modifier: Modifier = Modifier
) {

    val lineColor = MaterialTheme.colorScheme.primary

    CartesianChartHost(
        chart =
        rememberCartesianChart(
            rememberLineCartesianLayer(LineCartesianLayer.LineProvider.series( // blue line
                LineCartesianLayer.rememberLine(
                    remember { LineCartesianLayer.LineFill.single(fill(lineColor)) }
                )
            )),
            rememberColumnCartesianLayer( // red and green column
                columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                    columnColors.map { color ->
                        rememberLineComponent(
                            color = color,
                            thickness = 8.dp,
                            shape = CorneredShape.rounded(2.dp),
                        )
                    }
                )
            ),
//            bottomAxis = HorizontalAxis.rememberBottom(
//                valueFormatter = bottomAxisValueFormatter(),
//                guideline = null,
//                line = rememberAxisLineComponent(color = MaterialTheme.colorScheme.outlineVariant),
//                label = rememberAxisLabelComponent(color = MaterialTheme.colorScheme.outlineVariant),
//                tick = rememberAxisTickComponent(color = MaterialTheme.colorScheme.outlineVariant),
//                itemPlacer = remember { HorizontalAxis.ItemPlacer.segmented() }
//            ),
            topAxis = HorizontalAxis.rememberTop(
                valueFormatter = bottomAxisValueFormatter(),
                guideline = null, line = null, tick = null,
                label = rememberAxisLabelComponent(color = MaterialTheme.colorScheme.outlineVariant),
                itemPlacer = remember { HorizontalAxis.ItemPlacer.segmented() }
            ),
            marker = rememberMarker(),
            layerPadding = cartesianLayerPadding(scalableStart = 16.dp, scalableEnd = 16.dp),
        ),
        modelProducer = modelProducer,
        modifier = modifier,
        zoomState = rememberVicoZoomState(zoomEnabled = false),
    )
}

private fun bottomAxisValueFormatter(): CartesianValueFormatter = CartesianValueFormatter { _, x, _ ->

    val text = x.toLong().orDefault().toPersianMonthlyDate()
    text.ifEmpty { "..." }
}
