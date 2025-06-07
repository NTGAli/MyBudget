package com.ntg.core.designsystem.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.core.model.AttachData
import com.ntg.core.mybudget.common.Constants

@Composable
fun TransactionItem(
    modifier: Modifier = Modifier,
    title: String,
    amount: String,
    date: String,
    type: Int,
    divider: Boolean = false,
    attached: List<AttachData?> = emptyList(),
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp)
    ) {
        TransactionHeader(
            modifier=modifier.padding(bottom = if (attached.filterNotNull().isNotEmpty()) 4.dp else 16.dp),
            title = title,
            amount = amount,
            date = date,
            type = type
        )

        if (attached.filterNotNull().isNotEmpty()) {
            AttachmentsRow(attached = attached)
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (divider) {
            TransactionDivider()
        }
    }
}

@Composable
private fun TransactionHeader(
    modifier:Modifier=Modifier,
    title: String,
    amount: String,
    date: String,
    type: Int
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CategoryIcon()

        TransactionDetails(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            title = title,
            date = date
        )

        AmountDisplay(amount = amount)

        TransactionTypeIndicator(type = type)
    }
}

@Composable
private fun CategoryIcon() {
    Icon(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        painter = painterResource(id = BudgetIcons.food),
        contentDescription = "Category icon"
    )
}

@Composable
private fun TransactionDetails(
    modifier: Modifier = Modifier,
    title: String,
    date: String
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = date,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
private fun AmountDisplay(amount: String) {
    Text(
        text = amount,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun TransactionTypeIndicator(type: Int) {
    val (colors, icon, contentDescription) = when (type) {
        Constants.BudgetType.INCOME -> Triple(
            listOf(Color(0xFF50AD98), Color(0xFF4DA8BD)),
            BudgetIcons.ArrowDown,
            "Income indicator"
        )
        else -> Triple(
            listOf(Color(0xFFD1666D), Color(0xFFB04F74)),
            BudgetIcons.ArrowUp,
            "Expense indicator"
        )
    }

    Icon(
        modifier = Modifier
            .padding(start = 8.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = colors,
                    start = Offset.Zero,
                    end = Offset(0f, Float.POSITIVE_INFINITY)
                ),
                shape = RoundedCornerShape(4.dp)
            ),
        painter = painterResource(id = icon),
        contentDescription = contentDescription,
        tint = Color.White
    )
}

@Composable
private fun AttachmentsRow(attached: List<AttachData?>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        attached.filterNotNull()
            .filter { it.type == Constants.AttachTyp.ATTACHED_IMAGE }
            .forEach { attachment ->
                AttachmentItem(attachment = attachment)
            }
    }
}

@Composable
private fun AttachmentItem(attachment: AttachData) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = 8.dp)
    ) {
        Text(
            text = "×${attachment.count}",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Icon(
            modifier = Modifier.padding(start = 4.dp),
            painter = painterResource(id = BudgetIcons.Image),
            contentDescription = "Attachment: ${attachment.count} images",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TransactionDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 56.dp),
        color = MaterialTheme.colorScheme.surfaceDim
    )
}

// Preview Composables
@Preview(name = "Income Transaction")
@Preview(name = "Income Transaction - Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TransactionItemIncomePreview() {
    MaterialTheme {
        Surface {
            TransactionItem(
                title = "Salary Payment",
                amount = "+$3,500.00",
                date = "Today, 2:30 PM",
                type = Constants.BudgetType.INCOME,
                divider = true,
                attached = listOf(
                    AttachData(
                        type = Constants.AttachTyp.ATTACHED_IMAGE,
                        count = 2
                    )
                )
            )
        }
    }
}

@Preview(name = "Expense Transaction")
@Preview(name = "Expense Transaction - Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TransactionItemExpensePreview() {
    MaterialTheme {
        Surface {
            TransactionItem(
                title = "Grocery Shopping",
                amount = "-$127.50",
                date = "Yesterday, 6:45 PM",
                type = Constants.BudgetType.EXPENSE,
                divider = false,
                attached = listOf(
                    AttachData(
                        type = Constants.AttachTyp.ATTACHED_IMAGE,
                        count = 3
                    )
                )
            )
        }
    }
}

@Preview(name = "Transaction Without Attachments")
@Composable
private fun TransactionItemNoAttachmentsPreview() {
    MaterialTheme {
        Surface {
            TransactionItem(
                title = "Coffee Shop",
                amount = "-$4.50",
                date = "2 hours ago",
                type = Constants.BudgetType.EXPENSE,
                divider = true,
                attached = emptyList()
            )
        }
    }
}

@Preview(name = "Transaction List Preview")
@Composable
private fun TransactionListPreview() {
    MaterialTheme {
        Surface {
            Column {
                TransactionItem(
                    title = "Freelance Work",
                    amount = "+$850.00",
                    date = "Today, 10:00 AM",
                    type = Constants.BudgetType.INCOME,
                    divider = true,
                    attached = listOf(
                        AttachData(
                            type = Constants.AttachTyp.ATTACHED_IMAGE,
                            count = 1
                        )
                    )
                )

                TransactionItem(
                    title = "Gas Station",
                    amount = "-$45.20",
                    date = "Today, 8:30 AM",
                    type = Constants.BudgetType.EXPENSE,
                    divider = true,
                    attached = emptyList()
                )

                TransactionItem(
                    title = "Restaurant",
                    amount = "-$32.75",
                    date = "Yesterday, 7:15 PM",
                    type = Constants.BudgetType.EXPENSE,
                    divider = false,
                    attached = listOf(
                        AttachData(
                            type = Constants.AttachTyp.ATTACHED_IMAGE,
                            count = 4
                        )
                    )
                )
            }
        }
    }
}