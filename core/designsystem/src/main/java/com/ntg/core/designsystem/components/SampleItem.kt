package com.ntg.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.core.designsystem.theme.MyBudgetTheme
import com.ntg.mybudget.core.designsystem.R
import kotlinx.coroutines.launch

@Preview
@Composable
fun SampleItemPreview() {
    MyBudgetTheme(darkTheme = true) {
        Surface {
            Column {

                // 1.
                SampleItem(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
                    title = stringResource(id = R.string.user_account),
                    iconPainter = painterResource(id = BudgetIcons.UserCircle),
                    iconTint = MaterialTheme.colorScheme.outline
                ) {}

                // 2.
                SampleItem(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    title = stringResource(id = R.string.user_account),
                    iconPainter = painterResource(id = BudgetIcons.UserCircle),
                    iconTint = MaterialTheme.colorScheme.outline,
                    hasDivider = true
                ) {}

                // 3.
                SampleItem(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    title = stringResource(id = R.string.user_account),
                    iconPainter = painterResource(id = BudgetIcons.Exit),
                    type = ButtonType.Error
                ) {}

                // 4.
                SampleItem(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    title = stringResource(id = R.string.user_account),
                    iconPainter = painterResource(id = BudgetIcons.UserCircle),
                    iconTint = MaterialTheme.colorScheme.outline,
                    subText = stringResource(id = R.string.user_account),
                    secondIconPainter = painterResource(id = BudgetIcons.directionLeft)
                ) {}

                // 5.
                SampleItem(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    title = stringResource(id = R.string.user_account),
                    iconPainter = painterResource(id = BudgetIcons.UserCircle),
                    iconTint = MaterialTheme.colorScheme.outline,
                    subText = stringResource(id = R.string.user_account),
                    secondIconPainter = painterResource(id = BudgetIcons.directionLeft),
                    hasDivider = true
                ) {}

                // 6.
                SampleItem(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 5.dp),
                    title = stringResource(id = R.string.user_account),
                    setSwitch = true,
                    isSwitchCheck = true,
                    hasDivider = true
                ) {}

                // 7.
                SampleItem(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 5.dp),
                    title = stringResource(id = R.string.user_account),
                ) {}

                // 8.
                SampleItem(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 5.dp),
                    title = stringResource(id = R.string.user_account),
                    secondText = stringResource(id = R.string.user_account)
                ) {}
            }
        }
    }
}


@Composable
fun SampleItem(
    modifier: Modifier = Modifier,
    title: String,
    subText: String? = null,
    secondText: String? = null,
    imagePainter: Painter? = null,
    iconPainter: Painter? = null,
    secondIconPainter: Painter? = null,
    imageUrl: String? = null,
    setRadio: Boolean = false,
    isRadioCheck: Boolean = false,
    iconTint: Color = LocalContentColor.current,
    type: ButtonType = ButtonType.Neutral,
    hasDivider: Boolean = false,
    setSwitch: Boolean = false,
    isSwitchCheck: Boolean = false,
    onClick: () -> Unit,
) {
    Column {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable(interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
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

                if (subText != null) {
                    Text(
                        text = subText,
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

            if (setSwitch) {
                Switch(
                    checked = isSwitchCheck,
                    onCheckedChange = { onClick.invoke() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.background,
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                    ),
                    modifier = Modifier
                        .scale(0.65f)
                        .rotate(180f)
                )
            }else if (secondText != null){
                Text(
                    text = secondText,
                    style = MaterialTheme.typography.bodyMedium.copy(if(type == ButtonType.Error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                )
            }
        }

        if (hasDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 24.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHighest
            )
        }
    }
}
