package com.ntg.core.designsystem.model


data class PopupItem(
    val id: Int,
    val icon: Int,
    val title: String,
    val type: PopupType = PopupType.Default
)

enum class PopupType {
    Default,
    Error
}