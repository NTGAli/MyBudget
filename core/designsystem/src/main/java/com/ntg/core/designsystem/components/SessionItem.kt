package com.ntg.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.mybudget.core.designsystem.R

@Preview
@Composable
fun SessionItemPreview() {
    SessionItem(
        isMySelf = true,
        isMobile = true,
        deviceName = "Samsung A51",
        deviceIp = "127.0.0.1",
        deviceStatus = "2024-09-21 ساعت 13:30"
    ) {}
}

@Composable
fun SessionItem(
    isMySelf: Boolean,
    isMobile: Boolean,
    deviceName: String,
    deviceIp: String,
    deviceStatus: String,
    onClosClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(start = 24.dp, end = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .background(if (isMySelf) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(if (isMobile) BudgetIcons.mobile else BudgetIcons.desktop),
                    contentDescription = null,
                    tint = if (isMySelf) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = deviceName,
                    style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier.padding(bottom = 2.dp)
                )

                Text(
                    text = deviceIp,
                    style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.outlineVariant)
                )

                if (!isMySelf) {
                    Text(
                        text = deviceStatus,
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.outline)
                    )
                }
            }
        }

        if (isMySelf) {
            Box(
                Modifier
                    .padding(horizontal = 10.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = stringResource(R.string.MySession),
                    style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.primary)
                )
            }
        } else {
            IconButton(
                onClick = onClosClicked
            ) {
                Icon(
                    painter = painterResource(BudgetIcons.Close),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun SessionItemShimmer() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(start = 24.dp, end = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .flickerAnimation()
            )

            Column(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 83.dp, height = 20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .flickerAnimation()
                )

                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(width = 50.dp, height = 18.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .flickerAnimation()
                )

            }
        }

        Box(
            Modifier
                .width(29.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .flickerAnimation()
        )
    }
}