package com.ntg.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.ntg.core.designsystem.theme.BudgetIcons

@Composable
fun SampleItem(
    modifier: Modifier = Modifier,
    title: String,
    secondText: String? = null,
    imagePainter: Painter? = null,
    iconPainter: Painter? = null,
    secondIconPainter: Painter? = null,
    imageUrl: String? = null,
    setRadio: Boolean = false,
    isRadioCheck: Boolean = false,
    iconTint: Color = LocalContentColor.current,
    type: ButtonType = ButtonType.Neutral,
    onClick: () -> Unit,
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(interactionSource = remember { MutableInteractionSource() },
                indication = ripple(
                    color = if (type == ButtonType.Error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceDim
                ),
                onClick = {
                    onClick()
                }),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (setRadio){
            RadioCheck(
                modifier = Modifier.padding(start = 4.dp),
                isChecked = isRadioCheck, isCircle = true)
        }

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
        }else if (imageUrl != null){
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                placeholder = painterResource(BudgetIcons.bank),
                error = painterResource(BudgetIcons.bank),
                contentDescription = "Bank Logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(24.dp)
            )
        }

        Column (
            modifier = Modifier
                .padding(start = 8.dp)
                .padding(vertical = 16.dp)
                .weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(if(type == ButtonType.Error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
            )

            if (secondText != null) {
                Text(
                    text = secondText,
                    style = MaterialTheme.typography.labelMedium.copy(if(type == ButtonType.Error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline)
                )
            }
        }

        if (secondIconPainter != null) {
            IconButton(
                onClick = { onClick() }
            ) {
                Icon(
                    modifier = Modifier.padding(start = 8.dp),
                    painter = secondIconPainter,
                    tint = if (type == ButtonType.Error) MaterialTheme.colorScheme.error else iconTint,
                    contentDescription = null
                )
            }
        }
    }
}
