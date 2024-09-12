package com.ntg.login

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.core.data.repository.UserDataRepository
import com.ntg.core.data.repository.api.AuthRepository
import com.ntg.core.model.req.VerifyOtp
import com.ntg.core.model.res.CodeVerification
import com.ntg.core.mybudget.common.BudgetDispatchers
import com.ntg.core.mybudget.common.Constants
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
        private val userDataRepository: UserDataRepository,
        private val authRepository: AuthRepository,
        private val sharedPreferences: SharedPreferences,
        @Dispatcher(BudgetDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    ) : ViewModel() {

    var countrySelected : MutableLiveData<String> = MutableLiveData()
    private val _sendCodeState = MutableStateFlow<Result<ResponseBody<String?>>>(Result.Loading(false))
    val sendCodeState: StateFlow<Result<ResponseBody<String?>>> = _sendCodeState

    private val _codeVerificationState = MutableStateFlow<Result<CodeVerification?>>(Result.Loading(false))
    val codeVerificationState: StateFlow<Result<CodeVerification?>> = _codeVerificationState

    fun saveUserLogin(
        token: String, expire: String
    ){
        viewModelScope.launch {
            sharedPreferences.edit().putString(Constants.Prefs.ACCESS_TOKEN, token).apply()
            userDataRepository.setUserLogged(token, expire)
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

    fun verifyCode(verification: VerifyOtp){
        viewModelScope.launch {
            _codeVerificationState.value = Result.Loading(true)
            authRepository.verifyCode(verification)
                .collect { result ->
                    _codeVerificationState.value =
                        result
                }
        }
    }

}