package com.ntg.core.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenBottomSheet(
    showSheet: MutableState<Boolean>,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    appbar:@Composable () -> Unit,
    content:@Composable () -> Unit,
) {

    AnimatedVisibility(
        visible = showSheet.value,
        enter = slideInVertically(
            initialOffsetY = { it }, // Start from the bottom of the screen
            animationSpec = tween(durationMillis = 300)
        ),
        exit = slideOutVertically(
            targetOffsetY = { it }, // Slide to the bottom of the screen
            animationSpec = tween(durationMillis = 300)
        ),
    ) {

        Scaffold(
            topBar = {
                Column {
                    appbar()
                    if ((scrollBehavior?.state?.contentOffset ?: 0f) < -25f) {
                        HorizontalDivider(Modifier.height(1.dp), color = MaterialTheme.colorScheme.surfaceContainerHighest)
                    }
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.background)
                    .pointerInput(Unit) {}
            ) {
                content()
            }
        }

    }
}