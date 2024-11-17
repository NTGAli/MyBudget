package com.ntg.core.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.core.designsystem.theme.MyBudgetTheme

/**
 * - Items without an `Int` icon resource (null) will not show an icon.
 * show previews
 **/
@Composable
fun OptionSelector(
    items: List<Pair<String, Int?>>,
    selectedThemeIndex: Int,
    onOptionSelected: (Int) -> Unit
) {
    Column {
        items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOptionSelected(index) }
                    .padding(horizontal = 24.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    if (item.second != null) {
                        Icon(
                            painterResource(item.second!!),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Text(
                        text = item.first,
                        style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                if (index == selectedThemeIndex) {
                    Icon(
                        painterResource(BudgetIcons.check),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun OptionSelectorPreview() {
    val selectedOptions = remember { mutableIntStateOf(0) }
    val themeOptions = listOf(
        Pair("پیشفرض گوشی", BudgetIcons.default),
        Pair("روشن", BudgetIcons.sun),
        Pair("تاریک", BudgetIcons.moon),
    )

    MyBudgetTheme(darkTheme = false) {
        Surface() {
            OptionSelector(themeOptions,
                selectedThemeIndex = selectedOptions.intValue,
                onOptionSelected = { selectedOptions.intValue = it }
            )
        }
    }
}


@Preview
@Composable
fun OptionSelectorPreview1() {
    val selectedLanguage = remember { mutableIntStateOf(0) }
    val languageOptions = listOf(
        Pair("فارسی", null),
        Pair("English", null),
    )

    MyBudgetTheme(darkTheme = false) {
        Surface() {
            OptionSelector(
                languageOptions,
                selectedThemeIndex = selectedLanguage.intValue,
                onOptionSelected = { selectedLanguage.intValue = it }
            )
        }
    }
}