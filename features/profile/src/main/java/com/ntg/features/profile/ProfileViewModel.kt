package com.ntg.features.profile

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.core.data.repository.ProfileRepository
import com.ntg.core.model.res.CodeVerification
import com.ntg.core.model.res.UserInfo
import com.ntg.core.network.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _userInfoState = MutableStateFlow<Result<UserInfo>>(Result.Loading(false))
    val userInfoState: StateFlow<Result<UserInfo>> = _userInfoState


//    val userInfo = mutableStateOf<Flow<Result<UserInfo>>>(Result<UserInfo>())

    init {
        getUser()
    }

    private fun getUser() {
        viewModelScope.launch {
            profileRepository.getUserInfo().collect { res ->
                _userInfoState.value = res
            }
        }
    }
}