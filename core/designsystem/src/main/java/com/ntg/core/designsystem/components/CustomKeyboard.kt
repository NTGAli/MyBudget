package com.ntg.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomKeyboard(
    onKeyPressed: (String) -> Unit,
    onConfirm: () -> Unit,
    onBackspace: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Number Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("1", "2", "3").forEach { key ->
                KeyboardKey(key) { onKeyPressed(key) }
            }
        }
        // Number Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("4", "5", "6").forEach { key ->
                KeyboardKey(key) { onKeyPressed(key) }
            }
        }
        // Number Row 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("7", "8", "9").forEach { key ->
                KeyboardKey(key) { onKeyPressed(key) }
            }
        }
        // Symbols Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("*", "0", "/").forEach { key ->
                KeyboardKey(key) { onKeyPressed(key) }
            }
        }
        // Operations & Utility Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("-", "+").forEach { key ->
                KeyboardKey(key) { onKeyPressed(key) }
            }
            KeyboardKey("⌫", MaterialTheme.colorScheme.error) { onBackspace() }  // Backspace key with custom color
            KeyboardKey("✓", MaterialTheme.colorScheme.secondary) { onConfirm() }    // Confirm key with custom color
        }
    }
}

@Composable
fun KeyboardKey(
    label: String,
    backgroundColor: Color = Color(0xFFB0C4DE),
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(70.dp)
            .background(backgroundColor, shape = RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

