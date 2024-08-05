package com.ntg.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CardReport(
    modifier: Modifier = Modifier,
    title: String,
    subTitle: String,
    out: String,
    inValue: String
){

    Column(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.surfaceContainerLow, shape = RoundedCornerShape(16.dp))
            .border(width = 1.dp, color = MaterialTheme.colorScheme.surfaceContainerHighest, shape = RoundedCornerShape(16.dp))
    ) {
        Text(
            modifier = Modifier.padding(top = 16.dp, start = 16.dp),
            text = title, style= MaterialTheme.typography.titleMedium.copy(MaterialTheme.colorScheme.outlineVariant))
        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = subTitle, style= MaterialTheme.typography.labelMedium.copy(MaterialTheme.colorScheme.outline))

        AmountReport(
            modifier = Modifier.padding(horizontal = 16.dp).padding(top = 8.dp, bottom = 16.dp),
            outcome = out, income = inValue)
    }

}