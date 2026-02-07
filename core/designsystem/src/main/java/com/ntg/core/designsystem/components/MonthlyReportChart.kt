package com.ntg.core.designsystem.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ntg.core.mybudget.common.withSuffix
import com.ntg.mybudget.core.designsystem.R
import kotlin.math.abs

data class MonthlyChartData(
    val monthName: String,
    val year: Int,
    val daysInMonth: Int,
    val dailyIncome: Map<Int, Long>,
    val dailyExpense: Map<Int, Long>,
    val totalIncome: Long,
    val totalExpense: Long,
    val currentDay: Int
)

@Composable
fun MonthlyReportChart(
    data: MonthlyChartData,
    modifier: Modifier = Modifier
) {
    val incomeColor = MaterialTheme.colorScheme.secondary
    val expenseColor = MaterialTheme.colorScheme.error
    val onSurfaceColor = MaterialTheme.colorScheme.onSurfaceVariant
    val outlineColor = MaterialTheme.colorScheme.outlineVariant
    val primaryColor = MaterialTheme.colorScheme.primary

    val balance = data.totalIncome - data.totalExpense
    val balanceColor = if (balance >= 0) incomeColor else expenseColor

    Column(modifier = modifier) {

        // Month title
        Text(
            text = "${data.monthName} ${data.year}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Summary row: Income | Expense | Balance
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MonthlySummaryCard(
                label = stringResource(R.string.income),
                amount = data.totalIncome.withSuffix(),
                color = incomeColor,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            MonthlySummaryCard(
                label = stringResource(R.string.outcome),
                amount = data.totalExpense.withSuffix(),
                color = expenseColor,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            MonthlySummaryCard(
                label = stringResource(R.string.balance),
                amount = abs(balance).withSuffix(),
                color = balanceColor,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Legend
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(incomeColor)
            )
            Text(
                text = stringResource(R.string.income),
                style = MaterialTheme.typography.labelSmall,
                color = onSurfaceColor,
                modifier = Modifier.padding(start = 4.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(expenseColor)
            )
            Text(
                text = stringResource(R.string.outcome),
                style = MaterialTheme.typography.labelSmall,
                color = onSurfaceColor,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bar chart
        val dayWidthDp = 36.dp
        val chartHeightDp = 220.dp
        val scrollState = rememberScrollState()
        val density = LocalDensity.current

        // Auto-scroll to show the current day area
        LaunchedEffect(data.currentDay) {
            val targetScrollPx = with(density) {
                (dayWidthDp * (data.currentDay - 5).coerceAtLeast(0)).toPx()
            }
            scrollState.animateScrollTo(targetScrollPx.toInt())
        }

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(chartHeightDp)
                    .horizontalScroll(scrollState)
            ) {
                val chartWidthDp = dayWidthDp * data.daysInMonth

                Canvas(
                    modifier = Modifier
                        .width(chartWidthDp)
                        .fillMaxHeight()
                ) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val bottomPadding = 40f
                    val topPadding = 16f
                    val chartDrawHeight = canvasHeight - bottomPadding - topPadding

                    val maxValue = maxOf(
                        data.dailyIncome.values.maxOrNull() ?: 0L,
                        data.dailyExpense.values.maxOrNull() ?: 0L,
                        1L
                    )

                    val dayWidthPx = dayWidthDp.toPx()
                    val barWidth = dayWidthPx * 0.32f
                    val barGap = 2f

                    // Dashed horizontal grid lines
                    val gridCount = 4
                    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
                    for (i in 1..gridCount) {
                        val y = topPadding + chartDrawHeight * (1f - i.toFloat() / gridCount)
                        drawLine(
                            color = outlineColor.copy(alpha = 0.4f),
                            start = Offset(0f, y),
                            end = Offset(canvasWidth, y),
                            strokeWidth = 1f,
                            pathEffect = dashEffect
                        )
                    }

                    // Baseline
                    val baselineY = topPadding + chartDrawHeight
                    drawLine(
                        color = outlineColor,
                        start = Offset(0f, baselineY),
                        end = Offset(canvasWidth, baselineY),
                        strokeWidth = 1.5f
                    )

                    // Bars and labels for each day
                    for (day in 1..data.daysInMonth) {
                        val centerX = (day - 1) * dayWidthPx + dayWidthPx / 2f
                        val income = data.dailyIncome[day] ?: 0L
                        val expense = data.dailyExpense[day] ?: 0L

                        // Current day highlight background
                        if (day == data.currentDay) {
                            drawRoundRect(
                                color = primaryColor.copy(alpha = 0.07f),
                                topLeft = Offset(centerX - dayWidthPx / 2f + 2f, topPadding),
                                size = Size(dayWidthPx - 4f, chartDrawHeight),
                                cornerRadius = CornerRadius(8f, 8f)
                            )
                        }

                        // Income bar (left side)
                        if (income > 0L) {
                            val barHeight = (income.toFloat() / maxValue) * chartDrawHeight
                            drawRoundRect(
                                color = incomeColor,
                                topLeft = Offset(
                                    centerX - barWidth - barGap / 2f,
                                    baselineY - barHeight
                                ),
                                size = Size(barWidth, barHeight),
                                cornerRadius = CornerRadius(6f, 6f)
                            )
                        }

                        // Expense bar (right side)
                        if (expense > 0L) {
                            val barHeight = (expense.toFloat() / maxValue) * chartDrawHeight
                            drawRoundRect(
                                color = expenseColor,
                                topLeft = Offset(
                                    centerX + barGap / 2f,
                                    baselineY - barHeight
                                ),
                                size = Size(barWidth, barHeight),
                                cornerRadius = CornerRadius(6f, 6f)
                            )
                        }

                        // Day number label
                        drawContext.canvas.nativeCanvas.drawText(
                            "$day",
                            centerX,
                            canvasHeight - 6f,
                            Paint().apply {
                                textSize = 10.sp.toPx()
                                textAlign = Paint.Align.CENTER
                                color = if (day == data.currentDay) {
                                    primaryColor.toArgb()
                                } else {
                                    onSurfaceColor.toArgb()
                                }
                                if (day == data.currentDay) {
                                    typeface = Typeface.DEFAULT_BOLD
                                }
                            }
                        )

                        // Current day dot indicator
                        if (day == data.currentDay) {
                            drawCircle(
                                color = primaryColor,
                                radius = 3f,
                                center = Offset(centerX, canvasHeight - 0f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthlySummaryCard(
    label: String,
    amount: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.08f))
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = amount,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = color
        )
    }
}
