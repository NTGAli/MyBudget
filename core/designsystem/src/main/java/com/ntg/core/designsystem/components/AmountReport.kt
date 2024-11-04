package com.ntg.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.mybudget.core.designsystem.R

@Composable
fun AmountReport(
    modifier: Modifier = Modifier,
    outcome: String?,
    income: String?
){


    val incomeBrush = Brush.linearGradient(
        colors = listOf(Color(0xFFD1666D), Color(0xFFB04F74)),
        start = Offset(0f, 0f),
        end = Offset(0f, Float.POSITIVE_INFINITY)
    )

    val expenseBrush = Brush.linearGradient(
        colors = listOf(Color(0xFF50AD98), Color(0xFF4DA8BD)),
        start = Offset(0f, 0f),
        end = Offset(0f, Float.POSITIVE_INFINITY)
    )

    Row(modifier = modifier) {
        Item(value = income.orEmpty(), title = stringResource(id = R.string.income), icon = BudgetIcons.income, tint = Color.White, backColor = expenseBrush)
        Item(value = outcome.orEmpty(), title = stringResource(id = R.string.outcome), icon = BudgetIcons.outcome, tint = Color.White, backColor = incomeBrush)
    }
}


@Composable
private fun RowScope.Item(
    value: String,
    title: String,
    icon: Int,
    tint: Color,
    backColor: Brush
){
    Row(
        modifier = Modifier
            .padding(end = 4.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shape = RoundedCornerShape(18.dp)
            )
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .weight(1f),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            modifier = Modifier.background(brush = backColor, shape = RoundedCornerShape(8.dp)).padding(4.dp),
            painter = painterResource(id = icon), contentDescription = null, tint = tint)


        Column(
            modifier = Modifier.weight(1f).padding(start = 8.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.outline))
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = value, style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.outlineVariant))
        }

    }
}