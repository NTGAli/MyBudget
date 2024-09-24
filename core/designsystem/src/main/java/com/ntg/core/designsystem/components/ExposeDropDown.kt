package com.ntg.core.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.core.model.res.WalletType
import com.ntg.mybudget.core.designsystem.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuSample(
    modifier: Modifier = Modifier,
    walletType: List<WalletType>,
    defaultText:String = stringResource(id = R.string.select_one),
    onItemSelect:(WalletType) -> Unit = {}
) {

    var expanded by remember { mutableStateOf(false) }
    val text = rememberSaveable{ mutableStateOf(defaultText) }
    var icon by rememberSaveable {
        mutableIntStateOf(BudgetIcons.directionUp)
    }

    icon = if (expanded) BudgetIcons.directionUp
    else BudgetIcons.directionDown

    ExposedDropdownMenuBox(
        modifier = modifier
            .fillMaxWidth(),
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        BudgetTextField(
            modifier = Modifier.menuAnchor(),
            text = text,
            readOnly = true,
            singleLine = true,
            label = stringResource(id = R.string.source_expenditure),
            trailingIcon = painterResource(id = icon)
        )


        ExposedDropdownMenu(
            modifier = Modifier
                .exposedDropdownSize(),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (walletType.isNotEmpty()){
                walletType.forEach { option ->
                    DropdownMenuItem(
                        modifier = Modifier,
                        text = { Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = option.faName.orEmpty(), style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.End)) },
                        onClick = {
                            text.value = option.faName.orEmpty()
                            expanded = false
                            onItemSelect(option)
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }else{
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier
                        .progressSemantics()
                        .size(24.dp),strokeWidth = 2.dp)
                }
            }
        }
    }
}