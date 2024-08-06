package com.ntg.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

@Composable
fun SampleItem(
  modifier: Modifier = Modifier,
  title: String,
  secondText: String? = null,
  imagePainter : Painter? = null,
  iconPainter: Painter? = null,
  onClick:() -> Unit,
){

  Row(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .clickable {
        onClick()
      },
    verticalAlignment = Alignment.CenterVertically
  ) {

    if (iconPainter != null){
      Icon(
        modifier = Modifier.padding(start = 8.dp),
        painter = iconPainter, contentDescription = null)
    }else if (imagePainter != null){
      Image(
        modifier = Modifier.padding(start = 8.dp),
        painter = imagePainter, contentDescription = null)
    }

    Text(
      modifier = Modifier.padding(start = 4.dp).padding(vertical = 16.dp).weight(1f),
      text = title, style = MaterialTheme.typography.titleMedium.copy(MaterialTheme.colorScheme.onSurface))

    if (secondText != null){
      Text(
        modifier = Modifier.padding(end = 4.dp).padding(vertical = 16.dp),
        text = secondText, style = MaterialTheme.typography.titleSmall.copy(MaterialTheme.colorScheme.onSurface))
    }

  }

}
