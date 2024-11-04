package com.ntg.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.core.data.repository.AccountRepository
import com.ntg.core.data.repository.CategoryRepository
import com.ntg.core.data.repository.ConfigRepository
import com.ntg.core.data.repository.SourceExpenditureRepository
import com.ntg.core.data.repository.transaction.TransactionsRepository
import com.ntg.core.model.SourceWithDetail
import com.ntg.core.model.Transaction
import com.ntg.core.model.res.Category
import com.ntg.core.model.res.ServerConfig
import com.ntg.core.model.res.WalletType
import com.ntg.core.mybudget.common.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val sourceRepository: SourceExpenditureRepository,
    private val transactionsRepository: TransactionsRepository,
    private val categoryRepository: CategoryRepository,
    private val configRepository: ConfigRepository
): ViewModel() {

    private val _walletTypes = MutableStateFlow<List<SourceWithDetail>?>(emptyList())
    private val _categories = MutableStateFlow<List<Category>?>(emptyList())

    fun selectedAccount() = accountRepository.getSelectedAccount()

    fun selectedSources(): MutableStateFlow<List<SourceWithDetail>?> {
        viewModelScope.launch {
            sourceRepository.getSelectedSources().collect{
                _walletTypes.value = it
            }
        }
        return _walletTypes
    }

    fun transactions(sourceIds: List<Int>) = transactionsRepository.getTransactionsBySourceIds(sourceIds)

    fun accountWithSources() = accountRepository.getAccountsWithSources()

    fun updatedSelectedAccount(accountId: Int) {
        viewModelScope.launch {
            accountRepository.updateSelectedAccountAndSources(accountId)
        }
    }

    fun updatedSelectedSources(sourceIds: List<Int>) {
        viewModelScope.launch {
            sourceRepository.updateSelectedSources(sourceIds)
        }
    }

    fun getBankLogoColor(): Flow<ServerConfig?> {
        return configRepository.get(Constants.Configs.BANK_LOGO_COLOR_URL)
    }

    fun getCategories(): MutableStateFlow<List<Category>?> {
        viewModelScope.launch {
            categoryRepository.getCategories().collect{
                _categories.value = it
            }
        }
        return _categories
    }

    fun insertTransaction(transaction: Transaction){
        viewModelScope.launch {
            transactionsRepository.insertNewTransaction(transaction)
        }
    }
}