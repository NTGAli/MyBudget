package com.ntg.core.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay


@Composable
fun AnimatedSnackbarHost(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    data: SnackData,
    durationMillis: Int = 3000
) {
    val currentSnackbarData = snackbarHostState.currentSnackbarData
    var isSnackbarVisible by remember { mutableStateOf(currentSnackbarData != null) }

    LaunchedEffect(currentSnackbarData) {
        if (currentSnackbarData != null) {
            isSnackbarVisible = true
            delay(durationMillis.toLong())
            isSnackbarVisible = false
            delay(300)
            currentSnackbarData.dismiss()
        }
    }

    AnimatedVisibility(
        visible = isSnackbarVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
        )
    ) {
        SnackbarHost(
            hostState = snackbarHostState,
            snackbar = { snackbarData ->
                CustomSnackbarContent(data)
            },
            modifier = modifier
        )
    }
}

@Composable
fun CustomSnackbarContent(data: SnackData) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                color = Color(0xFF323232),
                shape = RoundedCornerShape(8.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (data.raw != null){
            Lottie(modifier = Modifier
                .padding(start = 16.dp)
                .size(24.dp)
                , res = data.raw)
        }

        Text(
            text = data.message,
            style = MaterialTheme.typography.titleSmall.copy(Color.White),
            modifier = Modifier.padding(16.dp)
        )
    }
}

data class SnackData(
    val message: String,
    val action: String? = null,
    val raw: Int? = null
)