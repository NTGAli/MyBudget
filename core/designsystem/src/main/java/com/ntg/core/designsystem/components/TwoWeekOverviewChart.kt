package com.ntg.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.ntg.core.designsystem.theme.MyBudgetTheme
import com.ntg.core.model.Transaction
import com.ntg.core.mybudget.common.Constants
import com.ntg.core.mybudget.common.Constants.BudgetType
import com.ntg.core.mybudget.common.formatInput
import com.ntg.core.mybudget.common.getDayOfWeek
import com.ntg.core.mybudget.common.getJalaliDayOfMonth
import com.ntg.mybudget.core.designsystem.R

data class DayTransactionData(
    val dayOfMonth: Int,
    val incomeAmount: Long,
    val expenseAmount: Long,
    val type: Int // BudgetType
)

data class WeekData(
    val title: String,
    val days: List<DayTransactionData>
)

@Preview()
@Composable
private fun TwoWeekOverviewChartPreview(modifier: Modifier = Modifier) {
    MyBudgetTheme(darkTheme = false) {
        Surface {
            // Sample data for preview
            val thisWeekData = WeekData(
                title = "این هفته",
                days = listOf(
                    DayTransactionData(15, 500000, 0, BudgetType.INCOME),
                    DayTransactionData(16, 0, 250000, BudgetType.EXPENSE),
                    DayTransactionData(17, 0, 180000, BudgetType.EXPENSE),
                    DayTransactionData(18, 0, 0, BudgetType.NOTHING),
                    DayTransactionData(19, 0, 0, BudgetType.NOTHING),
                    DayTransactionData(20, 0, 0, BudgetType.NOTHING),
                    DayTransactionData(21, 0, 0, BudgetType.NOTHING)
                )
            )

            val previousWeekData = WeekData(
                title = "هفته قبل",
                days = listOf(
                    DayTransactionData(8, 300000, 0, BudgetType.INCOME),
                    DayTransactionData(9, 0, 0, BudgetType.NOTHING),
                    DayTransactionData(10, 0, 0, BudgetType.NOTHING),
                    DayTransactionData(11, 0, 0, BudgetType.NOTHING),
                    DayTransactionData(12, 0, 120000, BudgetType.EXPENSE),
                    DayTransactionData(13, 0, 90000, BudgetType.EXPENSE),
                    DayTransactionData(14, 0, 200000, BudgetType.EXPENSE)
                )
            )

            TwoWeekOverviewChart(
                thisWeekData = thisWeekData,
                previousWeekData = previousWeekData,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}

@Composable
fun TwoWeekOverviewChart(
    thisWeekData: WeekData,
    previousWeekData: WeekData,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = stringResource(R.string.DailyIncomeOutcome),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        WeeklyOverview(thisWeekData)
        Spacer(modifier = Modifier.height(4.dp))
        WeeklyOverview(previousWeekData)
    }
}

@Composable
private fun WeeklyOverview(weekData: WeekData) {
    val green = MaterialTheme.colorScheme.secondary
    val red = MaterialTheme.colorScheme.error
    val gray = MaterialTheme.colorScheme.surfaceDim

    var selectedDay by remember { mutableStateOf<DayTransactionData?>(null) }

    Row(
        modifier = Modifier
            .padding(top = 4.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = weekData.title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(79.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            weekData.days.forEachIndexed { index, dayData ->
                DayItem(
                    dayData = dayData,
                    color = when (dayData.type) {
                        BudgetType.INCOME -> green
                        BudgetType.EXPENSE -> red
                        else -> gray
                    },
                    modifier = Modifier.weight(1f),
                    onClick = { selectedDay = dayData }
                )
            }
        }
    }

    // Show popup when a day is selected
    selectedDay?.let { day ->
        DayTransactionPopup(
            dayData = day,
            onDismiss = { selectedDay = null }
        )
    }
}

@Composable
private fun DayItem(
    dayData: DayTransactionData,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(color)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = dayData.dayOfMonth.toString(),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                ),
                color = when {
                    color.luminance() < 0.5f -> Color.White
                    else -> MaterialTheme.colorScheme.onSurface
                },
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun DayTransactionPopup(
    dayData: DayTransactionData,
    onDismiss: () -> Unit
) {
    Popup(
        onDismissRequest = onDismiss,
        properties = PopupProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .width(280.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with date
                Text(
                    text = "${dayData.dayOfMonth} ${getCurrentJalaliMonthName()}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Income section
                if (dayData.incomeAmount > 0) {
                    TransactionRow(
                        label = stringResource(R.string.income),
                        amount = formatInput(dayData.incomeAmount.toString()),
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Expense section
                if (dayData.expenseAmount > 0) {
                    TransactionRow(
                        label = stringResource(R.string.outcome),
                        amount = formatInput(dayData.expenseAmount.toString()),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // No transactions message
                if (dayData.incomeAmount == 0L && dayData.expenseAmount == 0L) {
                    Text(
                        text = "تراکنشی ثبت نشده",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                // Net amount (if both income and expense exist)
                if (dayData.incomeAmount > 0 && dayData.expenseAmount > 0) {
                    val netAmount = dayData.incomeAmount - dayData.expenseAmount
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "خالص:",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${formatInput(Math.abs(netAmount).toString())} ${if (netAmount >= 0) "درآمد" else "هزینه"}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (netAmount >= 0) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionRow(
    label: String,
    amount: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(16.dp)
                    .background(
                        color = color,
                        shape = RoundedCornerShape(2.dp)
                    )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Text(
            text = "$amount تومان",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = color
        )
    }
}

// Helper function to get current Jalali month name
@Composable
private fun getCurrentJalaliMonthName(): String {
    // You should implement this based on your existing Jalali date utilities
    // This is a placeholder - replace with your actual implementation
    val months = listOf(
        "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
    )
    // Return current month based on your date utilities
    return months[0] // Placeholder - implement actual logic
}


fun transactionsToWeekData(
    transactions: List<Transaction>,
    weekTitle: String,
    startDate: Long
): WeekData {
    // Group transactions by day
    val transactionsByDay = transactions.groupBy { transaction ->
        getDayOfWeek(transaction.date)
    }

    val days = mutableListOf<DayTransactionData>()

    // Create 7 days starting from startDate
    for (dayIndex in 0..6) {
        val dayTimestamp = startDate + (dayIndex * 24 * 60 * 60 * 1000) // Add days in milliseconds
        val dayOfMonth = getJalaliDayOfMonth(dayTimestamp)
        val dayOfWeek = getDayOfWeek(dayTimestamp)

        val dayTransactions = transactionsByDay[dayOfWeek] ?: emptyList()

        val incomeAmount = dayTransactions
            .filter { it.type == BudgetType.INCOME }
            .sumOf { it.amount }

        val expenseAmount = dayTransactions
            .filter { it.type == BudgetType.EXPENSE }
            .sumOf { it.amount }

        val type = when {
            incomeAmount > expenseAmount -> BudgetType.INCOME
            expenseAmount > incomeAmount -> BudgetType.EXPENSE
            incomeAmount > 0 || expenseAmount > 0 -> {
                // If they're equal but not zero, determine by which is larger or most recent
                if (incomeAmount == expenseAmount) BudgetType.INCOME else BudgetType.NOTHING
            }
            else -> BudgetType.NOTHING
        }

        days.add(
            DayTransactionData(
                dayOfMonth = dayOfMonth,
                incomeAmount = incomeAmount,
                expenseAmount = expenseAmount,
                type = type
            )
        )
    }

    return WeekData(
        title = weekTitle,
        days = days
    )
}

/**
 * Converts existing thisWeekState and lastWeekState to new format
 * This is for backward compatibility if you need to convert existing data
 */
fun convertLegacyWeekState(
    weekState: MutableList<Int>,
    weekTitle: String,
    startDate: Long
): WeekData {
    val days = weekState.mapIndexed { index, type ->
        val dayTimestamp = startDate + (index * 24 * 60 * 60 * 1000)
        val dayOfMonth = getJalaliDayOfMonth(dayTimestamp)

        // For legacy data, we don't have actual amounts, so use placeholder values
        val (income, expense) = when (type) {
            Constants.BudgetType.INCOME -> Pair(1000000L, 0L) // 1M as placeholder
            BudgetType.EXPENSE -> Pair(0L, 500000L) // 500K as placeholder
            else -> Pair(0L, 0L)
        }

        DayTransactionData(
            dayOfMonth = dayOfMonth,
            incomeAmount = income,
            expenseAmount = expense,
            type = type
        )
    }

    return WeekData(
        title = weekTitle,
        days = days
    )
}