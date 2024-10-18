package com.ntg.core.designsystem.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ntg.core.designsystem.theme.BudgetIcons

@Composable
fun CustomKeyboard(
    onKeyPressed: (String) -> Unit,
    onConfirm: () -> Unit,
    onBackspace: () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Number Row 1
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                listOf("7", "8", "9", "÷").forEach { key ->
                    KeyboardKey(label = key) { onKeyPressed(key) }
                }

                KeyboardKey(
                    modifier = Modifier
                        .fillMaxHeight(),
                    icon = painterResource(id = BudgetIcons.backspace),
                    backgroundColor = MaterialTheme.colorScheme.errorContainer,
                    labelColor = MaterialTheme.colorScheme.error
                ) { onBackspace() }

            }

            Row(modifier = Modifier.height(IntrinsicSize.Max)) {

                Column(
                    modifier = Modifier.weight(4f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("4", "5", "6", "×").forEach { key ->
                            KeyboardKey(label = key) { onKeyPressed(key) }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("1", "2", "3", "-").forEach { key ->
                            KeyboardKey(label = key) { onKeyPressed(key) }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf(".", "0", "+").forEach { key ->
                            KeyboardKey(label = key) { onKeyPressed(key) }
                        }
                    }
                }

                KeyboardKey(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f), icon = painterResource(id = BudgetIcons.Tick),
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    labelColor = MaterialTheme.colorScheme.onPrimary,
                    iconSize = 32.dp
                ) { onConfirm() }
            }

            // Operations & Utility Row
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceEvenly
//        ) {
//            listOf("-", "+").forEach { key ->
//                KeyboardKey(key) { onKeyPressed(key) }
//            }
//
//            KeyboardKey("✓", MaterialTheme.colorScheme.secondary) { onConfirm() }    // Confirm key with custom color
//        }
        }
    }

}

@Composable
fun RowScope.KeyboardKey(
    modifier: Modifier = Modifier,
    label: String = "",
    icon: Painter? = null,
    iconSize: Dp = 24.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    labelColor: Color = MaterialTheme.colorScheme.outlineVariant,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    // Animate the scale value when pressed/released
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f, // Scale down slightly when pressed
        animationSpec = tween(durationMillis = 100), label = ""
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .weight(1f)
            .padding(horizontal = 2.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .background(backgroundColor, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                        onClick()
                    }
                )
            }
    ) {
        if (label.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(vertical = 4.dp),
                text = label,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = labelColor,
                textAlign = TextAlign.Center
            )
        } else if (icon != null) {
            Icon(
                modifier = Modifier.size(iconSize),
                painter = icon,
                contentDescription = "key icon",
                tint = labelColor
            )
        }
    }
}


