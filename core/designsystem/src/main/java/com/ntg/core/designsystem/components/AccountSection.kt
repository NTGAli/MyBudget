package com.ntg.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.core.model.Account
import com.ntg.core.model.SourceExpenditure
import com.ntg.mybudget.core.designsystem.R

@Composable
fun AccountSection(
    modifier: Modifier = Modifier,
    account: Account,
    items: List<SourceExpenditure> = listOf(),
    canEdit: Boolean,
    insertNewItem: () -> Unit = {},
    onItemClick: (SourceExpenditure) -> Unit = {}
) {

    val isCheck = remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
    ) {

        Item(
            painter = painterResource(id = BudgetIcons.wallet),
            title = account.name,
            isChecked = isCheck,
            canEdit = canEdit
        ) {
            IconButton(onClick = {

            }) {
                Icon(
                    painter = painterResource(
                        id =
                        if (canEdit) BudgetIcons.Pen else
                            BudgetIcons.ArrowDown
                    ), contentDescription = "Arrow down"
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceDim)
        items.forEach {
            Item(
                painter = painterResource(id = BudgetIcons.BankLogo.icon(it.icon)),
                title = it.name,
                isChecked = isCheck,
                canEdit = true
            ) {

            }
        }

        InsetItem{
            insertNewItem()
        }
    }

}


@Composable
private fun Item(
    modifier: Modifier = Modifier,
    painter: Painter,
    title: String,
    subtitle: String? = null,
    setStartRadio: Boolean = false,
    isChecked: MutableState<Boolean>,
    canEdit: Boolean,
    endItem: @Composable () -> Unit
) {

    Row(
        modifier
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (setStartRadio) {
            RadioCheck(
                modifier = Modifier.padding(start = 8.dp),
                isChecked = isChecked.value, radius = 12
            )
        } else {
            Spacer(modifier = Modifier.padding(start = 16.dp))
        }

        Icon(
            modifier = modifier
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp),
            painter = painter, contentDescription = "icon wallet"
        )

        Column(
            modifier = Modifier
                .then(
                    if (canEdit) Modifier else Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                .padding(start = 12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
            if (subtitle != null) {
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.outline)
                )
            }
        }
        endItem()
    }
}


@Composable
private fun InsetItem(
    onCLick: () -> Unit = {}
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onCLick()
            }
            .padding(16.dp)
            ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp))
                .padding(6.dp),
            painter = painterResource(id = BudgetIcons.Add),
            contentDescription = "Add wallet icon",
            tint = MaterialTheme.colorScheme.onPrimary
        )

        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = stringResource(id = R.string.new_source_expenditure),
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
    }

}