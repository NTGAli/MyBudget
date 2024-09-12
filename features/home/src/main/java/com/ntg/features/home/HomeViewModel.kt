package com.ntg.features.home

import androidx.lifecycle.ViewModel
import com.ntg.core.data.repository.AccountRepository
import com.ntg.core.data.repository.transaction.TransactionsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transactionsRepository: TransactionsRepository
): ViewModel() {

    fun selectedAccount() = accountRepository.getSelectedAccount()

    fun transactions(sourceIds: List<Int>) = transactionsRepository.getTransactionsBySourceIds(sourceIds)

}