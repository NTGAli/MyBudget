package com.ntg.features.setup

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.core.data.repository.AccountRepository
import com.ntg.core.data.repository.BankCardRepository
import com.ntg.core.data.repository.ConfigRepository
import com.ntg.core.data.repository.CurrencyRepository
import com.ntg.core.data.repository.SourceExpenditureRepository
import com.ntg.core.data.repository.UserDataRepository
import com.ntg.core.data.repository.api.AuthRepository
import com.ntg.core.data.repository.transaction.TransactionsRepository
import com.ntg.core.model.Account
import com.ntg.core.model.SourceExpenditure
import com.ntg.core.model.SourceType
import com.ntg.core.model.Transaction
import com.ntg.core.model.res.Bank
import com.ntg.core.model.res.Currency
import com.ntg.core.model.res.ServerAccount
import com.ntg.core.model.res.ServerConfig
import com.ntg.core.model.res.WalletType
import com.ntg.core.mybudget.common.Constants
import com.ntg.core.network.model.Result
import com.ntg.mybudget.sync.work.workers.SyncData
import com.ntg.mybudget.sync.work.workers.initializers.Sync
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupViewModel
@Inject constructor(
    private val accountRepository: AccountRepository,
    private val sourceRepository: SourceExpenditureRepository,
    private val authRepository: AuthRepository,
    private val userDataRepository: UserDataRepository,
    private val bankCardRepository: BankCardRepository,
    private val transactionRepository: TransactionsRepository,
    private val configRepository: ConfigRepository,
    private val currencyRepository: CurrencyRepository,
    private val syncData: SyncData,
) : ViewModel() {

    val homeUiState = MutableStateFlow(SetupUiState.Loading)

    var selectedCurrency = flowOf<Currency?>(null)

    fun accounts() = accountRepository.getAll()

    fun getAccount(id: Int) = accountRepository.getAccountByAccount(id)

    fun accountWithSources() = accountRepository.getAccountsWithSources()

    fun getSourcesById(id: Int) = sourceRepository.getSourceDetails(id)

    private val _serverAccounts = MutableStateFlow<Result<List<ServerAccount>?>>(Result.Loading(false))
    val serverAccounts: StateFlow<Result<List<ServerAccount>?>> = _serverAccounts

    private val _walletTypes = MutableStateFlow<List<WalletType>?>(emptyList())
    val walletTypes: StateFlow<List<WalletType>?> = _walletTypes

    private val _localUserBanks = MutableStateFlow<List<Bank>?>(emptyList())
    val localUserBans: StateFlow<List<Bank>?> = _localUserBanks

    private val _currencies = MutableStateFlow<List<Currency>?>(emptyList())

    fun upsertAccount(account: Account, context: Context? = null) {
        viewModelScope.launch {
            account.dateModified = System.currentTimeMillis().toString()
            if (account.dateCreated.orEmpty().isEmpty()){
                account.dateCreated = System.currentTimeMillis().toString()
            }
            account.isSynced = false
            accountRepository.update(account)
        }

        if (context != null) {
            Sync.initialize(context = context)
        }
    }

    fun updateAccount(account: Account){
        viewModelScope.launch {
            account.dateModified = System.currentTimeMillis().toString()
            account.dateCreated = System.currentTimeMillis().toString()
            accountRepository.update(account)
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


    fun insertNewSource(
        source: SourceExpenditure
    ){
        viewModelScope.launch {
            sourceRepository.insert(source)
        }
    }

    fun insertNewBankCard(
        bankCard: SourceType.BankCard
    ){
        viewModelScope.launch {
            bankCardRepository.insert(bankCard)
        }
    }

    fun updateBankCard(bankCard: SourceType.BankCard) {
        viewModelScope.launch {
            bankCardRepository.update(bankCard)
        }
    }

    fun tempRemove(sourceId: Int) {
        viewModelScope.launch {
            sourceRepository.tempRemove(sourceId)
        }
    }

    fun initCardTransactions(
        initAmount: Long,
        sourceId: Int,
        accountId: Int
    ){
        viewModelScope.launch {
            transactionRepository.insertNewTransaction(
                Transaction(
                    0, amount = initAmount, accountId = accountId, sourceId = sourceId, date = System.currentTimeMillis()
                )
            )
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

    fun setDefaultAccount(accountId: String, dateCreated: String = System.currentTimeMillis().toString()){
        viewModelScope.launch {
            accountRepository.insert(
                Account(
                    id = 0,
                    sId = accountId,
                    name = "حساب شخصی",
                    isSynced = true,
                    isDefault = true,
                    dateCreated = dateCreated
                )
            )
        }
    }

    fun logout(){
        viewModelScope.launch {
            userDataRepository.logout()
        }
    }

    fun sync(context: Context){
        syncData.sync(context)
    }

    fun walletTypes(): MutableStateFlow<List<WalletType>?> {
        viewModelScope.launch {
            accountRepository.walletTypes().collect{
                _walletTypes.value = it
            }
        }
        return _walletTypes
    }

    fun deleteAccount(accountId: Int, context: Context? = null){
        viewModelScope.launch {
            accountRepository.delete(accountId)
        }
        if (context != null) {
            Sync.initialize(context = context)
        }
    }

    fun getLocalUserBanks(): MutableStateFlow<List<Bank>?> {
        viewModelScope.launch {
            bankCardRepository.getUserLocalBanks().collect{
                _localUserBanks.value = it
            }
        }
        return _localUserBanks
    }

    fun getBankLogoMono(): Flow<ServerConfig?> {
        return configRepository.get(Constants.Configs.BANK_LOGO_MONO_URL)
    }

    fun getBankLogoColor(): Flow<ServerConfig?> {
        return configRepository.get(Constants.Configs.BANK_LOGO_COLOR_URL)

    }

    fun loadCurrencies(): MutableStateFlow<List<Currency>?> {
        viewModelScope.launch {
            currencyRepository.upsert()
            currencyRepository.getCurrencies().collect{
                _currencies.value = it
            }
        }
        return _currencies
    }

}

enum class SetupUiState {
    Loading,
    Error,
    Success
}