package com.ntg.core.designsystem.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ntg.core.designsystem.model.NavigationItem
import com.ntg.core.designsystem.theme.BudgetIcons

@Composable
fun BottomNavigation(
    modifier: Modifier = Modifier,
    items: List<NavigationItem>,
    txtButton: String? = null,
    expandButton: Boolean = false,
    isLoading: Boolean = false,
    initialSelectedItem: Int = 1,
    onCLick: (Int) -> Unit,
) {
    require(items.size >= 2) { "BottomNavigation requires at least 2 items" }

    val firstItem = items[0]
    val secondItem = items[1]

    var selectedItemId by remember { mutableStateOf(initialSelectedItem) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val animatedWidth by animateDpAsState(
        targetValue = if (expandButton) screenWidth else 100.dp,
        label = "button_width_animation"
    )

    val buttonPadding by animateDpAsState(
        targetValue = if (expandButton) 10.dp else 4.dp,
        label = "button_padding_animation"
    )

    Column(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .background(MaterialTheme.colorScheme.background)
    ) {
        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceDim)

        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationIcon(
                    item = firstItem,
                    isSelected = selectedItemId == firstItem.id,
                    isVisible = !expandButton,
                    onClick = {
                        if (selectedItemId != firstItem.id) {
                            selectedItemId = firstItem.id
                            onCLick(firstItem.id)
                        }
                    }
                )

                Spacer(
                    modifier = Modifier
                        .width(92.dp)
                        .background(Color.White)
                )

                NavigationIcon(
                    item = secondItem,
                    isSelected = selectedItemId == secondItem.id,
                    isVisible = !expandButton,
                    onClick = {
                        if (selectedItemId != secondItem.id) {
                            selectedItemId = secondItem.id
                            onCLick(secondItem.id)
                        }
                    }
                )
            }

            ActionButton(
                modifier = Modifier.align(Alignment.Center),
                expandButton = expandButton,
                animatedWidth = animatedWidth,
                padding = buttonPadding,
                txtButton = txtButton,
                isLoading = isLoading,
                onClick = { onCLick(-1) }
            )
        }
    }
}

@Composable
private fun RowScope.NavigationIcon(
    item: NavigationItem,
    isSelected: Boolean,
    isVisible: Boolean,
    onClick: () -> Unit
) {
    Icon(
        modifier = Modifier
            .padding(start = 4.dp)
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .then(if (isVisible) Modifier.weight(1f) else Modifier.size(0.dp))
            .padding(vertical = 16.dp),
        painter = if (isSelected) item.selectedPainter else item.painter,
        contentDescription = item.title,
    )
}

@Composable
private fun ActionButton(
    modifier: Modifier = Modifier,
    expandButton: Boolean,
    animatedWidth: androidx.compose.ui.unit.Dp,
    padding: androidx.compose.ui.unit.Dp,
    txtButton: String?,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .then(if (expandButton) Modifier.width(animatedWidth) else Modifier)
            .padding(horizontal = 24.dp, vertical = padding)
            .clip(RoundedCornerShape(8.dp))
            .background(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primary
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = padding)
    ) {
        if (txtButton != null) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = txtButton,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isLoading) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary
                )
            )

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .progressSemantics()
                        .size(16.dp)
                        .align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onBackground,
                    strokeWidth = 2.dp
                )
            }
        } else {
            Icon(
                modifier = Modifier.align(Alignment.Center),
                painter = painterResource(id = BudgetIcons.Transaction),
                contentDescription = "Transaction",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}