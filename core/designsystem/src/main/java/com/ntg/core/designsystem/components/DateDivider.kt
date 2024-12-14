package com.ntg.core.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.core.mybudget.common.logd

@Composable
fun DateDivider(
    modifier: Modifier = Modifier,
    date: String,
    amount: Long,
    type: String,
    isCollapse: Boolean = false
){
    Row(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.surfaceContainer, shape = RoundedCornerShape(4.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = date, style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.outlineVariant))

        AnimatedVisibility(visible = !isCollapse) {
            Row {
                Spacer(Modifier.weight(1f))
                Text(
                    text = com.ntg.core.mybudget.common.formatInput(amount.toString()),
                    style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                Icon(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(16.dp),
                    painter = painterResource(id = if (amount > 0) BudgetIcons.plus else BudgetIcons.minus),
                    contentDescription = "", tint = if (amount > 0) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
                )
            }
        }


    }
}