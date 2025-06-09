package com.ntg.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.core.mybudget.common.toPersianMonthlyString
import com.ntg.mybudget.core.designsystem.R

/**
 * A card component that displays financial report information with title, amounts, and optional info.
 *
 * @param modifier Modifier to be applied to the card
 * @param title Main title text displayed at the top
 * @param subTitle Subtitle text displayed below the title
 * @param out Outcome/expense amount string
 * @param inValue Income amount string
 * @param showBottomInfo Whether to show the bottom info section with timestamp
 */
@Composable
fun CardReport(
    modifier: Modifier = Modifier,
    title: String,
    subTitle: String,
    out: String,
    inValue: String,
    showBottomInfo: Boolean = true
) {
    val timestampInfo = remember {
        System.currentTimeMillis().toPersianMonthlyString()
    }

    val cardShape = RoundedCornerShape(16.dp)
    val contentPadding = 16.dp

    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = cardShape
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                shape = cardShape
            )
            .semantics {
                contentDescription = "Financial report card showing $title with income and expenses"
            },
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HeaderSection(
            title = title,
            subTitle = subTitle,
            contentPadding = contentPadding
        )

        AmountReport(
            modifier = Modifier.padding(horizontal = contentPadding),
            outcome = out,
            income = inValue
        )

        if (showBottomInfo) {
            BottomInfoSection(
                timestampInfo = timestampInfo,
                contentPadding = contentPadding
            )
        }
    }
}

@Composable
private fun HeaderSection(
    title: String,
    subTitle: String,
    contentPadding: Dp
) {
    Column(
        modifier = Modifier.padding(
            top = contentPadding,
            start = contentPadding,
            end = contentPadding
        )
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.outlineVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = subTitle,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun BottomInfoSection(
    timestampInfo: String,
    contentPadding: Dp
) {
    Row(
        modifier = Modifier
            .padding(
                horizontal = contentPadding + 8.dp
            ).padding(bottom = contentPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            modifier = Modifier.size(12.dp),
            painter = painterResource(id = BudgetIcons.info),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline
        )

        Text(
            text = stringResource(R.string.card_info_format, timestampInfo),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}



@Preview(name = "Multiple Cards", heightDp = 600)
@Composable
private fun MultipleCardReportsPreview() {
    MaterialTheme {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CardReport(
                    modifier = Modifier.fillMaxWidth(),
                    title = "۲,۵۰۰,۰۰۰ ت",
                    subTitle = "موجودی کل",
                    out = "۸۵۰,۰۰۰",
                    inValue = "۱,۲۰۰,۰۰۰"
                )

                CardReport(
                    modifier = Modifier.fillMaxWidth(),
                    title = "۵۰۰,۰۰۰ ت",
                    subTitle = "موجودی نقدی",
                    out = "۲۰۰,۰۰۰",
                    inValue = "۳۰۰,۰۰۰",
                    showBottomInfo = false
                )

                CardReport(
                    modifier = Modifier.fillMaxWidth(),
                    title = "۱,۸۰۰,۰۰۰ ت",
                    subTitle = "موجودی بانکی",
                    out = "۶۵۰,۰۰۰",
                    inValue = "۹۰۰,۰۰۰"
                )
            }
    }
}