package com.ntg.core.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ntg.core.model.res.Currency

@Composable
fun CurrencyItem(
    modifier: Modifier = Modifier,
    currency: Currency,
    onClick:(Currency) -> Unit
){

    val flag = getLanguageFlag(currency.countryAlpha2 ?: "") ?: ""
    Row(modifier = modifier
        .clickable {
            onClick.invoke(currency)
        }
        .padding(vertical = 16.dp)) {

        Text(
            modifier = Modifier.padding(start = 32.dp),
            text = flag)

        Text(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f),
            text = currency.faName.orEmpty())


        Text(
            modifier = Modifier.padding(end = 32.dp),
            text = "${currency.symbol} | ${currency.nativeName}",
            style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.primary)
        )
    }

}
