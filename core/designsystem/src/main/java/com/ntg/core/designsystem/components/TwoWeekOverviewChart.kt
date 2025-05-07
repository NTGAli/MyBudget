package com.ntg.core.designsystem.components

import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ntg.core.designsystem.theme.MyBudgetTheme
import com.ntg.core.mybudget.common.Constants.BudgetType
import com.ntg.mybudget.core.designsystem.R

@Preview()
@Composable
private fun TwoWeekOverviewChartPreview(modifier: Modifier = Modifier) {
    MyBudgetTheme(darkTheme = false) {
        Surface {

            val thisWeekState = remember { mutableListOf(BudgetType.INCOME, BudgetType.EXPENSE, BudgetType.EXPENSE, BudgetType.NOTHING, BudgetType.NOTHING, BudgetType.NOTHING, BudgetType.NOTHING) }
            val previousWeek = remember { mutableListOf(BudgetType.INCOME, BudgetType.NOTHING, BudgetType.NOTHING, BudgetType.NOTHING, BudgetType.EXPENSE, BudgetType.EXPENSE, BudgetType.EXPENSE,) }

            TwoWeekOverviewChart(
                thisWeekState,
                previousWeek,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}

@Composable
fun TwoWeekOverviewChart(
    thisWeekState: MutableList<Int>,
    previousWeek: MutableList<Int>,
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
        Spacer(modifier = Modifier.height(4.dp))

        WeeklyOverview(stringResource(R.string.ThisWeek), thisWeekState)
        WeeklyOverview(stringResource(R.string.PreviousWeek), previousWeek)
    }
}

@Composable
private fun WeeklyOverview(title: String, data: MutableList<Int>) {

    val green = MaterialTheme.colorScheme.secondary
    val red = MaterialTheme.colorScheme.error
    val gray = MaterialTheme.colorScheme.surfaceDim

    Row(
        modifier = Modifier
            .padding(top = 4.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(79.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(7) { index ->
                val color by remember {
                    derivedStateOf {
                        when (data[index]) {
                            BudgetType.INCOME -> green
                            BudgetType.EXPENSE -> red
                            else -> gray
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color)
                )
            }
        }
    }
}