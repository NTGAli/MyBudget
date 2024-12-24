package com.ntg.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ntg.mybudget.core.designsystem.R

@Composable
fun SearchTextField(
    modifier: Modifier = Modifier,
    query: MutableState<String> = remember { mutableStateOf("") },
    ){

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(16.dp))
            .background(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            modifier = Modifier.size(16.dp),
            imageVector = Icons.Rounded.Search, contentDescription = null)


        Box(
            modifier = Modifier.padding(start = 8.dp)
                .weight(1f),
        ) {

            if (query.value.isEmpty()) {
                Text(
                    modifier = Modifier.align(Alignment.CenterStart),
                    text = stringResource(id = R.string.searchInPeople),
                    style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.outline),
                )
            }
            BasicTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterStart),
                value = query.value,
                onValueChange = {
                    query.value = it
                },
                maxLines = 1,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.outlineVariant),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
            )
        }

    }
}

@Composable
@Preview(showBackground = true)
private fun TestSearchTextField(){
    SearchTextField(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
    )
}