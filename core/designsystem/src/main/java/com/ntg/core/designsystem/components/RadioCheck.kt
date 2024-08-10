package com.ntg.core.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ntg.core.designsystem.theme.BudgetIcons


@Composable
fun RadioCheck(
    isChecked: Boolean,
    size: Float = 24f,
    radius: Int = 4,
    checkedColor: Color = MaterialTheme.colorScheme.primary,
    uncheckedColor: Color = MaterialTheme.colorScheme.background,
) {
    val checkboxColor: Color by animateColorAsState(if (isChecked) checkedColor else uncheckedColor,
        label = ""
    )
    val density = LocalDensity.current
    val duration = 200
    Box(
        modifier = Modifier
            .size(size.dp)
            .background(color = checkboxColor, shape = RoundedCornerShape(radius.dp))
            .border(width = 1.5.dp, color = if (isChecked) checkedColor else MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(radius.dp)),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.animation.AnimatedVisibility(
            visible = isChecked,
            enter = slideInHorizontally(animationSpec = tween(duration)) {
                with(density) { (size * -0.5).dp.roundToPx() }
            } + expandHorizontally(
                expandFrom = Alignment.Start,
                animationSpec = tween(duration)
            ),
            exit = fadeOut()
        ) {
            Icon(
                painterResource(id = BudgetIcons.Tick),
                contentDescription = null,
                tint = uncheckedColor
            )
        }
    }
}
