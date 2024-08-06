package com.ntg.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
fun Tag(
    modifier: Modifier = Modifier,
    text: String,
    type: String,
    enableDismiss:Boolean = true,
    dismissClick: () -> Unit = {},
    onClick: () -> Unit,
) {

    Row(
        modifier = modifier.background(color = MaterialTheme.colorScheme.surfaceContainerHighest, shape = RoundedCornerShape(8.dp))

            .clip(RoundedCornerShape(8.dp))
            .clickable {
                onClick()
            }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (enableDismiss){
            Icon(
                modifier = Modifier
                    .padding(end = 4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable {
                        dismissClick()
                    },
                painter = painterResource(id = BudgetIcons.Close), contentDescription = "close tag")
        }
        Text(text = text, style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.inverseSurface))
    }

}


@Composable
fun ImageTag(){

}