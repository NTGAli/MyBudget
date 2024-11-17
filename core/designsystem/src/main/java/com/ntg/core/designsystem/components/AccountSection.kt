package com.ntg.core.designsystem.components

import androidx.compose.animation.animateContentSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.ntg.core.designsystem.model.PopupItem
import com.ntg.core.designsystem.model.PopupType
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.core.model.AccountWithSources
import com.ntg.core.model.SourceType
import com.ntg.core.mybudget.common.getCardDetailsFromAssets
import com.ntg.core.mybudget.common.mask
import com.ntg.mybudget.core.designsystem.R

@Composable
fun AccountSection(
    modifier: Modifier = Modifier,
    account: AccountWithSources,
    isCheckBox: Boolean,
    isAccountSelected: Boolean = false,
    selectedSources: MutableList<Int> = mutableListOf(),
    insertNewItem: () -> Unit = {},
    onSourceEdit: (Int) -> Unit = {},
    accountEndIconClick: (Int) -> Unit = {},
    deleteAccount: (Int) -> Unit = {},
    deleteSource: (Int) -> Unit = {},
    onAccountSelect: (Int) -> Unit = {},
    onSourceSelect: (Int) -> Unit = {}
) {
    var expaned by remember { mutableStateOf(isAccountSelected) }
    val context = LocalContext.current

    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .animateContentSize()
    ) {

        Item(
            type = -1,
            title = account.accountName,
            isChecked = isAccountSelected,
            isCheckBox = isCheckBox,
            isHeader = true,
            onCLick = {
                expaned = true
                onAccountSelect.invoke(account.accountId)
            }
        ) {
            Popup(popupItems = listOf(
                PopupItem(1, BudgetIcons.Pen, stringResource(id = R.string.edit)),
                PopupItem(2, BudgetIcons.trash, stringResource(id = R.string.delete), type = PopupType.Error))){
                if (it == 1) {
                    accountEndIconClick(account.accountId)
                } else if (it == 2) {
                    deleteAccount(account.accountId)
                }
            }

            Spacer(Modifier.weight(1f))

            IconButton(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(32.dp),
                onClick = { expaned = !expaned }
            ) {
                Icon(
                    painter = painterResource(id = BudgetIcons.directionDown),
                    contentDescription = "wallet icon"
                )
            }
        }

        if (expaned) {
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceDim)
            account.sources.forEach { source ->

                val subTitle = if (source?.data is SourceType.BankCard) (source.data as SourceType.BankCard).number.mask("#### #### #### ####")
                else ""

                val title = if (source?.data is SourceType.BankCard){
                    val bandData = getCardDetailsFromAssets(context,
                        (source.data as SourceType.BankCard).number)
                    if (bandData != null){
                        "${bandData.bank_title} - ${
                            (source.data as SourceType.BankCard).number.takeLast(4)
                        }"
                    }else stringResource(id = R.string.bank_card)
                }
                else ""

                if (title.isNotEmpty()){
                    Item(
                        isImage = true,
                        title = title,
                        subtitle = subTitle,
                        isCheckBox = isCheckBox,
                        type = source?.type ?: 0,
                        isChecked = selectedSources.contains(source?.id ?: -1),
                        onCLick = { onSourceSelect.invoke(source?.id ?: -1) }
                    ) {
                        Popup(popupItems = listOf(
                            PopupItem(1, BudgetIcons.Pen, stringResource(id = R.string.edit)),
                            PopupItem(2, BudgetIcons.trash, stringResource(id = R.string.delete), type = PopupType.Error))){
                            if (it == 1) {
                                onSourceEdit(source?.id ?: -1)
                            }else if (it == 2){
                                deleteSource(source?.id ?: -1)
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

}


@Composable
private fun Item(
    modifier: Modifier = Modifier,
    type: Int,
    isImage: Boolean = false,
    title: String,
    subtitle: String? = null,
    isChecked: Boolean = false,
    isCheckBox: Boolean,
    isHeader: Boolean = false,
    onCLick: () -> Unit,
    endItem: @Composable () -> Unit,
) {

    val context = LocalContext.current

    Row(
        modifier
            .clickable { onCLick.invoke() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (isHeader && isCheckBox) {
            RadioCheck(
                modifier = Modifier.padding(horizontal = 16.dp),
                isChecked = isChecked, isCircle = true
            )
        } else if (isCheckBox) {
            RadioCheck(
                modifier = Modifier.padding(horizontal = 16.dp),
                isChecked = isChecked, isCircle = false
            )
        }else{
            Spacer(Modifier.padding(8.dp))
        }

        if (isImage){
            when(type){

                1 -> {
                    val bankData = getCardDetailsFromAssets(context, subtitle.orEmpty().replace(" ", ""))
                    val onSurface = MaterialTheme.colorScheme.onSurface
                    var defaultTint by remember {
                        mutableStateOf<ColorFilter?>(ColorFilter.tint(onSurface))
                    }
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(bankData?.bank_logo)
                            .crossfade(true)
                            .decoderFactory(SvgDecoder.Factory())
                            .build(),
                        placeholder = painterResource(BudgetIcons.bank),
                        error = painterResource(BudgetIcons.card),
                        onSuccess = {defaultTint = null},
                        contentDescription = "Bank Logo",
                        contentScale = ContentScale.Crop,
                        colorFilter = defaultTint,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(4.dp)
                            .size(24.dp)

                    )
                }

                else -> {
                    Image(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                            .size(24.dp),
                        painter = painterResource(id = BudgetIcons.card), contentDescription = "icon wallet",
                    )
                }

            }

        }else{
            // This is for Account
            Icon(
                modifier = modifier
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp),
                painter = painterResource(id = BudgetIcons.wallet), contentDescription = "icon wallet"
            )
        }


        Column(
            modifier = Modifier
                .then(if (!isHeader) Modifier.weight(1f) else Modifier)
                .padding(start = 12.dp, end = 8.dp)
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
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.outline).copy(textDirection = TextDirection.Ltr)
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