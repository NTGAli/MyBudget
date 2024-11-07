package com.ntg.features.profile

import android.content.pm.PackageInfo
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ntg.core.designsystem.components.AppBar
import com.ntg.core.designsystem.components.ProfileCell
import com.ntg.core.designsystem.components.SampleItem
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.core.model.ProfileActions
import com.ntg.feature.profile.R


@Composable
fun ProfileRoute(
    profileActions: (action: ProfileActions) -> Unit
) {
    ProfileScreen(
        profileActions = profileActions
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profileActions: (action: ProfileActions) -> Unit
) {

    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val pInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val versionName = pInfo.versionName
    val items = listOf(
        ProfileItemsData(stringResource(id = R.string.ActiveSessions), BudgetIcons.shield, null, ProfileActions.SESSIONS),
        ProfileItemsData(stringResource(id = R.string.AppUi), BudgetIcons.paintBucket, null, ProfileActions.APP_UI),
        ProfileItemsData(stringResource(id = R.string.Notifications), BudgetIcons.notification, null, ProfileActions.NOTIFICATIONS),
        ProfileItemsData(stringResource(id = R.string.BankMessages), BudgetIcons.bankMessages, null, ProfileActions.BANK_MESSAGE),
        ProfileItemsData(stringResource(id = R.string.Update), BudgetIcons.update, "نسخه ی 2 به تازگی منتشر شده", ProfileActions.UPDATES),
        ProfileItemsData(stringResource(id = R.string.AboutApp), BudgetIcons.help, null, ProfileActions.ABOUT_APP),
        ProfileItemsData(stringResource(id = R.string.RulesAndRegulations), BudgetIcons.rules, null, ProfileActions.RULES_REGULATIONS),
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppBar(
                enableNavigation = false,
                title = stringResource(id = R.string.UserAccount),
                scrollBehavior = scrollBehavior
            )
        },

    ) {
        LazyColumn(
            modifier = Modifier.padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                ProfileCell(
                    "UserMail@mail.com", //TODO: mahdi
                    "نیلا ایمانی",
                    "https://imgcdn.stablediffusionweb.com/2024/2/28/32f87d26-3cd0-4733-8f86-38a81c8e0f3a.jpg"
                ) {
                    profileActions.invoke(ProfileActions.CHANGE_INFO)
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    thickness = 2.dp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            itemsIndexed(items) { index, it ->
                SampleItem(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 5.dp),
                    title = it.text,
                    iconPainter = painterResource(id = it.icon),
                    iconTint = MaterialTheme.colorScheme.outline,
                    imagePainter = painterResource(id = BudgetIcons.directionRight),
                    secondText = it.secondText,
                    secondIconPainter = painterResource(id = BudgetIcons.directionLeft)
                ) {
                    profileActions.invoke(it.action)
                }

                if (index != items.size - 1) {

                    HorizontalDivider(
                        modifier = Modifier.padding(
                            horizontal = if (index == 3) 0.dp else 24.dp,
                            vertical = if (index == 3) 8.dp else 0.dp
                        ),
                        thickness = if (index == 3) 8.dp else 1.dp,
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    )
                }
            }

            item {
                Text(
                    text = stringResource(id = R.string.AppVertion, versionName),
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.outline)
                )
            }
        }
    }
}

data class ProfileItemsData(
    val text: String,
    val icon: Int,
    val secondText: String?,
    val action: ProfileActions
)