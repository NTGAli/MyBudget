package com.ntg.features.profile.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.core.data.repository.ProfileRepository
import com.ntg.core.data.repository.UserDataRepository
import com.ntg.core.model.res.UserInfo
import com.ntg.core.mybudget.common.orDefault
import com.ntg.core.network.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val userDataRepository: UserDataRepository
) : ViewModel() {

    val userData = userDataRepository.userData

    init {
        getUser()
    }

    fun getUserInfoFlow(): Flow<UserInfo> {
        return userData.map { userData ->
            UserInfo(
                id = userData.token ?: "Unknown",
                full_name = userData.name,
                email = userData.email,
                avatar_url = userData.avatarImage ?: "",
                phone = userData.phone ?: ""
            )
        }
    }

    private fun getUser() {
        viewModelScope.launch {
            profileRepository.getUserInfo().collect { res ->

                when (res) {
                    is Result.Success -> {
                        saveNameEmail(
                            name = res.data.full_name.orDefault(),
                            email = res.data.email ?: "",
                            phone = res.data.phone ?: "",
                            image = res.data.avatar_url ?: "",
                        )
                    }

                    is Result.Loading -> {}

                    is Result.Error -> {
                        saveNameEmail(
                            name = "نام کاربر",
                            email = "ایمیل کاربر",
                            phone = "شماره تلفن کاربر",
                            image = "error",
                        )
                    }
                }
            }
        }
    }


    private fun saveNameEmail(
        name: String,
        email: String,
        phone: String,
        image: String
    ){
        viewModelScope.launch {
            userDataRepository.saveUserBasicData(name, email,phone, image)
        }
    }
}