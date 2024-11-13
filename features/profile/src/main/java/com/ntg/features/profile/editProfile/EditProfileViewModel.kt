package com.ntg.features.profile.editProfile

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.core.data.repository.ProfileRepository
import com.ntg.core.data.repository.UserDataRepository
import com.ntg.core.model.UserData
import com.ntg.core.model.res.UploadAvatarRes
import com.ntg.core.model.res.UserInfo
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

    var selectedAvatarImage by mutableStateOf<Bitmap?>(null)

    private val _uploadState = MutableStateFlow<Result<UploadAvatarRes>>(Result.Loading(false))
    val uploadState: StateFlow<Result<UploadAvatarRes>> = _uploadState

    fun uploadImage(image: Bitmap, mimeType: String) {

        viewModelScope.launch {
            selectedAvatarImage = image

            profileRepository.uploadAvatar(image, mimeType).collect { res ->
                _uploadState.value = res
            }
        }
    }

    fun updateUserInfo(
        userInfo: UserData
    ) {
        viewModelScope.launch {
            userDataRepository.saveUserBasicData(
                name = userInfo.name ?: "",
                email = userInfo.email ?: "",
                phone = userInfo.phone ?: "",
                image = userInfo.avatarImage ?: "",
            )
        }
    }
}