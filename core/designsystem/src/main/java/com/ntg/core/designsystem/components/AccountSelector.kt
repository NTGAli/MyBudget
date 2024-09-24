package com.ntg.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun AccountSelector(
    modifier: Modifier = Modifier,
    title: String,
    subTitle: String,
    onClick: () -> Unit
){

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick.invoke() }
            .padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.background(color = MaterialTheme.colorScheme.surfaceContainer, shape = RoundedCornerShape(8.dp)).padding(4.dp),
            painter = painterResource(id = BudgetIcons.wallet), contentDescription = "wallet icon")

        Column(
            modifier = Modifier
                .padding(start = 8.dp, end = 16.dp)
                .padding(vertical = 4.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.labelMedium.copy(MaterialTheme.colorScheme.outlineVariant))
            Text(text = subTitle, style = MaterialTheme.typography.labelSmall.copy(MaterialTheme.colorScheme.outline))
        }
        
        Icon(painter = painterResource(id = BudgetIcons.directionDown), contentDescription = "wallet icon")
    }

}