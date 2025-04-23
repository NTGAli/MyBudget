package com.ntg.core.designsystem.components

import android.annotation.SuppressLint
import android.graphics.Typeface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.ntg.core.mybudget.common.orDefault
import com.ntg.core.mybudget.common.toPersianMonthlyDate
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.cartesianLayerPadding
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.shape.rounded
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.Defaults.AXIS_LABEL_ROTATION_DEGREES
import com.patrykandpatrick.vico.core.common.shape.CorneredShape

@Composable
fun ColumnsChart(
    outcomeList: MutableMap<Long, Long>
) {

    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(outcomeList) {
        modelProducer.runTransaction {
            columnSeries {
                series(outcomeList.keys.toList(), outcomeList.values.toList())
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

    val columnColors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.surfaceDim)

    CartesianChartHost(
        chart =
        rememberCartesianChart(
            rememberColumnCartesianLayer(
                columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                    columnColors.map { color ->
                        rememberLineComponent(
                            color = color,
                            thickness = 60.dp,
                            shape = CorneredShape.rounded(8.dp, 8.dp),
                        )
                    }
                )
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = bottomAxisValueFormatter(),
                guideline = null, line = null,tick = null,
                label = rememberAxisLabelComponent(
                    color = MaterialTheme.colorScheme.outlineVariant,/* textSize = MaterialTheme.typography.labelSmall.fontSize,*/
                    typeface = Typeface.create(MaterialTheme.typography.labelSmall.fontFamily.toString(), Typeface.NORMAL)
                ),
                labelRotationDegrees = -40f,
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

private fun bottomAxisValueFormatter(): CartesianValueFormatter = CartesianValueFormatter { _, x, _ ->

    val text = x.toLong().orDefault().toPersianMonthlyDate()
    text.ifEmpty { "..." }
}