package com.ntg.features.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.ntg.core.designsystem.components.AppBar
import com.ntg.core.designsystem.components.BudgetButton
import com.ntg.core.designsystem.components.BudgetTextField
import com.ntg.core.designsystem.components.ButtonStyle
import com.ntg.core.designsystem.components.FullScreenBottomSheet
import com.ntg.core.designsystem.components.SampleItem
import com.ntg.core.designsystem.components.SwitchText
import com.ntg.core.designsystem.components.Tag
import com.ntg.core.designsystem.components.TextDivider
import com.ntg.core.designsystem.components.WheelList
import com.ntg.core.designsystem.model.SwitchItem
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.core.model.Contact
import com.ntg.core.model.Transaction
import com.ntg.core.model.TransactionFilter
import com.ntg.core.model.res.Category
import com.ntg.core.mybudget.common.Constants
import com.ntg.core.mybudget.common.getCurrentJalaliDate
import com.ntg.core.mybudget.common.jalaliToTimestamp
import com.ntg.core.mybudget.common.number.numToText
import com.ntg.core.mybudget.common.persianDate.PersianDate
import com.ntg.core.mybudget.common.toPersianDate
import com.ntg.mybudget.core.designsystem.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TransactionFilterBottomSheet(
    showSheet: MutableState<Boolean>,
    categories: List<Category>?,
    allTransactions: List<Transaction>?,
    initialFilter: TransactionFilter,
    onApplyFilter: (TransactionFilter) -> Unit,
    onClearFilter: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    BackHandler(enabled = showSheet.value) {
        showSheet.value = false
    }

    val defaultTypeIndex = when (initialFilter.type) {
        Constants.BudgetType.EXPENSE -> 0
        Constants.BudgetType.INCOME -> 1
        Constants.BudgetType.TRANSFER -> 2
        else -> 3
    }
    var filterTypeIndex by remember(initialFilter) { mutableIntStateOf(defaultTypeIndex) }

    var dateFrom by remember(initialFilter) { mutableStateOf(initialFilter.dateFrom) }
    var dateTo by remember(initialFilter) { mutableStateOf(initialFilter.dateTo) }

    val dateFromText = remember(initialFilter) {
        mutableStateOf(initialFilter.dateFrom?.toPersianDate().orEmpty())
    }
    val dateToText = remember(initialFilter) {
        mutableStateOf(initialFilter.dateTo?.toPersianDate().orEmpty())
    }

    // Multi-select categories
    val selectedCategories = remember(initialFilter) {
        mutableStateListOf<Category>().apply {
            categories?.filter { it.id in initialFilter.categoryIds }?.let { addAll(it) }
        }
    }
    val categoryText = remember(initialFilter) {
        mutableStateOf(
            when {
                initialFilter.categoryIds.isEmpty() -> ""
                initialFilter.categoryIds.size == 1 -> categories?.find { it.id == initialFilter.categoryIds.first() }?.name.orEmpty()
                else -> "${initialFilter.categoryIds.size} دسته بندی"
            }
        )
    }

    // Tags from existing transactions
    val allExistingTags = remember(allTransactions) {
        allTransactions?.flatMap { it.tags.orEmpty() }?.distinct()?.sorted() ?: emptyList()
    }
    val tags = remember(initialFilter) {
        mutableStateListOf<String>().apply { addAll(initialFilter.tags) }
    }

    // Contacts from existing transactions
    val allExistingContacts = remember(allTransactions) {
        allTransactions?.flatMap { it.contacts.orEmpty() }
            ?.distinctBy { it.fullName }
            ?.sortedBy { it.fullName }
            ?: emptyList()
    }
    val selectedContactNames = remember(initialFilter) {
        mutableStateListOf<String>().apply { addAll(initialFilter.contactNames) }
    }

    var hasImage by remember(initialFilter) { mutableStateOf(initialFilter.hasImage) }

    // Amount range
    val amountMinText = remember(initialFilter) {
        mutableStateOf(initialFilter.amountMin?.toString().orEmpty())
    }
    val amountMaxText = remember(initialFilter) {
        mutableStateOf(initialFilter.amountMax?.toString().orEmpty())
    }

    var showFromDatePicker by remember { mutableStateOf(false) }
    var showToDatePicker by remember { mutableStateOf(false) }
    var showCategorySheet by remember { mutableStateOf(false) }
    var showTagSheet by remember { mutableStateOf(false) }
    var showContactSheet by remember { mutableStateOf(false) }

    FullScreenBottomSheet(
        showSheet = showSheet,
        scrollBehavior = scrollBehavior,
        appbar = {
            AppBar(
                title = stringResource(id = R.string.filter_transactions),
                scrollBehavior = scrollBehavior,
                navigationOnClick = { showSheet.value = false }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            // Transaction Type
            TextDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                title = stringResource(id = R.string.transaction_type)
            )

            val switchItems = listOf(
                SwitchItem(
                    0,
                    stringResource(id = R.string.outcome),
                    tint = MaterialTheme.colorScheme.onError,
                    backColor = MaterialTheme.colorScheme.error
                ),
                SwitchItem(
                    1,
                    stringResource(id = R.string.income),
                    tint = MaterialTheme.colorScheme.onSecondary,
                    backColor = MaterialTheme.colorScheme.secondary
                ),
                SwitchItem(
                    2,
                    stringResource(id = R.string.internal_transfer),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    backColor = MaterialTheme.colorScheme.primary
                ),
                SwitchItem(
                    3,
                    stringResource(id = R.string.all),
                    tint = MaterialTheme.colorScheme.onSurface,
                    backColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )

            SwitchText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                items = switchItems,
                defaultSelected = filterTypeIndex
            ) {
                filterTypeIndex = it
                // Clear categories when type changes since they're type-specific
                selectedCategories.clear()
                categoryText.value = ""
            }

            // Date Range
            TextDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                title = stringResource(id = R.string.date_range)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BudgetTextField(
                    modifier = Modifier.weight(1f),
                    text = dateFromText,
                    label = stringResource(id = R.string.from_date),
                    readOnly = true,
                    trailingIcon = painterResource(id = BudgetIcons.CalenderTick),
                    onClick = { showFromDatePicker = true }
                )

                BudgetTextField(
                    modifier = Modifier.weight(1f),
                    text = dateToText,
                    label = stringResource(id = R.string.to_date),
                    readOnly = true,
                    trailingIcon = painterResource(id = BudgetIcons.CalenderTick),
                    onClick = { showToDatePicker = true }
                )
            }

            // Amount Range
            TextDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                title = stringResource(id = R.string.amount_range)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BudgetTextField(
                    modifier = Modifier.weight(1f),
                    text = amountMinText,
                    label = stringResource(id = R.string.min_amount),
                    keyboardType = KeyboardType.Number
                )

                BudgetTextField(
                    modifier = Modifier.weight(1f),
                    text = amountMaxText,
                    label = stringResource(id = R.string.max_amount),
                    keyboardType = KeyboardType.Number
                )
            }

            // Amount in words
            val amountMinVal = amountMinText.value.toLongOrNull()
            val amountMaxVal = amountMaxText.value.toLongOrNull()
            if (amountMinVal != null || amountMaxVal != null) {
                val tomanLabel = stringResource(id = R.string.toman)
                val wordsText = buildString {
                    if (amountMinVal != null) {
                        append(numToText(amountMinVal.toString()))
                        append(" $tomanLabel")
                    }
                    if (amountMinVal != null && amountMaxVal != null) {
                        append("  ~  ")
                    }
                    if (amountMaxVal != null) {
                        append(numToText(amountMaxVal.toString()))
                        append(" $tomanLabel")
                    }
                }
                Text(
                    text = wordsText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Category (multi-select, filtered by type)
            TextDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                title = stringResource(id = R.string.category)
            )

            BudgetTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                text = categoryText,
                label = stringResource(id = R.string.select_category),
                readOnly = true,
                trailingIcon = painterResource(id = BudgetIcons.directionLeft),
                onClick = { showCategorySheet = true }
            )

            if (selectedCategories.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    selectedCategories.forEach { category ->
                        Tag(
                            text = category.name,
                            dismissClick = {
                                selectedCategories.remove(category)
                                categoryText.value = when {
                                    selectedCategories.isEmpty() -> ""
                                    selectedCategories.size == 1 -> selectedCategories.first().name
                                    else -> "${selectedCategories.size} دسته بندی"
                                }
                            }
                        )
                    }
                }
            }

            // Tags
            TextDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                title = stringResource(id = R.string.tags)
            )

            BudgetTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                text = remember { mutableStateOf("") },
                label = stringResource(id = R.string.search_tags),
                readOnly = true,
                trailingIcon = painterResource(id = BudgetIcons.directionLeft),
                onClick = { showTagSheet = true }
            )

            if (tags.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tags.forEach { tag ->
                        Tag(
                            text = tag,
                            dismissClick = { tags.remove(tag) }
                        )
                    }
                }
            }

            // Contacts filter
            if (allExistingContacts.isNotEmpty()) {
                TextDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    title = stringResource(id = R.string.select_contacts)
                )

                val contactText = remember(selectedContactNames.size) {
                    mutableStateOf(
                        when {
                            selectedContactNames.isEmpty() -> ""
                            selectedContactNames.size == 1 -> selectedContactNames.first()
                            else -> "${selectedContactNames.size} مخاطب"
                        }
                    )
                }

                BudgetTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    text = contactText,
                    label = stringResource(id = R.string.select_contacts),
                    readOnly = true,
                    trailingIcon = painterResource(id = BudgetIcons.directionLeft),
                    onClick = { showContactSheet = true }
                )

                if (selectedContactNames.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier.padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        selectedContactNames.forEach { name ->
                            Tag(
                                text = name,
                                dismissClick = { selectedContactNames.remove(name) }
                            )
                        }
                    }
                }
            }

            // Has Image
            SampleItem(
                modifier = Modifier.padding(top = 8.dp),
                title = stringResource(id = R.string.only_with_images),
                setSwitch = true,
                isSwitchCheck = hasImage
            ) {
                hasImage = !hasImage
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Apply Button
            BudgetButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                text = stringResource(id = R.string.apply_filters)
            ) {
                val type = when (filterTypeIndex) {
                    0 -> Constants.BudgetType.EXPENSE
                    1 -> Constants.BudgetType.INCOME
                    2 -> Constants.BudgetType.TRANSFER
                    else -> null
                }
                val amountMin = amountMinText.value.toLongOrNull()
                val amountMax = amountMaxText.value.toLongOrNull()
                onApplyFilter(
                    TransactionFilter(
                        type = type,
                        dateFrom = dateFrom,
                        dateTo = dateTo,
                        categoryIds = selectedCategories.map { it.id },
                        tags = tags.toList(),
                        hasImage = hasImage,
                        amountMin = amountMin,
                        amountMax = amountMax,
                        contactNames = selectedContactNames.toList()
                    )
                )
                showSheet.value = false
            }

            // Clear Button
            BudgetButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                text = stringResource(id = R.string.clear_filters),
                style = ButtonStyle.TextOnly
            ) {
                filterTypeIndex = 3
                dateFrom = null
                dateTo = null
                dateFromText.value = ""
                dateToText.value = ""
                selectedCategories.clear()
                categoryText.value = ""
                tags.clear()
                hasImage = false
                amountMinText.value = ""
                amountMaxText.value = ""
                selectedContactNames.clear()
                onClearFilter()
                showSheet.value = false
            }
        }
    }

    // From Date Picker
    if (showFromDatePicker) {
        JalaliDatePickerSheet(
            onDismiss = { showFromDatePicker = false },
            onDateSelected = { timestamp, displayText ->
                dateFrom = timestamp
                dateFromText.value = displayText
                showFromDatePicker = false
            }
        )
    }

    // To Date Picker
    if (showToDatePicker) {
        JalaliDatePickerSheet(
            onDismiss = { showToDatePicker = false },
            onDateSelected = { timestamp, displayText ->
                dateTo = timestamp
                dateToText.value = displayText
                showToDatePicker = false
            }
        )
    }

    // Category selection sheet with multi-select, filtered by type
    if (showCategorySheet) {
        CategorySelectionSheet(
            filterTypeIndex = filterTypeIndex,
            categories = categories,
            selectedCategories = selectedCategories,
            categoryText = categoryText,
            onDismiss = { showCategorySheet = false }
        )
    }

    // Tag selection sheet
    if (showTagSheet) {
        TagSelectionSheet(
            allExistingTags = allExistingTags,
            tags = tags,
            onDismiss = { showTagSheet = false }
        )
    }

    // Contact selection sheet
    if (showContactSheet) {
        ContactSelectionSheet(
            allExistingContacts = allExistingContacts,
            selectedContactNames = selectedContactNames,
            onDismiss = { showContactSheet = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategorySelectionSheet(
    filterTypeIndex: Int,
    categories: List<Category>?,
    selectedCategories: MutableList<Category>,
    categoryText: MutableState<String>,
    onDismiss: () -> Unit
) {
    val currentBudgetType = when (filterTypeIndex) {
        0 -> Constants.BudgetType.EXPENSE
        1 -> Constants.BudgetType.INCOME
        else -> null
    }
    val filteredCategories = if (currentBudgetType != null) {
        categories?.filter { it.type == currentBudgetType }
    } else {
        categories
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
            ) {
                item {
                    SampleItem(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        title = stringResource(id = R.string.all_categories),
                        setCheckbox = true,
                        isRadioCheck = selectedCategories.isEmpty()
                    ) {
                        selectedCategories.clear()
                        categoryText.value = ""
                    }
                }

                items(filteredCategories.orEmpty()) { category ->
                    SampleItem(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        title = category.name,
                        setCheckbox = true,
                        isRadioCheck = selectedCategories.any { it.id == category.id }
                    ) {
                        if (selectedCategories.any { it.id == category.id }) {
                            selectedCategories.removeAll { it.id == category.id }
                        } else {
                            selectedCategories.add(category)
                        }
                        categoryText.value = when {
                            selectedCategories.isEmpty() -> ""
                            selectedCategories.size == 1 -> selectedCategories.first().name
                            else -> "${selectedCategories.size} دسته بندی"
                        }
                    }
                }
            }

            // Sticky confirm button at bottom
            BudgetButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                text = stringResource(id = R.string.confirm_selection)
            ) {
                onDismiss()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TagSelectionSheet(
    allExistingTags: List<String>,
    tags: MutableList<String>,
    onDismiss: () -> Unit
) {
    val tagSearchQuery = remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            BudgetTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp),
                text = tagSearchQuery,
                label = stringResource(id = R.string.search_tags),
                searchMode = true,
                onChange = { tagSearchQuery.value = it }
            )

            val filteredTags = allExistingTags.filter {
                tagSearchQuery.value.isEmpty() || it.contains(tagSearchQuery.value, ignoreCase = true)
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
            ) {
                // Option to add custom tag
                if (tagSearchQuery.value.isNotEmpty() && tagSearchQuery.value !in allExistingTags) {
                    item {
                        SampleItem(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            title = tagSearchQuery.value,
                            iconPainter = painterResource(id = BudgetIcons.Add),
                            setCheckbox = true,
                            isRadioCheck = tags.contains(tagSearchQuery.value)
                        ) {
                            if (!tags.contains(tagSearchQuery.value)) {
                                tags.add(tagSearchQuery.value)
                            }
                            tagSearchQuery.value = ""
                        }
                    }
                }

                items(filteredTags) { tag ->
                    SampleItem(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        title = tag,
                        setCheckbox = true,
                        isRadioCheck = tags.contains(tag)
                    ) {
                        if (tags.contains(tag)) {
                            tags.remove(tag)
                        } else {
                            tags.add(tag)
                        }
                    }
                }
            }

            // Sticky confirm button at bottom
            BudgetButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                text = stringResource(id = R.string.confirm_selection)
            ) {
                onDismiss()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactSelectionSheet(
    allExistingContacts: List<Contact>,
    selectedContactNames: MutableList<String>,
    onDismiss: () -> Unit
) {
    val searchQuery = remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            BudgetTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp),
                text = searchQuery,
                label = stringResource(id = R.string.search_contacts),
                searchMode = true,
                onChange = { searchQuery.value = it }
            )

            val filteredContacts = allExistingContacts.filter {
                searchQuery.value.isEmpty() || (it.fullName?.contains(searchQuery.value, ignoreCase = true) == true)
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
            ) {
                items(filteredContacts) { contact ->
                    val name = contact.fullName.orEmpty()
                    SampleItem(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        title = name,
                        subText = contact.phoneNumber,
                        setCheckbox = true,
                        isRadioCheck = selectedContactNames.contains(name)
                    ) {
                        if (selectedContactNames.contains(name)) {
                            selectedContactNames.remove(name)
                        } else {
                            selectedContactNames.add(name)
                        }
                    }
                }
            }

            // Sticky confirm button at bottom
            BudgetButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                text = stringResource(id = R.string.confirm_selection)
            ) {
                onDismiss()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JalaliDatePickerSheet(
    onDismiss: () -> Unit,
    onDateSelected: (Long, String) -> Unit
) {
    val months = listOf(
        "فروردین", "اردیبهشت", "خرداد",
        "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر",
        "دی", "بهمن", "اسفند"
    )

    val currentJalali = getCurrentJalaliDate()
    val currentYear = currentJalali.first

    val selectedDateState = remember {
        mutableStateListOf(
            currentJalali.first.toString(),
            months[currentJalali.second - 1],
            currentJalali.third.toString()
        )
    }

    var daysInMonth by remember { mutableIntStateOf(31) }

    LaunchedEffect(selectedDateState[1]) {
        daysInMonth = when (months.indexOfFirst { it == selectedDateState[1] } + 1) {
            in 1..6 -> 31
            in 7..11 -> 30
            12 -> if (PersianDate().isLeap(selectedDateState[0].toInt())) 30 else 29
            else -> 31
        }
    }

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss
    ) {
        Column {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Row(
                    modifier = Modifier.padding(vertical = 24.dp)
                ) {
                    WheelList(
                        modifier = Modifier.weight(1f),
                        items = (currentYear - 5..currentYear).toList(),
                        initialItem = selectedDateState[0].toInt(),
                        onItemSelected = { i, _ ->
                            selectedDateState[0] = (currentYear - 5..currentYear).toList()[i].toString()
                        }
                    )

                    WheelList(
                        modifier = Modifier.weight(1f),
                        items = months,
                        initialItem = selectedDateState[1],
                        onItemSelected = { i, _ ->
                            selectedDateState[1] = months[i]
                        }
                    )

                    WheelList(
                        modifier = Modifier.weight(1f),
                        items = (1..daysInMonth).toList(),
                        initialItem = selectedDateState[2].toInt(),
                        onItemSelected = { i, _ ->
                            selectedDateState[2] = (1..daysInMonth).toList()[i].toString()
                        }
                    )
                }
            }

            BudgetButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .padding(bottom = 24.dp),
                text = stringResource(id = R.string.confirm)
            ) {
                val year = selectedDateState[0].toInt()
                val monthIndex = months.indexOfFirst { it == selectedDateState[1] } + 1
                val day = selectedDateState[2].toInt()
                val timestamp = jalaliToTimestamp(
                    year = year,
                    month = monthIndex,
                    day = day,
                    hour = 0,
                    minute = 0
                )
                val displayText = "$day ${selectedDateState[1]} $year"
                onDateSelected(timestamp, displayText)
                scope.launch { sheetState.hide() }
            }
        }
    }
}
