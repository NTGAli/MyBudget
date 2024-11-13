package com.ntg.features.profile.editProfile

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.core.data.repository.ProfileRepository
import com.ntg.core.data.repository.UserDataRepository
import com.ntg.core.model.UserData
import com.ntg.core.model.res.UploadAvatarRes
import com.ntg.core.mybudget.common.orDefault
import com.ntg.core.network.model.ResponseBody
import com.ntg.core.network.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val userDataRepository: UserDataRepository
) : ViewModel() {

    val userData = userDataRepository.userData
    val name = mutableStateOf("")
    val email = mutableStateOf("")

    private val _uploadAvatarState = MutableStateFlow<Result<UploadAvatarRes>>(Result.Loading(false))
    val uploadAvatarState: StateFlow<Result<UploadAvatarRes>> = _uploadAvatarState

    private val _updateInfoState = MutableStateFlow<Result<ResponseBody<String?>>>(Result.Loading(false))
    val updateInfoState: StateFlow<Result<ResponseBody<String?>>> = _updateInfoState

    fun uploadImage(image: Bitmap, mimeType: String) {

        viewModelScope.launch {
            profileRepository.uploadAvatar(image, mimeType).collect { res ->
                _uploadAvatarState.value = res
            }
        }
    }

    fun updateServerUserInfo() {
        viewModelScope.launch {
            var username = email
            if (username.value.isBlank() || username.value.isEmpty()) username.value = "Empty"

            profileRepository.updateUserInfo(name.value, username.value).collect { res ->
                _updateInfoState.value = res
            }
        }
    }

    fun updateLocalUserInfo(
        userInfo: UserData
    ) {
        viewModelScope.launch {
            userDataRepository.saveUserBasicData(
                name = userInfo.name.orDefault(),
                email = userInfo.email.orDefault(),
                phone = userInfo.phone.orDefault(),
                image = userInfo.avatarImage.orDefault(),
            )
        }
    }
}