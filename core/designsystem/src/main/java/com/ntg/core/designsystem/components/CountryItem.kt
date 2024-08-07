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
import java.util.Objects

@Composable
fun CountryItem(
    modifier: Modifier = Modifier,
    country: Country,
    onClick:(Country) -> Unit
){

    val flag = getLanguageFlag(country.shortname ?: "") ?: ""
    Row(modifier = modifier
        .clickable {
            onClick.invoke(country)
        }
        .padding(vertical = 16.dp)) {

        Text(
            modifier = Modifier.padding(start = 32.dp),
            text = flag)

        Text(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f),
            text = country.name.orEmpty())


        Text(
            modifier = Modifier.padding(end = 32.dp),
            text = "+${country.code}",
            style = TextStyle(
                color = MaterialTheme.colorScheme.outline,
                fontSize = 14.sp
            )
        )
    }

}


fun getLanguageFlag(countryLetter: String): String? {
    if (countryLetter.length != 2 || countryLetter == "YL") return null
    when (countryLetter) {
        "FT" -> {
            return "\uD83C\uDFF4\u200D\u2620\uFE0F"
        }

        "XG" -> {
            return "\uD83D\uDEF0"
        }

        "XV" -> {
            return "\uD83C\uDF0D"
        }

        else -> {
            val base = 0x1F1A5
            val chars = countryLetter.toCharArray()
            val emoji = charArrayOf(
                CharacterCompat.highSurrogate(base),
                CharacterCompat.lowSurrogate(base + chars[0].code),
                CharacterCompat.highSurrogate(base),
                CharacterCompat.lowSurrogate(base + chars[1].code)
            )
            return String(emoji)
        }
    }
}


object CharacterCompat {
    const val MIN_HIGH_SURROGATE = '\uD800'
    const val MIN_SUPPLEMENTARY_CODE_POINT = 0x010000
    const val MIN_LOW_SURROGATE = '\uDC00'

    /**
     * Compat version of [Character.highSurrogate]
     */
    fun highSurrogate(codePoint: Int): Char {
        return ((codePoint ushr 10)
                + (MIN_HIGH_SURROGATE.code - (MIN_SUPPLEMENTARY_CODE_POINT ushr 10))).toChar()
    }

    /**
     * Compat version of [Character.lowSurrogate]
     */
    fun lowSurrogate(codePoint: Int): Char {
        return ((codePoint and 0x3ff) + MIN_LOW_SURROGATE.code).toChar()
    }
}

data class Country (
    var name: String? = null,
    var code: String? = null,
    var shortname: String? = null
)