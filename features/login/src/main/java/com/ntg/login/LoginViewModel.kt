package com.ntg.login

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.core.data.repository.AccountRepository
import com.ntg.core.data.repository.WalletsRepository
import com.ntg.core.data.repository.UserDataRepository
import com.ntg.core.data.repository.api.AuthRepository
import com.ntg.core.model.Account
import com.ntg.core.model.Wallet
import com.ntg.core.model.req.VerifyOtp
import com.ntg.core.model.res.CodeVerification
import com.ntg.core.model.res.ServerAccount
import com.ntg.core.mybudget.common.BudgetDispatchers
import com.ntg.core.mybudget.common.Constants
import com.ntg.core.mybudget.common.Dispatcher
import com.ntg.core.network.model.ResponseBody
import com.ntg.core.network.model.Result
import com.ntg.mybudget.sync.work.workers.initializers.Sync
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
        private val accountRepository: AccountRepository,
//        private val bankCardRepository: BankCardRepository,
        private val sourceRepository: WalletsRepository,
        @Dispatcher(BudgetDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    ) : ViewModel() {

    val loginUiState = MutableStateFlow(LoginUiState.Success)

    var countrySelected : MutableLiveData<String> = MutableLiveData()
    private val _sendCodeState = MutableStateFlow<Result<ResponseBody<String?>>>(Result.Loading(false))
    val sendCodeState: StateFlow<Result<ResponseBody<String?>>> = _sendCodeState

    private val _codeVerificationState = MutableStateFlow<Result<CodeVerification?>>(Result.Loading(false))
    val codeVerificationState: StateFlow<Result<CodeVerification?>> = _codeVerificationState

    private val _serverAccounts = MutableStateFlow<Result<List<ServerAccount>?>>(Result.Loading(false))
    val serverAccounts: StateFlow<Result<List<ServerAccount>?>> = _serverAccounts

    fun saveUserLogin(
        token: String, expire: String
    ){
        viewModelScope.launch {
            sharedPreferences.edit().putString(Constants.Prefs.ACCESS_TOKEN, token).apply()
            userDataRepository.setUserLogged(token, expire)
        }
    }

    fun logout(){
        viewModelScope.launch {
            sharedPreferences.edit().clear().apply()
            userDataRepository.logout()
        }
    }

    fun sendCode(phone: String) {
        viewModelScope.launch(ioDispatcher) {
            if (phone.isNotEmpty()){
                _sendCodeState.value = Result.Loading(true)
                authRepository.sendCode(phone)
                    .collect { result ->
                        _sendCodeState.value =
                            result
                    }
            }else{
                _sendCodeState.value = Result.Loading(false)
            }
        }
    }


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

    fun serverAccounts(){
        viewModelScope.launch {
            _serverAccounts.value = Result.Loading(true)
            authRepository.serverAccounts()
                .collect { result ->
                    _serverAccounts.value =
                        result
                }
        }
    }



    fun insertNewSource(
        source: Wallet
    ){
        viewModelScope.launch {
            sourceRepository.insert(source)
        }
    }


    fun insertNewAccount(
        account: Account,
        context: Context? = null
    ){
        viewModelScope.launch {
            if (account.dateCreated.orEmpty().isEmpty()){
                account.dateCreated = System.currentTimeMillis().toString()
                account.dateModified = System.currentTimeMillis().toString()
            }else{
                account.dateModified = System.currentTimeMillis().toString()
            }
            accountRepository.insert(account)
        }
        if (context != null){
            Sync.initialize(context = context)
        }
    }

}

enum class LoginUiState {
    Loading,
    Error,
    Success
}