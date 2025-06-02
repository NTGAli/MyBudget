//package com.ntg.features.home
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun TransactionFilterBottomSheet(
//    showSheet: MutableState<Boolean>,
//    categories: List<Category>?,
//    initialFilter: TransactionFilter = TransactionFilter(),
//    onApplyFilter: (TransactionFilter) -> Unit
//) {
//    val filterType = remember { mutableIntStateOf(initialFilter.type ?: -1) }
//    val dateFrom = remember { mutableLongStateOf(initialFilter.dateFrom ?: 0) }
//    val dateTo = remember { mutableLongStateOf(initialFilter.dateTo ?: 0) }
//    val selectedCategory = remember { mutableStateOf<Category?>(
//        categories?.find { it.id == initialFilter.categoryId }
//    ) }
//    val tags = remember { mutableStateListOf<String>().apply { addAll(initialFilter.tags) } }
//    val tagInput = remember { mutableStateOf("") }
//    val hasImage = remember { mutableStateOf(initialFilter.hasImage) }
//
//    // Date picker states
//    var showFromDatePicker by remember { mutableStateOf(false) }
//    var showToDatePicker by remember { mutableStateOf(false) }
//
//    // For category selection
//    var showCategorySheet by remember { mutableStateOf(false) }
//    val categorySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
//
//    ModalBottomSheet(
//        onDismissRequest = { showSheet.value = false },
//        sheetState = rememberModalBottomSheetState()
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 24.dp)
//                .verticalScroll(rememberScrollState())
//        ) {
//            // Header
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 16.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = stringResource(id = R.string.filter_transactions),
//                    style = MaterialTheme.typography.titleLarge,
//                    modifier = Modifier.weight(1f)
//                )
//
//                IconButton(onClick = { showSheet.value = false }) {
//                    Icon(
//                        painter = painterResource(id = BudgetIcons.close),
//                        contentDescription = "Close"
//                    )
//                }
//            }
//
//            // Transaction Type Filter
//            TextDivider(
//                modifier = Modifier.fillMaxWidth(),
//                title = stringResource(id = R.string.transaction_type)
//            )
//
//            val items = listOf(
//                SwitchItem(
//                    0,
//                    stringResource(id = R.string.outcome),
//                    tint = MaterialTheme.colorScheme.onError,
//                    backColor = MaterialTheme.colorScheme.error
//                ),
//                SwitchItem(
//                    0,
//                    stringResource(id = R.string.income),
//                    tint = MaterialTheme.colorScheme.onSecondary,
//                    backColor = MaterialTheme.colorScheme.secondary
//                ),
//                SwitchItem(
//                    0,
//                    stringResource(id = R.string.internal_transfer),
//                    tint = MaterialTheme.colorScheme.onPrimary,
//                    backColor = MaterialTheme.colorScheme.primary
//                ),
//                SwitchItem(
//                    0,
//                    stringResource(id = R.string.all),
//                    tint = MaterialTheme.colorScheme.onSurface,
//                    backColor = MaterialTheme.colorScheme.surfaceVariant
//                )
//            )
//
//            SwitchText(
//                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
//                items = items,
//                selectedIndex = if (filterType.intValue == -1) 3 else filterType.intValue
//            ) {
//                filterType.intValue = if (it == 3) -1 else it
//            }
//
//            // Date Range Filter
//            TextDivider(
//                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
//                title = stringResource(id = R.string.date_range)
//            )
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 8.dp),
//                horizontalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                BudgetTextField(
//                    modifier = Modifier.weight(1f),
//                    text = remember(dateFrom.longValue) {
//                        mutableStateOf(
//                            if (dateFrom.longValue > 0) {
//                                SimpleDateFormat("yyyy/MM/dd").format(Date(dateFrom.longValue))
//                            } else ""
//                        )
//                    },
//                    label = stringResource(id = R.string.from_date),
//                    readOnly = true,
//                    trailingIcon = painterResource(id = BudgetIcons.calendar),
//                    onClick = { showFromDatePicker = true }
//                )
//
//                BudgetTextField(
//                    modifier = Modifier.weight(1f),
//                    text = remember(dateTo.longValue) {
//                        mutableStateOf(
//                            if (dateTo.longValue > 0) {
//                                SimpleDateFormat("yyyy/MM/dd").format(Date(dateTo.longValue))
//                            } else ""
//                        )
//                    },
//                    label = stringResource(id = R.string.to_date),
//                    readOnly = true,
//                    trailingIcon = painterResource(id = BudgetIcons.calendar),
//                    onClick = { showToDatePicker = true }
//                )
//            }
//
//            // Category Filter
//            TextDivider(
//                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
//                title = stringResource(id = R.string.catgory)
//            )
//
//            BudgetTextField(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 8.dp),
//                text = remember(selectedCategory.value) {
//                    mutableStateOf(selectedCategory.value?.name.orEmpty())
//                },
//                label = stringResource(id = R.string.select_category),
//                readOnly = true,
//                trailingIcon = painterResource(id = BudgetIcons.directionLeft),
//                onClick = { showCategorySheet = true }
//            )
//
//            // Tags Filter
//            TextDivider(
//                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
//                title = stringResource(id = R.string.tags)
//            )
//
//            BudgetTextField(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 8.dp),
//                text = tagInput,
//                label = stringResource(id = R.string.add_tag),
//                trailingIcon = painterResource(id = BudgetIcons.Add),
//                trailingIconOnClick = {
//                    if (it.isNotEmpty() && !tags.contains(it)) {
//                        tags.add(it)
//                    }
//                    tagInput.value = ""
//                }
//            )
//
//            Row(
//                modifier = Modifier
//                    .horizontalScroll(rememberScrollState())
//                    .padding(bottom = 8.dp),
//            ) {
//                tags.forEach { tag ->
//                    Tag(
//                        modifier = Modifier.padding(end = 8.dp),
//                        text = tag,
//                        dismissClick = {
//                            tags.remove(tag)
//                        }
//                    ) {
//                        tags.remove(tag)
//                    }
//                }
//            }
//
//            // Has Image Filter
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 16.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = stringResource(id = R.string.only_with_images),
//                    style = MaterialTheme.typography.bodyLarge,
//                    modifier = Modifier.weight(1f)
//                )
//
//                Switch(
//                    checked = hasImage.value,
//                    onCheckedChange = { hasImage.value = it }
//                )
//            }
//
//            // Apply Button
//            BudgetButton(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 16.dp),
//                text = stringResource(id = R.string.apply_filters),
//                onClick = {
//                    onApplyFilter(
//                        TransactionFilter(
//                            type = if (filterType.intValue == -1) null else filterType.intValue,
//                            dateFrom = if (dateFrom.longValue == 0L) null else dateFrom.longValue,
//                            dateTo = if (dateTo.longValue == 0L) null else dateTo.longValue,
//                            categoryId = selectedCategory.value?.id,
//                            tags = tags.toList(),
//                            hasImage = hasImage.value
//                        )
//                    )
//                    showSheet.value = false
//                }
//            )
//
//            // Clear Filters Button
//            BudgetButton(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 24.dp),
//                text = stringResource(id = R.string.clear_filters),
//                style = ButtonStyle.TextOnly,
//                onClick = {
//                    filterType.intValue = -1
//                    dateFrom.longValue = 0
//                    dateTo.longValue = 0
//                    selectedCategory.value = null
//                    tags.clear()
//                    hasImage.value = false
//
//                    onApplyFilter(TransactionFilter())
//                    showSheet.value = false
//                }
//            )
//        }
//    }
//
//    // Date Pickers
//    if (showFromDatePicker) {
//        DatePickerDialog(
//            onDismissRequest = { showFromDatePicker = false },
//            onDateSelected = { dateFrom.longValue = it }
//        )
//    }
//
//    if (showToDatePicker) {
//        DatePickerDialog(
//            onDismissRequest = { showToDatePicker = false },
//            onDateSelected = { dateTo.longValue = it }
//        )
//    }
//
//    // Category Selection Bottom Sheet
//    if (showCategorySheet) {
//        ModalBottomSheet(
//            onDismissRequest = { showCategorySheet = false },
//            sheetState = categorySheetState
//        ) {
//            LazyColumn(
//                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
//            ) {
//                item {
//                    // Add "All Categories" option
//                    SampleItem(
//                        modifier = Modifier.padding(horizontal = 16.dp),
//                        title = stringResource(id = R.string.all_categories),
//                        setRadio = true,
//                        isRadioCheck = selectedCategory.value == null
//                    ) {
//                        selectedCategory.value = null
//                        showCategorySheet = false
//                    }
//                }
//
//                items(categories.orEmpty()) { category ->
//                    SampleItem(
//                        modifier = Modifier.padding(horizontal = 16.dp),
//                        title = category.name,
//                        setRadio = true,
//                        isRadioCheck = selectedCategory.value?.id == category.id
//                    ) {
//                        selectedCategory.value = category
//                        showCategorySheet = false
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun DatePickerDialog(
//    onDismissRequest: () -> Unit,
//    onDateSelected: (Long) -> Unit
//) {
//    val datePickerState = rememberDatePickerState()
//
//    DatePickerDialog(
//        onDismissRequest = onDismissRequest,
//        confirmButton = {
//            TextButton(onClick = {
//                datePickerState.selectedDateMillis?.let { onDateSelected(it) }
//                onDismissRequest()
//            }) {
//                Text(stringResource(id = R.string.confirm))
//            }
//        },
//        dismissButton = {
//            TextButton(onClick = onDismissRequest) {
//                Text(stringResource(id = R.string.cancel))
//            }
//        }
//    ) {
//        DatePicker(state = datePickerState)
//    }
//}