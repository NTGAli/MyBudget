package com.ntg.features.home

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ntg.core.designsystem.components.AppBar
import com.ntg.core.designsystem.components.BudgetButton
import com.ntg.core.designsystem.components.ButtonSize
import com.ntg.core.designsystem.components.ButtonStyle
import com.ntg.core.designsystem.components.ButtonType
import com.ntg.core.designsystem.components.ImagesPreview
import com.ntg.core.designsystem.components.SampleItem
import com.ntg.core.designsystem.components.Tag
import com.ntg.core.designsystem.components.TextDivider
import com.ntg.core.designsystem.model.PopupItem
import com.ntg.core.designsystem.model.PopupType
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.core.model.Contact
import com.ntg.core.model.SourceType
import com.ntg.core.model.Transaction
import com.ntg.core.mybudget.common.Constants
import com.ntg.core.mybudget.common.formatInput
import com.ntg.core.mybudget.common.getCardDetailsFromAssets
import com.ntg.core.mybudget.common.logd
import com.ntg.core.mybudget.common.orDefault
import com.ntg.core.mybudget.common.toPersianDate
import com.ntg.mybudget.core.designsystem.R


@Composable
fun DetailsRoute(
    homeViewModel: HomeViewModel = hiltViewModel(),
    tId: Int?,
    onBack: () -> Unit,
    navToEdit: (Int) -> Unit,
    navToImageFull: (String) -> Unit,
) {


    if (tId != null) {
        val transaction =
            homeViewModel.transactionById(tId).collectAsStateWithLifecycle(initialValue = null)

        if (transaction.value != null) {
            DetailsScreen(transaction, onBack,
                navToImageFull = navToImageFull,
                navToEdit = {
                navToEdit(tId)
            },
                delete = {
                    homeViewModel.deleteTransaction(it)
                    onBack()
                })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(transaction: State<Transaction?>, onBack: () -> Unit, navToImageFull: (String) -> Unit, delete: (Int) -> Unit, navToEdit: () -> Unit) {
    val context = LocalContext.current
    val selectedContact = remember { mutableStateOf<Contact?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val title = if (transaction.value?.walletData is SourceType.BankCard) {
        val bandData = getCardDetailsFromAssets(
            context,
            (transaction.value?.walletData as SourceType.BankCard).number
        )
        if (bandData != null) {
            "${bandData.bank_title} - ${
                (transaction.value?.walletData as SourceType.BankCard).number.takeLast(4)
            }"
        } else stringResource(id = R.string.bank_card)
    } else ""

    Scaffold(
        topBar = {
            AppBar(
                navigationOnClick = {
                    onBack()
                },
                title = stringResource(
                    R.string.transaction_fortmat,
                    transaction.value?.date?.toPersianDate().orDefault()
                ),
                popupItems = listOf(
                    PopupItem(0, BudgetIcons.edit, stringResource(R.string.edit)),
                    PopupItem(
                        1,
                        BudgetIcons.trash,
                        stringResource(R.string.delete),
                        type = PopupType.Error
                    ),
                ),
                popupItemOnClick = {
                    if (it == 0){
                        navToEdit()
                    }else if (it == 1){
                        showDeleteDialog = true
                    }
                }
            )
        }
    ) {
        Column(
            Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {

            SampleItem(
                modifier = Modifier.padding(horizontal = 24.dp), title = stringResource(
                    R.string.amount
                ), secondText = formatInput(transaction.value?.amount.orDefault().toString())
            ) {

            }

            SampleItem(
                modifier = Modifier.padding(horizontal = 24.dp), title = stringResource(
                    R.string.category
                ), secondText = transaction.value?.name
            ) {

            }

            SampleItem(
                modifier = Modifier.padding(horizontal = 24.dp), title = stringResource(
                    R.string.date
                ), secondText = transaction.value?.date.orDefault().toPersianDate(true)
            ) {

            }

            SampleItem(
                modifier = Modifier.padding(horizontal = 24.dp), title = stringResource(
                    R.string.type
                ), secondText = when (transaction.value?.type) {
                    Constants.BudgetType.INCOME -> stringResource(R.string.income)
                    Constants.BudgetType.EXPENSE -> stringResource(R.string.expenses)
                    else -> stringResource(R.string.internal_transfer)
                }
            ) {

            }

            SampleItem(
                modifier = Modifier.padding(horizontal = 24.dp), title = stringResource(
                    R.string.source_expenditure
                ), secondText = title
            ) {

            }


            if (transaction.value?.tags.orEmpty().isNotEmpty()) {
                TextDivider(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .padding(horizontal = 16.dp),
                    title = stringResource(R.string.tags)
                )

                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(top = 8.dp)
                        .padding(horizontal = 24.dp),
                ) {
                    transaction.value?.tags.orEmpty().forEach {
                        Tag(
                            modifier = Modifier.padding(end = 8.dp),
                            text = it,
                            enableDismiss = false
                        )
                    }
                }
            }




            if (transaction.value?.contacts.orEmpty().isNotEmpty()) {
                // contacts
                TextDivider(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    title = stringResource(id = R.string.people)
                )
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(top = 16.dp)
                        .padding(horizontal = 24.dp)
                ) {


                    transaction.value?.contacts.orEmpty().forEach {
                        Tag(
                            modifier = Modifier.padding(end = 8.dp),
                            text = it.fullName,
                            enableDismiss = false
                        ){
                            selectedContact.value = it
                        }
                    }

                }
            }


            if (transaction.value?.images.orEmpty().isNotEmpty()) {
                TextDivider(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .padding(horizontal = 16.dp),
                    title = stringResource(R.string.images)
                )


                ImagesPreview(
                    Modifier.padding(top = 8.dp),
                    remember<SnapshotStateList<String>> { mutableStateListOf() }.also {
                        it.clear()
                        it.addAll(
                            transaction.value?.images.orEmpty()
                        )
                    },
                    PaddingValues(),
                    removeAble = false,
                    onClick = {
                        navToImageFull(it)
                    }
                )
            }

            if (transaction.value?.note.orEmpty().isNotEmpty()) {
                TextDivider(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .padding(horizontal = 16.dp),
                    title = stringResource(R.string.description)
                )

                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp),
                    text = transaction.value?.note.orEmpty(),
                    style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.outlineVariant)
                )
            }


        }
    }


    if (selectedContact.value != null) {
        AlertDialog(
            onDismissRequest = {
                selectedContact.value = null
            },
            title = { Text(text = selectedContact.value?.fullName.orEmpty(), style = MaterialTheme.typography.titleMedium) },
            text = {
                SampleItem(
                    modifier = Modifier.padding(horizontal = 4.dp), title = stringResource(
                        R.string.phone_number
                    ), secondText = selectedContact.value?.phoneNumber
                ) {

                }
            },
            confirmButton = {
                BudgetButton(text = stringResource(R.string.close), type = ButtonType.Neutral, style = ButtonStyle.TextOnly, size = ButtonSize.SM){
                    selectedContact.value = null
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            title = { Text(text = stringResource(R.string.delete_transaction_title), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.titleMedium) },
            text = {
                Text(text = stringResource(R.string.delete_transaction_desc), style = MaterialTheme.typography.bodySmall)
            },
            confirmButton = {
               Row {
                   BudgetButton(
                       text = stringResource(R.string.cancel), type = ButtonType.Neutral, style = ButtonStyle.TextOnly, size = ButtonSize.SM){
                       showDeleteDialog = false
                   }

                   BudgetButton(
                       modifier = Modifier.padding(start = 4.dp),
                       text = stringResource(R.string.delete), type = ButtonType.Error, style = ButtonStyle.TextOnly, size = ButtonSize.SM){
                       if (transaction.value != null){
                           delete(transaction.value!!.id)
                       }
                   }


               }
            }
        )
    }


}