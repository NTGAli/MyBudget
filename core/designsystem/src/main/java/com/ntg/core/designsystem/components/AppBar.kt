package com.ntg.core.designsystem.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.ntg.core.designsystem.model.AppbarItem
import com.ntg.core.designsystem.model.PopupItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    title: String = "",
    titleState: @Composable () -> Unit = {},
    enableNavigation: Boolean = true,
    navigationOnClick: () -> Unit = {},
    navigateIconColor: Color = MaterialTheme.colorScheme.outline,
    enableSearchbar: MutableState<Boolean> = remember { mutableStateOf(false) },
    searchQueryText: MutableState<String> = remember { mutableStateOf("") },
    actions: List<AppbarItem> = emptyList(),
    popupItems: List<PopupItem>? = null,
    actionOnClick: (Int) -> Unit = {},
    popupItemOnClick: (Int) -> Unit = {},
    onQueryChange: (String) -> Unit = {}
) {
    if (enableSearchbar.value) {
        SearchBar(
            searchQueryText,
            onQueryChange = { onQueryChange.invoke(it) },
            onDismiss = { enableSearchbar.value = false })
    } else {
        Column(modifier = modifier) {

            TopAppBar(
                title = {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            title,
                            maxLines = 1,
                            style = MaterialTheme.typography.labelLarge.copy(MaterialTheme.colorScheme.onBackground)
                        )

                        titleState()

                    }

                },
                navigationIcon = {
                    if (enableNavigation) {
                        var backIcon = Icons.Rounded.KeyboardArrowLeft
                        if (LocalLayoutDirection.current == LayoutDirection.Rtl) {
                            backIcon = Icons.Rounded.KeyboardArrowRight
                        }
                        IconButton(onClick = { navigationOnClick.invoke() }) {
                            Icon(
                                imageVector = backIcon,
                                contentDescription = "navigation",
                                tint = navigateIconColor
                            )
                        }

                    }

                },
                actions = {
                    actions.forEach { appbarItem ->
                        IconButton(onClick = { actionOnClick.invoke(appbarItem.id) }) {
                            Icon(
                                imageVector = appbarItem.imageVector,
                                tint = appbarItem.iconColor,
                                contentDescription = "action appbar"
                            )
                        }
                    }

                    if (popupItems != null) {
                        Popup(popupItems = popupItems) {
                            popupItemOnClick.invoke(it)
                        }
                    }

                },
                colors = TopAppBarDefaults.topAppBarColors(
                    MaterialTheme.colorScheme.background
                )
                ,
//                scrollBehavior = scrollBehavior,
                windowInsets = TopAppBarDefaults.windowInsets
            )

            if ((scrollBehavior?.state?.contentOffset ?: 0f) < -25f) {
                HorizontalDivider(Modifier.height(1.dp), color = MaterialTheme.colorScheme.surfaceVariant)
            }

        }

    }


}

@Composable
fun Popup(modifier: Modifier = Modifier, popupItems: List<PopupItem>, onClick: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(
            onClick = {
                expanded = true
                onClick.invoke(-1)
            }
        ) {
            Icon(
                imageVector = Icons.Rounded.MoreVert,
                tint = MaterialTheme.colorScheme.outline,
                contentDescription = "action appbar"
            )
        }



        MaterialTheme(
            shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))
        ) {

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {

                popupItems.forEach {
                    DropdownMenuItem(
                        onClick = {
                            onClick.invoke(it.id)
                            expanded = false
                        },
                        interactionSource = MutableInteractionSource(),
                        text = {
                            Text(it.title, style = MaterialTheme.typography.labelLarge.copy(MaterialTheme.colorScheme.outline))
                        },
                        leadingIcon = {
                            Icon(
                                painter = it.icon,
                                contentDescription = it.title,
                                tint = MaterialTheme.colorScheme.outline
                            )
                        }
                    )
                }


            }

        }
    }
}

@Composable
fun SearchBar(
    searchQueryText: MutableState<String> = remember { mutableStateOf("") },
    onQueryChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(searchQueryText.value) }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current



    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        value = text,
        onValueChange = {
            text = it
            onQueryChange.invoke(it)
        },
        singleLine = true,
        textStyle = MaterialTheme.typography.labelLarge.copy(MaterialTheme.colorScheme.onSurfaceVariant),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                focusManager.clearFocus()
            }
        ),
        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = {
                onQueryChange("")
                onDismiss()
            }) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "leading",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}