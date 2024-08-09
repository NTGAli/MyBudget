package com.ntg.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ntg.core.designsystem.components.AppBar
import com.ntg.core.designsystem.components.OtpField
import com.ntg.feature.login.R

@Composable
fun CodeRoute(
    onBack:() -> Unit
) {
    CodeScreen(
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CodeScreen(
    onBack:() -> Unit
) {

    var wasWrong by remember {
        mutableStateOf(false)
    }

    var isSucceeded by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            AppBar(
                enableNavigation = true,
                navigationOnClick = onBack
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = stringResource(id = R.string.entere_code),
                style = MaterialTheme.typography.titleSmall.copy(MaterialTheme.colorScheme.onBackground)
            )

            Text(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 8.dp),
                text = stringResource(id = R.string.sent_code_format),
                style = MaterialTheme.typography.bodySmall.copy(MaterialTheme.colorScheme.outline)
            )

            OtpField(
                modifier = Modifier.padding(top = 32.dp),
                wasWrong = wasWrong, isSucceeded = isSucceeded
            ) { text, bool ->

            }

        }
    }

}