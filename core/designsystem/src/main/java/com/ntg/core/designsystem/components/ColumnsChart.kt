package com.ntg.core.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisTickComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
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
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.shape.CorneredShape

@Composable
fun ColumnsChart(walletBalance: MutableMap<Int, Int>, inputList: MutableMap<Int, Int>, outputList: MutableMap<Int, Int>) {

    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(walletBalance, inputList, outputList) {
        modelProducer.runTransaction {
            lineSeries { series(walletBalance.keys.toList(), walletBalance.values.toList())}
            columnSeries {
                series(inputList.keys.toList(), inputList.values.toList())
                series(outputList.keys.toList(), outputList.values.toList())
            }
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        ChartLineAndColumns1(modelProducer)
    }
}

@Composable
private fun ChartLineAndColumns1(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier
) {

    val columnColors = listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.error)
    val lineColor = MaterialTheme.colorScheme.primary

    CartesianChartHost(
        chart =
        rememberCartesianChart(
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
            bottomAxis = HorizontalAxis.rememberBottom(
//                valueFormatter = bottomAxisValueFormatter,
                guideline = null,
                line = rememberAxisLineComponent(color = MaterialTheme.colorScheme.outlineVariant),
                label = rememberAxisLabelComponent(color = MaterialTheme.colorScheme.outlineVariant),
                tick = rememberAxisTickComponent(color = MaterialTheme.colorScheme.outlineVariant),
                itemPlacer = remember { HorizontalAxis.ItemPlacer.segmented() },
            ),
            marker = rememberMarker(),
            layerPadding = cartesianLayerPadding(scalableStart = 16.dp, scalableEnd = 16.dp),
        ),
        modelProducer = modelProducer,
        modifier = modifier,
        zoomState = rememberVicoZoomState(zoomEnabled = false),
    )
}