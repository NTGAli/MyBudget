package com.ntg.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.mybudget.core.designsystem.R

@Composable
fun AccountSelector(
    modifier: Modifier = Modifier,
    title: String,
    subTitle: String,
    isOpen: MutableState<Boolean>,
    onClick: () -> Unit
) {
    // Simple trigger for animation - just toggle to trigger restart
    val animationTrigger = remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                onClick.invoke()
                // Toggle the trigger to restart animation
                animationTrigger.value = !animationTrigger.value
            }
            .padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LottieComponent(
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(4.dp),
            res = R.raw.wallet,
            color = MaterialTheme.colorScheme.outline,
            isPlaying = animationTrigger,
            iterations = 1
        )

        Column(
            modifier = Modifier
                .padding(start = 8.dp, end = 16.dp)
                .padding(vertical = 4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            )
            Text(
                text = subTitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.outline
                )
            )
        }

        Icon(
            painter = painterResource(id = BudgetIcons.directionDown),
            contentDescription = "dropdown icon"
        )
    }
}