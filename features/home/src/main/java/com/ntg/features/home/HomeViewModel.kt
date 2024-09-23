package com.ntg.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.core.data.repository.AccountRepository
import com.ntg.core.data.repository.SourceExpenditureRepository
import com.ntg.core.data.repository.transaction.TransactionsRepository
import com.ntg.core.model.SourceWithDetail
import com.ntg.core.model.res.WalletType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val sourceRepository: SourceExpenditureRepository,
    private val transactionsRepository: TransactionsRepository
): ViewModel() {

    private val _walletTypes = MutableStateFlow<List<SourceWithDetail>?>(emptyList())

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
}