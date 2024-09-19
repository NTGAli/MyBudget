package com.ntg.core.designsystem.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.ntg.core.designsystem.model.SwitchItem
import com.ntg.core.designsystem.model.SwitchTextColor
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SwitchText(
    modifier: Modifier = Modifier,
    items: List<SwitchItem>,
    selected: (Int) -> Unit,
) {
    val scope = rememberCoroutineScope()

    var width by remember { mutableIntStateOf(0) }

    var itemSelected by rememberSaveable { mutableIntStateOf(0) }

    val offsetSelected = remember { Animatable(Offset.Zero, Offset.VectorConverter) }

    val layoutDirection = LocalLayoutDirection.current

    // Set initial offset based on layout direction and selected item
    LaunchedEffect(width, itemSelected, layoutDirection) {
        val initialOffset = if (layoutDirection == LayoutDirection.Rtl) {
            (items.size - 1 - itemSelected) * width.toFloat() // Reverse the offset for RTL
        } else {
            itemSelected * width.toFloat() // Normal for LTR
        }
        offsetSelected.animateTo(Offset(x = initialOffset, y = 0f))
    }

    Box(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .onGloballyPositioned {
                width = it.size.width / items.size
            }
            .background(
                shape = RoundedCornerShape(ROUND_CORNER.dp),
                color = MaterialTheme.colorScheme.surfaceContainer,
            )
            .wrapContentHeight(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(with(LocalDensity.current) { width.toDp() })
                .padding(8.dp)
                .offset(
                    x = with(LocalDensity.current) {
                        // Adjust offset based on layout direction
                        val currentOffsetX = offsetSelected.value.x
                        if (layoutDirection == LayoutDirection.Rtl) {
                            // Reverse the offset for RTL mode
                            (width * (items.size - 1) - currentOffsetX).toDp()
                        } else {
                            currentOffsetX.toDp()
                        }
                    }
                )
                .background(
                    shape = RoundedCornerShape(SELECTOR_CORNER.dp),
                    color = items[itemSelected].backColor,
                ),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (layoutDirection == LayoutDirection.Rtl) {
                Arrangement.End // In RTL, layout from the end (right)
            } else {
                Arrangement.Start // In LTR, layout from the start (left)
            }
        ) {
            items.forEachIndexed { index, switchItem ->
                ItemSelector(
                    text = switchItem.title,
                    color = switchItem.tint,
                    isSelected = itemSelected == index,
                ) {
                    scope.launch {
                        // Calculate the target offset for animation
                        val targetOffsetX = if (layoutDirection == LayoutDirection.Rtl) {
                            // In RTL, reverse the index for the correct offset
                            (items.size - 1 - index) * width.toFloat()
                        } else {
                            index * width.toFloat()
                        }
                        offsetSelected.animateTo(Offset(x = targetOffsetX, y = offsetSelected.value.y))
                    }
                    itemSelected = index
                    selected(index)
                }
            }
        }
    }
}



@Composable
private fun RowScope.ItemSelector(
    text: String,
    color: Color,
    isSelected: Boolean,
    onClick: (Float) -> Unit,
) {

    var itemOffset by remember {
        mutableFloatStateOf(0f)
    }

    Text(
        modifier = Modifier
            .onGloballyPositioned {
                itemOffset = it.positionInParent().x
            }
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = remember {
                    MutableInteractionSource()
                },
                indication = null,
                onClick = { onClick.invoke(itemOffset) },
            )
            .weight(1f)
            .padding(vertical = 16.dp),
        text = text,
        style = MaterialTheme.typography.labelMedium.copy(
            color = if (isSelected) color else MaterialTheme.colorScheme.outline,
        ),
        textAlign = TextAlign.Center,
    )
}


@Composable
private fun defaultSwitchTextColor(): SwitchTextColor {
    return SwitchTextColor(
        defaultColor = MaterialTheme.colorScheme.secondary,
        firstColor = MaterialTheme.colorScheme.error,
        secondColor = MaterialTheme.colorScheme.tertiary,
        firstBackColor = MaterialTheme.colorScheme.errorContainer,
        secondBackColor = MaterialTheme.colorScheme.tertiaryContainer,
        borderColor = MaterialTheme.colorScheme.surfaceVariant,
    )
}


//settings
private const val ROUND_CORNER = 12
private const val SELECTOR_CORNER = 8
private const val WIDTH_SHAPE = 2
