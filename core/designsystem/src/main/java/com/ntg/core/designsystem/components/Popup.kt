package com.ntg.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ntg.core.designsystem.model.PopupItem
import com.ntg.core.designsystem.model.PopupType
import com.ntg.core.designsystem.theme.BudgetIcons


@Composable
fun Popup(modifier: Modifier = Modifier, popupItems: List<PopupItem>, onClick: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(
            onClick = {
                expanded = true
                onClick.invoke(-1)
            }
        ) {
            Icon(
                painter = painterResource(id = BudgetIcons.more),
                contentDescription = "action appbar"
            )
        }


        MaterialTheme(
            shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))
        ) {

            DropdownMenu(
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerHighest),
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {

                popupItems.forEach {
                    DropdownMenuItem(
                        modifier = Modifier.width(200.dp),
                        onClick = {
                            onClick.invoke(it.id)
                            expanded = false
                        },
                        interactionSource = MutableInteractionSource(),
                        text = {
                            Text(
                                text = it.title, style = MaterialTheme.typography.labelLarge.copy(color = if (it.type == PopupType.Default) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.error)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = it.icon),
                                contentDescription = "popup item",
                                tint = if (it.type == PopupType.Default) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.error
                            )
                        })

                }


            }
        }


    }
}

