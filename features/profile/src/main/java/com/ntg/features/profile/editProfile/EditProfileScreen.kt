package com.ntg.features.profile.editProfile

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.ntg.core.designsystem.components.AppBar
import com.ntg.core.designsystem.components.BudgetTextField
import com.ntg.core.designsystem.components.EditAvatarImage
import com.ntg.core.model.UserData
import com.ntg.core.mybudget.common.LoginEventListener
import com.ntg.core.mybudget.common.SharedViewModel
import com.ntg.core.mybudget.common.orDefault
import com.ntg.core.network.model.Result
import com.ntg.feature.profile.R
import kotlinx.coroutines.launch

@Composable
fun EditeProfileRout(
    sharedViewModel: SharedViewModel,
    editProfileViewModel: EditProfileViewModel = hiltViewModel(),
    onShowSnackbar: suspend (Int, String?, Int?) -> Boolean,
) {

    sharedViewModel.setExpand.postValue(true)
    sharedViewModel.bottomNavTitle.postValue(stringResource(id = R.string.Submit))

    val lifecycleOwner = LocalLifecycleOwner.current
    val currentLifecycleState = lifecycleOwner.lifecycle
    val scope = rememberCoroutineScope()

    val userInfo = editProfileViewModel.userData.collectAsStateWithLifecycle(null)
    val userName = remember(userInfo.value?.name) { mutableStateOf(userInfo.value?.name.orDefault()) }
    val userEmail = remember(userInfo.value?.email) { mutableStateOf(userInfo.value?.email.orDefault()) }

    EditeProfileScreen(
        userInfo,
        userName = userName,
        userEmail = userEmail,
        onImageSelected = { image, mimeType ->
            editProfileViewModel.uploadImage(image, mimeType)
        },
    )

    LaunchedEffect(userName.value, userEmail.value) {
        editProfileViewModel.name.value = userName.value
        editProfileViewModel.email.value = userEmail.value
    }

    LaunchedEffect(Unit) {

        sharedViewModel.loginEventListener = object : LoginEventListener {
            override fun onBottomButtonClick() {
                editProfileViewModel.updateServerUserInfo()
            }
        }
    }

    LaunchedEffect(Unit) {
        editProfileViewModel.uploadAvatarState
            .flowWithLifecycle(currentLifecycleState, Lifecycle.State.STARTED)
            .collect {
                when (it) {
                    is Result.Success -> {
                        sharedViewModel.setLoading.postValue(false)
                        scope.launch {
                            onShowSnackbar(R.string.ImageUploaded, null, null)
                        }

                        userInfo.value?.let { userInfo ->
                            editProfileViewModel.updateLocalUserInfo(userInfo.copy(avatarImage = it.data.avatar_url))
                        }
                    }

                    is Result.Loading -> {
                        if (it.loading){
                            sharedViewModel.setLoading.postValue(true)
                        }
                    }

                    is Result.Error -> {
                        sharedViewModel.setLoading.postValue(false)
                        onShowSnackbar(R.string.ImageUploadedFailed, null, null)
                    }
                }
            }
    }

    LaunchedEffect(Unit) {
        editProfileViewModel.updateInfoState
            .flowWithLifecycle(currentLifecycleState, Lifecycle.State.STARTED)
            .collect {
                when (it) {
                    is Result.Success -> {
                        sharedViewModel.setLoading.postValue(false)
                        scope.launch {
                            onShowSnackbar(R.string.UserInfoUpdated, null, null)
                        }

                        userInfo.value?.let { userInfo ->
                            editProfileViewModel.updateLocalUserInfo(
                                userInfo.copy(name = editProfileViewModel.name.value)
                            )
                        }
                    }

                    is Result.Loading -> {
                        if (it.loading){
                            sharedViewModel.setLoading.postValue(true)
                        }
                    }

                    is Result.Error -> {
                        sharedViewModel.setLoading.postValue(false)
                        onShowSnackbar(R.string.UpdateUserInfoFailed, null, null)
                    }
                }
            }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditeProfileScreen(
    userInfo: State<UserData?>,
    userName: MutableState<String>,
    userEmail: MutableState<String>,
    onImageSelected: (Bitmap, String) -> Unit,
) {

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val userPhone = remember(userInfo.value?.phone) { mutableStateOf(userInfo.value?.phone.orDefault()) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppBar(
                enableNavigation = false,
                title = stringResource(id = R.string.UserProfile),
                scrollBehavior = scrollBehavior
            )
        },
        ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            EditAvatarImage(userInfo.value?.avatarImage.orDefault(), onImageSelected)

            BudgetTextField(
                text = userName,
                label = stringResource(id = R.string.Name),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp),
                enabled = true
            )

            BudgetTextField(
                text = userPhone,
                label = stringResource(id = R.string.PhoneNumber),
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 8.dp)
            )

            BudgetTextField(
                text = userEmail,
                label = stringResource(id = R.string.Email),
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 8.dp)
            )
        }
    }
}

