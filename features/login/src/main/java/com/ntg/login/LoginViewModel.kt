package com.ntg.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.core.data.repository.AccountRepository
import com.ntg.core.data.repository.UserDataRepository
import com.ntg.core.model.Account
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject constructor(
        private val accountRepository: AccountRepository,
        private val userDataRepository: UserDataRepository
    )
    : ViewModel() {

    var countrySelected : MutableLiveData<String> = MutableLiveData()

    fun setDefaultAccount(){
        viewModelScope.launch {
            accountRepository.insert(
                Account(
                    id = 0,
                    sId = null,
                    name = "حساب شخصی",
                    isSelected = true,
                    dateCreated = System.currentTimeMillis().toString()
                )
            )
        }
    }

    fun saveUserLogin(){
        viewModelScope.launch {
            userDataRepository.setUserLogged()
        }
    }

}