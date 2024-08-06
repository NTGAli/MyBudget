package com.ntg.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ntg.core.designsystem.theme.BudgetIcons

@Composable
fun TransactionItem(
    modifier: Modifier = Modifier,
    title: String,
    amount: String,
    date: String,
    type: String,
    divider: Boolean = false,
    attached: Boolean = false,
){

    Column(
        modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable {

            }
            .padding(horizontal = 8.dp)
    ) {

        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {
                Icon(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                    ,
                    painter = painterResource(id = BudgetIcons.food), contentDescription = "Category icon")
            }

            Column(modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)) {
                Text(text = title, style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = date, style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.outlineVariant))
            }

            Text(text = amount, style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface))


            if (type == "income"){
                Icon(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(4.dp)
                        ),
                    painter = painterResource(id = BudgetIcons.ArrowDown),
                    contentDescription = "Income icon",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            else{
                Icon(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(4.dp)
                        ),
                    painter = painterResource(id = BudgetIcons.ArrowUp),
                    contentDescription = "Income icon",
                    tint = MaterialTheme.colorScheme.error
                )
            }


        }

        if (attached){
            Row(modifier = Modifier.padding(top = 8.dp, start = 8.dp)) {
                Icon(
                    painter = painterResource(id = BudgetIcons.Pdf), contentDescription = "Category icon")
                Icon(
                    modifier = Modifier.padding(start = 4.dp),
                    painter = painterResource(id = BudgetIcons.Image), contentDescription = "Category icon")
            }
        }

        Spacer(modifier = Modifier.padding(bottom = 16.dp))

        if (divider){
            HorizontalDivider(
                modifier = Modifier.padding(start = 56.dp),
                color = MaterialTheme.colorScheme.surfaceDim)
        }
    }
}