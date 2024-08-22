package com.ntg.core.designsystem.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.mybudget.core.designsystem.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuSample(
    modifier: Modifier = Modifier,
    defaultText:String = stringResource(id = R.string.select_one),
    onItemSelect:(String) -> Unit = {}
) {
    val options = listOf(stringResource(id = R.string.bank_card), stringResource(id = R.string.cash), stringResource(
        id = R.string.crypto
    ))
    var expanded by remember { mutableStateOf(false) }
    val text = remember { mutableStateOf(defaultText) }
    var icon by remember {
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
            options.forEach { option ->
                DropdownMenuItem(
                    modifier = Modifier,
                    text = { Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = option, style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.End)) },
                    onClick = {
                        text.value = option
                        expanded = false
                        onItemSelect(option)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}