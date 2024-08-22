package com.ntg.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ntg.core.designsystem.theme.BudgetIcons

@Composable
fun BudgetButton(
    modifier: Modifier = Modifier,
    text: String,
    iconStart: Painter? = null,
    type: ButtonType = ButtonType.Primary,
    size: ButtonSize = ButtonSize.LG,
    style: ButtonStyle = ButtonStyle.Contained,
    enable: Boolean = true,
    loading: Boolean = false,
    radius: Dp = 8.dp,
    onClick: () -> Unit = {}

) {

    var innerPadding = PaddingValues()

    var textColor = Color.White
    var background = MaterialTheme.colorScheme.primary
    var borderColor = MaterialTheme.colorScheme.primary
    var loadingColor = MaterialTheme.colorScheme.onPrimary
    var textStyle = MaterialTheme.typography.titleSmall
    var progressSize = 24.dp
    var progressWidth = 3.dp

    when (type) {

        ButtonType.Primary -> {

            when (style) {
                ButtonStyle.Contained -> {
                    background = MaterialTheme.colorScheme.primary
                    borderColor = MaterialTheme.colorScheme.primary
                    textColor = MaterialTheme.colorScheme.onPrimary
                    loadingColor = MaterialTheme.colorScheme.background
                }
                ButtonStyle.Outline -> {
                    background = Color.Transparent
                    borderColor = MaterialTheme.colorScheme.primary
                    textColor = MaterialTheme.colorScheme.primary
                }
                ButtonStyle.TextOnly -> {
                    background = Color.Transparent
                    borderColor = Color.Transparent
                    textColor = MaterialTheme.colorScheme.primary
                }
            }

        }
        ButtonType.Success -> {
            when (style) {
                ButtonStyle.Contained -> {
                    background = MaterialTheme.colorScheme.tertiary
                    borderColor = MaterialTheme.colorScheme.tertiary
                    textColor = Color.White
                }
                ButtonStyle.Outline -> {
                    background = Color.Transparent
                    borderColor = MaterialTheme.colorScheme.tertiary
                    textColor = MaterialTheme.colorScheme.tertiary
                }
                ButtonStyle.TextOnly -> {
                    background = Color.Transparent
                    borderColor = Color.Transparent
                    textColor = MaterialTheme.colorScheme.tertiary
                }
            }
        }
        ButtonType.Secondary -> {
            when (style) {
                ButtonStyle.Contained -> {
                    background = MaterialTheme.colorScheme.surfaceVariant
                    borderColor = MaterialTheme.colorScheme.surfaceVariant
                    textColor = MaterialTheme.colorScheme.onSurfaceVariant
                }
                ButtonStyle.Outline -> {
                    background = Color.Transparent
                    borderColor = MaterialTheme.colorScheme.secondary
                    textColor = MaterialTheme.colorScheme.secondary
                }
                ButtonStyle.TextOnly -> {
                    background = Color.Transparent
                    borderColor = Color.Transparent
                    textColor = MaterialTheme.colorScheme.secondary
                }
            }
        }
        ButtonType.Error -> {
            when (style) {
                ButtonStyle.Contained -> {
                    background = MaterialTheme.colorScheme.error
                    borderColor = MaterialTheme.colorScheme.error
                    textColor = MaterialTheme.colorScheme.onError
                    loadingColor = MaterialTheme.colorScheme.onError
                }
                ButtonStyle.Outline -> {
                    background = Color.Transparent
                    borderColor = MaterialTheme.colorScheme.error
                    textColor = MaterialTheme.colorScheme.onErrorContainer
                }
                ButtonStyle.TextOnly -> {
                    background = Color.Transparent
                    borderColor = Color.Transparent
                    textColor = MaterialTheme.colorScheme.error
                }
            }
        }

    }


    when (size) {

        ButtonSize.XL -> {
            innerPadding = PaddingValues(vertical = 16.dp, horizontal = 24.dp)
            textStyle = MaterialTheme.typography.bodyLarge
            progressSize = 24.dp
            progressWidth = 3.dp
        }
        ButtonSize.LG -> {
            innerPadding = PaddingValues(vertical = 14.dp, horizontal = 20.dp)
            textStyle = MaterialTheme.typography.bodyMedium
            progressSize = 20.dp
            progressWidth = 3.dp
        }
        ButtonSize.MD -> {
            innerPadding = PaddingValues(vertical = 10.dp, horizontal = 16.dp)
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            progressSize = 16.dp
            progressWidth = 2.dp

        }
        ButtonSize.SM -> {
            innerPadding = PaddingValues(vertical = 7.dp, horizontal = 12.dp)
            textStyle = MaterialTheme.typography.labelLarge
            progressSize = 12.dp
            progressWidth = 2.dp
        }
        ButtonSize.XS -> {
            innerPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp)
            textStyle = MaterialTheme.typography.labelMedium
            progressSize = 8.dp
            progressWidth = 1.dp
        }

    }



    Box(
        modifier = modifier
            .wrapContentHeight()
            .clip(RoundedCornerShape(radius))

            .background(background)
            .clickable(
                enabled = enable,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    color = textColor.copy(alpha = 0.4f)
                )
            ) {
                onClick()
            }
            .border(width = 2.dp, color = borderColor, shape = RoundedCornerShape(radius))
        ,
        contentAlignment = Alignment.Center
    )
    {
        Row(modifier = Modifier
            .align(Alignment.Center)
            .padding(innerPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {


            if (iconStart != null){
                Icon(
                    modifier = Modifier.size(18.dp),
                    painter = iconStart, contentDescription = "icon start",tint = textColor)
                Spacer(modifier = Modifier.padding(start = 8.dp))
            }

            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = text,
                color = if (!loading) textColor else background,
                style = textStyle
            )

        }

        if (loading){
            CircularProgressIndicator(modifier = Modifier
                .progressSemantics()
                .size(progressSize)
                .align(Alignment.Center)
                , color = loadingColor, strokeWidth = progressWidth)
        }
    }



}



enum class ButtonType{
    Primary,
    Secondary,
    Success,
    Error
}

enum class ButtonSize{
    XL,
    LG,
    MD,
    SM,
    XS
}

enum class ButtonStyle{
    Contained,
    TextOnly,
    Outline
}
