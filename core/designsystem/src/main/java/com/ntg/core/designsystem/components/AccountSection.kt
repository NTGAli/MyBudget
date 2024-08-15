package com.ntg.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.ntg.core.model.AccountWithSources
import com.ntg.core.model.SourceExpenditure
import com.ntg.core.model.SourceType
import com.ntg.core.mybudget.common.Constants
import com.ntg.core.mybudget.common.mask
import com.ntg.mybudget.core.designsystem.R

@Composable
fun AccountSection(
    modifier: Modifier = Modifier,
    account: AccountWithSources,
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
            title = account.accountName,
            isChecked = isCheck,
            canEdit = canEdit,
            isHeader = true
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
        account.sources.forEach { source ->

            val subTitle = if (source.sourceType is SourceType.BankCardSource) (source.sourceType as SourceType.BankCardSource).cardNumber.mask("#### #### #### ####")
            else ""

            val title = if (source.sourceType is SourceType.BankCardSource) "بانک ملی - 4106"
            else ""

            if (title.isNotEmpty()){
                Item(
                    painter = painterResource(id = BudgetIcons.BankLogo.icon(Constants.SourceExpenseIcons.MELLI)),
                    isImage = true,
                    title = title ?: Constants.SourceExpenseIcons.MELLI,
                    subtitle = subTitle,
                    isChecked = isCheck,
                    canEdit = canEdit
                ) {
                    if (canEdit){
                        IconButton(
                            onClick = {}) {
                            Icon(
                                painter = painterResource(
                                    id = BudgetIcons.Pen
                                ), contentDescription = "Edit"
                            )
                        }
                    }
                }
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
    isImage: Boolean = false,
    title: String,
    subtitle: String? = null,
    setStartRadio: Boolean = false,
    isChecked: MutableState<Boolean>,
    canEdit: Boolean,
    isHeader: Boolean = false,
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

        if (isImage){
            Image(
                modifier = modifier
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
                    .size(24.dp),
                painter = painter, contentDescription = "icon wallet"
            )
        }else{
            Icon(
                modifier = modifier
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp),
                painter = painter, contentDescription = "icon wallet"
            )
        }


        Column(
            modifier = Modifier
                .then(
                    if (canEdit && isHeader) Modifier else Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                .padding(start = 12.dp)
        ) {

            Text(
                modifier = Modifier.padding(top = 4.dp),
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