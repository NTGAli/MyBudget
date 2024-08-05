package com.ntg.core.designsystem.model

import androidx.compose.ui.graphics.painter.Painter

data class PopupItem(
    val id: Int,
    val icon: Painter,
    val title: String
)