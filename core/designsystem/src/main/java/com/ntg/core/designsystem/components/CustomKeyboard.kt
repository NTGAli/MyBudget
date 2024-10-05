package com.ntg.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                KeyboardKey(
                    "⌫",
                    backgroundColor = MaterialTheme.colorScheme.errorContainer,
                    labelColor = MaterialTheme.colorScheme.error
                ) { onBackspace() }

                listOf("÷", "7", "8", "9").forEach { key ->
                    KeyboardKey(key) { onKeyPressed(key) }
                }

            }

            Row(modifier = Modifier.height(IntrinsicSize.Max)) {


                KeyboardKey(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f), label = "key"
                ) { onKeyPressed("key") }

                Column(
                    modifier = Modifier.weight(4f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("×", "4", "5", "6").forEach { key ->
                            KeyboardKey(key) { onKeyPressed(key) }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("-", "1", "2", "3").forEach { key ->
                            KeyboardKey(key) { onKeyPressed(key) }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("+", "0", ".").forEach { key ->
                            KeyboardKey(key) { onKeyPressed(key) }
                        }
                    }
                }
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
    label: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    labelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .weight(1f)
            .padding(horizontal = 2.dp)
            .background(backgroundColor, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = labelColor,
            textAlign = TextAlign.Center
        )
    }
}

