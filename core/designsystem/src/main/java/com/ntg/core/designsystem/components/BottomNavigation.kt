package com.ntg.core.designsystem.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ntg.core.designsystem.model.NavigationItem
import com.ntg.core.designsystem.theme.BudgetIcons

@Composable
fun BottomNavigation(
    modifier: Modifier = Modifier,
    items: List<NavigationItem>,
    txtButton: String? = null,
    expandButton: Boolean = false,
    onCLick: (Int) -> Unit,
) {

    val firstItem = items[0]
    val secondItem = items[1]

    var itemSelected by remember {
        mutableIntStateOf(1)
    }

//    var expandButton by remember {
//        mutableStateOf(false)
//    }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val animatedWidth by animateDpAsState(targetValue = if (expandButton) screenWidth else 100.dp,
        label = ""
    )

    val padding by animateDpAsState(targetValue = if (expandButton) 12.dp else 4.dp,
        label = ""
    )

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceDim)
        Box(modifier = Modifier
            .fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            if (itemSelected != 1) {
                                onCLick.invoke(firstItem.id)
                                itemSelected = 1
                            }
                        }
                        .then(
                            if (!expandButton)
                                Modifier.weight(1f)
                            else Modifier.size(0.dp)
                        )
                        .padding(vertical = 16.dp),
                    painter =
                    if (itemSelected == 1) firstItem.selectedPainter else firstItem.painter
                    , contentDescription = firstItem.title,
                )


                Spacer(
                    modifier = Modifier
                        .width(92.dp)
                        .background(Color.White)
                )

                Icon(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            if (itemSelected != 2) {
                                onCLick.invoke(secondItem.id)
                                itemSelected = 2
                            }
                        }
                        .then(
                            if (!expandButton)
                                Modifier.weight(1f)
                            else Modifier.size(0.dp)
                        )
                        .padding(vertical = 16.dp),
                    painter =
                    if (itemSelected == 2) secondItem.selectedPainter else secondItem.painter
                    , contentDescription = secondItem.title,
                )
            }


            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .then(if (expandButton) Modifier.width(animatedWidth) else Modifier)
                    .padding(horizontal = 24.dp, vertical = padding)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    .clickable {
                        onCLick.invoke(-1)
                    }

                    .padding(horizontal = 24.dp, vertical = padding)
            ) {
                if (txtButton != null){
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = txtButton, style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onPrimary))
                }else{
                    Icon(
                        modifier = Modifier.align(Alignment.Center),
                        painter = painterResource(id = BudgetIcons.Transaction),
                        contentDescription = "Transaction",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

        }

    }
}
