package com.ntg.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun SampleChip(selected: MutableState<Boolean>, text: String, icon: Int, enableColor: Color) {

    Row(
        modifier = Modifier
            .background(
                if (selected.value) enableColor else MaterialTheme.colorScheme.surfaceContainerHighest,
                RoundedCornerShape(8.dp)
            )
            .clickable { selected.value = !selected.value }
            .padding(horizontal = 8.dp, vertical = 2.dp)
        ,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            painter = painterResource(icon),
            contentDescription = "filter chip",
            modifier = Modifier
                .padding(end = 4.dp)
                .size(12.dp),
            tint = if (selected.value) Color.White else MaterialTheme.colorScheme.inverseSurface
        )

        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(color = if (selected.value) Color.White else MaterialTheme.colorScheme.inverseSurface)
        )
    }
}