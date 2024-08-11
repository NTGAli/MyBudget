package com.ntg.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ntg.core.designsystem.model.TextDividerType

@Composable
fun TextDivider(
    modifier: Modifier = Modifier,
    title: String,
    type: TextDividerType = TextDividerType.START
){

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        HorizontalDivider(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surfaceDim)

        val textModifier = if (type == TextDividerType.START) Modifier.align(Alignment.CenterStart) else Modifier.align(Alignment.Center)

        Text(
            modifier = textModifier.padding(horizontal = 16.dp)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 8.dp),
            text = title, style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.onBackground))
    }

}
