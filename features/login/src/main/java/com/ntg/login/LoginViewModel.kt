package com.ntg.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.core.data.repository.AccountRepository
import com.ntg.core.data.repository.UserDataRepository
import com.ntg.core.data.repository.api.AuthRepository
import com.ntg.core.model.Account
import com.ntg.core.mybudget.common.BudgetDispatchers
import com.ntg.core.mybudget.common.Dispatcher
import com.ntg.core.network.model.ResponseBody
import com.ntg.core.network.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject constructor(
        private val accountRepository: AccountRepository,
        private val userDataRepository: UserDataRepository,
        private val authRepository: AuthRepository,
        @Dispatcher(BudgetDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    ) : ViewModel() {

    var countrySelected : MutableLiveData<String> = MutableLiveData()
    private val _sendCodeState = MutableStateFlow<Result<ResponseBody<String?>>>(Result.Loading(false))
    val sendCodeState: StateFlow<Result<ResponseBody<String?>>> = _sendCodeState

    fun setDefaultAccount(){
        viewModelScope.launch {
            accountRepository.insert(
                Account(
                    id = 0,
                    sId = null,
                    name = "حساب شخصی",
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

    fun sendCode(phone: String) {
        viewModelScope.launch(ioDispatcher) {
            _sendCodeState.value = Result.Loading(true)
            authRepository.sendCode(phone)
                .collect { result ->
                    _sendCodeState.value =
                        result
                }
        }
    }

}