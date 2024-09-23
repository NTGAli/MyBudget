package com.ntg.core.designsystem.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.mybudget.core.designsystem.R

@Composable
fun SampleAddAccountButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHighest
            )
            .clickable {
                onClick.invoke()
            }
            .padding(vertical = 18.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.add_new_account),
            style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.outlineVariant)
        )
        Icon(
            modifier = Modifier.padding(start = 8.dp),
            painter = painterResource(id = BudgetIcons.Add),
            contentDescription = "add account"
        )
    }
}