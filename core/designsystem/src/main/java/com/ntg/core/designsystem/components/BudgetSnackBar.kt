package com.ntg.core.designsystem.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BudgetSnackBar(
    modifier: Modifier = Modifier,
    data: SnackbarData
){
    Snackbar(
        actionColor = MaterialTheme.colorScheme.primary,
        snackbarData = data,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        contentColor = MaterialTheme.colorScheme.onBackground
    )
}