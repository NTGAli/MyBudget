package com.ntg.core.designsystem.model

import androidx.compose.ui.graphics.painter.Painter

data class NavigationItem(
    val id: Int,
    val title: String,
    val painter: Painter,
    val selectedPainter: Painter,
    var isSelected: Boolean,
)
