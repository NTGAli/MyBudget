package com.ntg.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

@Composable
fun SampleItem(
    modifier: Modifier = Modifier,
    title: String,
    secondText: String? = null,
    imagePainter: Painter? = null,
    iconPainter: Painter? = null,
    iconTint: Color = LocalContentColor.current,
    type: ButtonType = ButtonType.Neutral,
    onClick: () -> Unit,
) {

    Row(
        modifier = modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .clickable(interactionSource = remember { MutableInteractionSource() }, indication = rememberRipple(
            color = if(type == ButtonType.Error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceDim
          ), onClick = {
            onClick()
          }),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (iconPainter != null) {
            Icon(
                modifier = Modifier.padding(start = 8.dp),
                painter = iconPainter, tint = if(type == ButtonType.Error) MaterialTheme.colorScheme.error else iconTint, contentDescription = null
            )
        } else if (imagePainter != null) {
            Image(
                modifier = Modifier.padding(start = 8.dp),
                painter = imagePainter, contentDescription = null
            )
        }

        Text(
            modifier = Modifier
              .padding(start = 8.dp)
              .padding(vertical = 16.dp)
              .weight(1f),
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(if(type == ButtonType.Error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
        )

        if (secondText != null) {
            Text(
                modifier = Modifier
                  .padding(end = 8.dp)
                  .padding(vertical = 16.dp),
                text = secondText,
                style = MaterialTheme.typography.titleSmall.copy(if(type == ButtonType.Error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
            )
        }

    }

}
