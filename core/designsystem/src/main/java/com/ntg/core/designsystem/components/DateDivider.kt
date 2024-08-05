package com.ntg.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ntg.core.designsystem.theme.BudgetIcons

@Composable
fun DateDivider(
    modifier: Modifier = Modifier,
    date: String,
    amount: String,
    type: String,
    isCollapse: Boolean = false
){

    Row(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.surfaceContainer, shape = RoundedCornerShape(4.dp))
            .padding(4.dp)
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = date, style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.outlineVariant))

        Text(
            text = amount,
            style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )

        Icon(
            modifier = Modifier
                .padding(start = 4.dp)
                .background(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(4.dp)),
            painter = painterResource(id = BudgetIcons.ArrowDown),
            contentDescription = "", tint = MaterialTheme.colorScheme.secondary
        )
    }
}